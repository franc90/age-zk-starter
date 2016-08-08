package org.age.zk.services.topology;

import com.google.common.eventbus.Subscribe;
import org.age.zk.services.AbstractService;
import org.age.zk.services.discovery.DiscoveryConsts;
import org.age.zk.services.discovery.watcher.events.MembersUpdatedEvent;
import org.age.zk.services.leadership.LeadershipService;
import org.age.zk.services.topology.creator.TopologyAssembler;
import org.age.zk.services.topology.creator.TopologyCreator;
import org.age.zk.utils.graph.Graph;
import org.age.zk.services.topology.watcher.TopologyWatcher;
import org.age.zk.services.topology.watcher.events.TopologyUpdatedEvent;
import org.age.zk.utils.ZookeeperUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TopologyServiceImpl extends AbstractService implements TopologyService {

    private static final Logger log = LoggerFactory.getLogger(TopologyServiceImpl.class);

    @Autowired
    private LeadershipService leadershipService;

    @Autowired
    private TopologyCreator topologyCreator;

    @Autowired
    private TopologyWatcher topologyWatcher;

    @Autowired
    private ZookeeperUtils zookeeperUtils;

    private DirectedGraph<String, DefaultEdge> currentTopology;

    @Override
    public void start() {
        log.debug("Start topology service");
        running.set(true);

        try {
            createTopologyNode();
        } catch (Exception e) {
            log.error("Error while starting discovery service {}", DiscoveryConsts.DISCOVERY_NODE_PATH);
        }
        zookeeperUtils.setWatcher(TopologyConst.TOPOLOGY_NODE_PATH, topologyWatcher);

        log.debug("Topology started");
    }

    private void createTopologyNode() throws Exception {
        while (!lifecycleService.isAlive()) {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        CuratorFramework client = lifecycleService.getClient();
        Stat discoveryNodeStat = client
                .checkExists()
                .forPath(TopologyConst.TOPOLOGY_NODE_PATH);

        if (discoveryNodeStat == null) {
            client
                    .create()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(TopologyConst.TOPOLOGY_NODE_PATH);
        } else {
            updateTopology(null);
        }
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

    @Subscribe
    public void memberShipUpdated(MembersUpdatedEvent membersUpdatedEvent) {
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
        zookeeperUtils.killChildren(TopologyConst.TOPOLOGY_NODE_PATH);
        zookeeperUtils.createGraph(TopologyConst.TOPOLOGY_NODE_PATH, topologyGraph);
        log.debug("New Topology saved");
    }

    @Subscribe
    public void updateTopology(TopologyUpdatedEvent event) {
        log.debug("Update topology");

        Graph topologyGraph = zookeeperUtils.getGraph(TopologyConst.TOPOLOGY_NODE_PATH);
        currentTopology = TopologyAssembler.convert(topologyGraph);

        log.debug("Current topology updated to {}", currentTopology);
    }
}
