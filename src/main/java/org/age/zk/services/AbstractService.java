package org.age.zk.services;

import com.google.common.eventbus.EventBus;
import org.age.zk.services.discovery.DiscoveryConsts;
import org.age.zk.services.identity.IdentityService;
import org.age.zk.services.lifecycle.LifecycleService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractService implements SmartLifecycle {

    protected final AtomicBoolean running = new AtomicBoolean(false);

    @Autowired
    protected EventBus eventBus;

    @Autowired
    protected IdentityService identityService;

    @Autowired
    protected LifecycleService lifecycleService;

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
        while (!lifecycleService.isAlive()) {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        CuratorFramework client = lifecycleService.getClient();
        Stat serviceNodeStat = client
                .checkExists()
                .forPath(nodePath);

        if (serviceNodeStat == null) {
            client
                    .create()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(nodePath);
        }
    }

}
