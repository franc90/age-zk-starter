package org.age.zk.services.discovery;

import com.google.common.collect.Sets;
import org.age.zk.services.AbstractService;
import org.age.zk.services.discovery.watcher.LifecycleWatcher;
import org.age.zk.utils.ZookeeperUtils;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DiscoveryServiceImpl extends AbstractService implements DiscoveryService {

    private static final Logger log = LoggerFactory.getLogger(DiscoveryServiceImpl.class);

    @Autowired
    private ZookeeperUtils zookeeperUtils;

    @Autowired
    private LifecycleWatcher lifecycleWatcher;

    @Override
    public void start() {
        log.debug("Starting discovery service");
        log.debug("Neighbours: {} ");
        running.set(true);

        try {
            createServiceNode(DiscoveryConsts.DISCOVERY_NODE_PATH);
            createMemberNode();
        } catch (Exception e) {
            log.error("Error while starting discovery service {}", DiscoveryConsts.DISCOVERY_NODE_PATH);
        }

        zookeeperUtils.setWatcher(DiscoveryConsts.DISCOVERY_NODE_PATH, lifecycleWatcher);
        log.debug("Discovery service started");
    }

    private void createMemberNode() throws Exception {
        String nodeId = identityService.getNodeId();
        String result = lifecycleService.getClient()
                .create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath(DiscoveryConsts.DISCOVERY_MEMBER_PATH, nodeId.getBytes());

        log.info("Created member node {}", result);
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public void stop() {
        log.debug("Stopping discovery service");

        // TODO cleanup
//        if (hazelcastInstance.getLifecycleService().isRunning()) {
//            cleanUp();
//        }
        running.set(false);
        log.debug("Discovery service stopped");
    }

    @Override
    public int getPhase() {
        return Integer.MIN_VALUE + 1;
    }

    @Override
    public Set<String> getAllMembers() {
        return Sets.newHashSet(zookeeperUtils.getChildren(DiscoveryConsts.DISCOVERY_NODE_PATH));
    }
}
