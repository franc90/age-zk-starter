package org.age.zk.services.discovery;

import com.google.common.collect.Sets;
import org.age.zk.services.AbstractService;
import org.age.zk.services.discovery.watcher.LifecycleWatcher;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DiscoveryServiceImpl extends AbstractService implements DiscoveryService {

    private static final Logger log = LoggerFactory.getLogger(DiscoveryServiceImpl.class);

    private final LifecycleWatcher lifecycleWatcher;

    @Autowired
    public DiscoveryServiceImpl(LifecycleWatcher lifecycleWatcher) {
        this.lifecycleWatcher = lifecycleWatcher;
    }

    @Override
    public void start() {
        log.debug("Starting discovery service");
        running.set(true);

        try {
            createServiceNode(DiscoveryConsts.DISCOVERY_NODE_PATH);
            createMemberNode();
        } catch (Exception e) {
            log.error("Error while starting discovery service", e);
        }

        zookeeperService.setWatcher(DiscoveryConsts.DISCOVERY_NODE_PATH, lifecycleWatcher);
        log.debug("Discovery service started");
    }

    private void createMemberNode() throws Exception {
        String nodeUUID = identityService.getNodeUUID();
        String nodeId = zookeeperService.getClient()
                .create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath(DiscoveryConsts.DISCOVERY_MEMBER_PATH, nodeUUID.getBytes());

        String id = nodeId.substring(nodeId.lastIndexOf("/") + 1);
        identityService.setNodeId(id);

        log.info("Created member node {}", nodeId);
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public void stop() {
        log.debug("Stopping discovery service");
        running.set(false);
        log.debug("Discovery service stopped");
    }

    @Override
    public int getPhase() {
        return Integer.MIN_VALUE + 1;
    }

    @Override
    public Set<String> getAllMembers() {
        return Sets.newHashSet(zookeeperService.getChildren(DiscoveryConsts.DISCOVERY_NODE_PATH));
    }

    @Override
    public int getNodesCount() {
        Set<String> allMembers = getAllMembers();
        return allMembers.size();
    }
}
