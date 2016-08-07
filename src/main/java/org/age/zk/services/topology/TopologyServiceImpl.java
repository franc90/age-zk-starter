package org.age.zk.services.topology;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.age.zk.services.discovery.watcher.events.MembersUpdatedEvent;
import org.age.zk.services.identity.IdentityService;
import org.age.zk.services.leadership.LeadershipService;
import org.age.zk.services.lifecycle.LifecycleService;
import org.age.zk.services.topology.creator.TopologyAssembler;
import org.age.zk.services.topology.creator.TopologyCreator;
import org.age.zk.services.topology.creator.structure.Graph;
import org.age.zk.utils.ZookeeperUtils;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TopologyServiceImpl implements SmartLifecycle, TopologyService {

    private static final Logger log = LoggerFactory.getLogger(TopologyServiceImpl.class);

    @Autowired
    private LifecycleService lifecycleService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private LeadershipService leadershipService;

    @Autowired
    private TopologyCreator topologyCreator;

    @Autowired
    private ZookeeperUtils zookeeperUtils;

    @Autowired
    private EventBus eventBus;

    @PostConstruct
    public void init() throws Exception {
        eventBus.register(this);
    }


    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void start() {
        log.debug("Start topology service");

        log.debug("Topology started");
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

        log.debug("Topology stopped");
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Subscribe
    public void memberShipUpdated(MembersUpdatedEvent membersUpdatedEvent) {
        log.debug("Membership updated: {} ", membersUpdatedEvent);

        if (!leadershipService.isMaster()) {
            log.debug("Not a master, ignoring");
        }

        DirectedGraph<String, DefaultEdge> topologyGraph = topologyCreator.createTopologyGraph();
        Graph convertedTopology = TopologyAssembler.convert(topologyGraph);
    }

    private void saveTopology(Graph topologyGraph) {
        log.debug("Clearing old topology");
        zookeeperUtils.killChildren(TopologyConst.TOPOLOGY_PATH);
        zookeeperUtils.createGraph(TopologyConst.TOPOLOGY_PATH, topologyGraph);
        log.debug("Creating new topology");
    }
}
