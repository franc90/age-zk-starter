package org.age.zk.services.topology;

import com.google.common.eventbus.Subscribe;
import org.age.zk.services.AbstractService;
import org.age.zk.services.communication.message.Message;
import org.age.zk.services.communication.watcher.events.SendMessageEvent;
import org.age.zk.services.discovery.watcher.events.MembersUpdatedEvent;
import org.age.zk.services.leadership.LeadershipService;
import org.age.zk.services.topology.creator.TopologyAssembler;
import org.age.zk.services.topology.creator.TopologyCreator;
import org.age.zk.services.topology.watcher.TopologyWatcher;
import org.age.zk.services.topology.watcher.events.TopologyUpdatedEvent;
import org.age.zk.utils.graph.Graph;
import org.apache.commons.collections.CollectionUtils;
import org.apache.zookeeper.CreateMode;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class TopologyServiceImpl extends AbstractService implements TopologyService {

    private static final Logger log = LoggerFactory.getLogger(TopologyServiceImpl.class);

    private final LeadershipService leadershipService;

    private final TopologyCreator topologyCreator;

    private final TopologyWatcher topologyWatcher;

    private DirectedGraph<String, DefaultEdge> currentTopology;

    @Autowired
    public TopologyServiceImpl(TopologyWatcher topologyWatcher,
                               TopologyCreator topologyCreator,
                               LeadershipService leadershipService) {
        this.topologyWatcher = topologyWatcher;
        this.topologyCreator = topologyCreator;
        this.leadershipService = leadershipService;
    }

    @Override
    public void start() {
        log.debug("Start topology service");
        running.set(true);

        try {
            createTopologyNode();
        } catch (Exception e) {
            log.error("Error while starting topology service service ", e);
        }
        zookeeperService.setWatcher(TopologyConst.TOPOLOGY_NODE_PATH, topologyWatcher);

        log.debug("Topology started");
    }

    private void createTopologyNode() throws Exception {
        while (!zookeeperService.isAlive()) {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        if (zookeeperService.nodeExist(TopologyConst.TOPOLOGY_NODE_PATH)) {
            updateTopology(null);
            return;
        }

        zookeeperService.getClient()
                .create()
                .withMode(CreateMode.PERSISTENT)
                .forPath(TopologyConst.TOPOLOGY_NODE_PATH);
    }

    @Override
    public void stop(Runnable callback) {
        log.debug("Stop topology service with callback");
        stop();
        callback.run();
    }

    @Override
    public void stop() {
        log.debug("Stop topology service");
        running.set(false);
        log.debug("Topology stopped");
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public DirectedGraph<String, DefaultEdge> getTopology() {
        return currentTopology;
    }

    @Override
    public Set<String> neighbors() {
        String nodeId = identityService.getNodeId();
        if (nodeId == null) {
            log.debug("No id for current node");
            return Collections.emptySet();
        }
        if (!currentTopology.vertexSet().contains(nodeId)) {
            log.debug("Current topology does not include {}", nodeId);
            return Collections.emptySet();
        }

        log.debug("Getting edges of {}", currentTopology);
        return currentTopology
                .edgesOf(nodeId)
                .stream()
                .map(currentTopology::getEdgeTarget)
                .collect(Collectors.toSet());
    }

    @Subscribe
    public void membershipUpdated(MembersUpdatedEvent membersUpdatedEvent) {
        log.debug("Membership updated: {} ", membersUpdatedEvent);

        if (!leadershipService.isMaster()) {
            log.debug("Not a master, ignoring");
            return;
        }

        DirectedGraph<String, DefaultEdge> topologyGraph = topologyCreator.createTopologyGraph();
        Graph convertedTopology = TopologyAssembler.convert(topologyGraph);
        saveTopology(convertedTopology);
    }

    private void saveTopology(Graph topologyGraph) {
        zookeeperService.replaceChildrenWithGraph(TopologyConst.TOPOLOGY_NODE_PATH, topologyGraph);
        log.debug("New Topology saved");
    }

    @Subscribe
    public void updateTopology(TopologyUpdatedEvent event) throws InterruptedException {
        log.debug("Update topology");

        Graph topologyGraph = zookeeperService.getGraph(TopologyConst.TOPOLOGY_NODE_PATH);
        currentTopology = TopologyAssembler.convert(topologyGraph);

        sendMessageToNeighbors();

        log.debug("Current topology updated to {}", currentTopology);
    }

    private void sendMessageToNeighbors() {
        Set<String> neighbors = neighbors();
        if (CollectionUtils.isEmpty(neighbors)) {
            log.debug("No Neighbors, returning");
            return;
        }

        log.debug("Sending message to neighbors");
        String senderId = identityService.getNodeId();
        String messageUUID = UUID.randomUUID().toString();
        long sendTime = System.currentTimeMillis();

        neighbors
                .stream()
                .filter(neighbor -> !senderId.equals(neighbor))
                .map(neighbor -> new Message(senderId, neighbor, messageUUID, sendTime, senderId + ": Topology updated"))
                .map(SendMessageEvent::new)
                .forEach(eventBus::post);
    }

}
