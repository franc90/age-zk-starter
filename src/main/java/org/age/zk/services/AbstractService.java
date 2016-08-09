package org.age.zk.services;

import com.google.common.eventbus.EventBus;
import org.age.zk.services.identity.IdentityService;
import org.age.zk.services.zookeeper.ZookeeperService;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractService implements SmartLifecycle {

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

}
