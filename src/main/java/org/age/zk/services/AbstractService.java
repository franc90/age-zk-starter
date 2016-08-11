package org.age.zk.services;

import com.google.common.eventbus.EventBus;
import org.age.zk.services.identity.IdentityService;
import org.age.zk.services.zookeeper.ZookeeperService;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractService implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(AbstractService.class);

    protected final AtomicBoolean running = new AtomicBoolean(false);

    @Autowired
    protected IdentityService identityService;

    @Autowired
    protected ZookeeperService zookeeperService;

    @Autowired
    protected EventBus eventBus;

    @PostConstruct
    public void init() throws Exception {
        eventBus.register(this);
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    protected void createServiceNode(String nodePath) throws Exception {
        while (!zookeeperService.isAlive()) {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        if (zookeeperService.nodeNotExist(nodePath)) {
            zookeeperService
                    .getClient()
                    .create()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(nodePath);
        }
    }

    protected void createServiceNode(String nodePath, byte[] data) throws Exception {
        while (!zookeeperService.isAlive()) {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        if (zookeeperService.nodeNotExist(nodePath)) {
            log.debug("Creating {}", nodePath);
            zookeeperService
                    .getClient()
                    .create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(nodePath, data);
        }
    }

}
