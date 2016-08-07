package org.age.zk.services.discovery;

import com.google.common.collect.Sets;
import org.age.zk.services.discovery.watcher.LifecycleWatcher;
import org.age.zk.services.identity.IdentityService;
import org.age.zk.services.lifecycle.LifecycleService;
import org.age.zk.utils.ZookeeperUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class DiscoveryServiceImpl implements SmartLifecycle, DiscoveryService {

    private static final Logger log = LoggerFactory.getLogger(DiscoveryServiceImpl.class);

    private final AtomicBoolean running = new AtomicBoolean(false);

    @Autowired
    private IdentityService identityService;

    @Autowired
    private LifecycleService lifecycleService;

    @Autowired
    private ZookeeperUtils zookeeperUtils;

    @Autowired
    private LifecycleWatcher lifecycleWatcher;

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void start() {
        log.debug("Starting discovery service");
        log.debug("Neighbours: {} ");
        running.set(true);

        try {
            createDiscoveryNode();
            createMemberNode();
        } catch (Exception e) {
            log.error("Error while starting discovery service {}", DiscoveryConsts.DISCOVERY_NODE_PATH);
        }
        zookeeperUtils.setWatcher(DiscoveryConsts.DISCOVERY_NODE_PATH, lifecycleWatcher);
        log.debug("Discovery service started");
    }

    private void createDiscoveryNode() throws Exception {
        while (!lifecycleService.isAlive()) {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        CuratorFramework client = lifecycleService.getClient();
        Stat topologyStat = client
                .checkExists()
                .forPath(DiscoveryConsts.DISCOVERY_NODE_PATH);

        if (topologyStat == null) {
            client
                    .create()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(DiscoveryConsts.DISCOVERY_NODE_PATH);
        }
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
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public int getPhase() {
        return Integer.MIN_VALUE + 1;
    }

    @Override
    public Set<String> getAllMembers() {
        return Sets.newHashSet(zookeeperUtils.getChildren(DiscoveryConsts.DISCOVERY));
    }
}
