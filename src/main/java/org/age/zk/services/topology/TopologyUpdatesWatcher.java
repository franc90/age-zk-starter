package org.age.zk.services.topology;

import org.age.zk.services.lifecycle.LifecycleService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
public class TopologyUpdatesWatcher implements Watcher {

    private static final Logger log = LoggerFactory.getLogger(TopologyUpdatesWatcher.class);

    @Autowired
    private LifecycleService lifecycleService;

    @PostConstruct
    public void init() throws Exception {
        while (!lifecycleService.isAlive()) {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        CuratorFramework client = lifecycleService.getClient();
        Stat topologyStat = client
                .checkExists()
                .forPath("/topology");

        if (topologyStat == null) {
            client
                    .create()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath("/topology");
        }

        setWatch();
    }

    @Override
    public void process(WatchedEvent event) {
        log.info("Received event: {}", event);
        setWatch();
    }

    private void setWatch() {
        try {
            lifecycleService
                    .getClient()
                    .getChildren()
                    .usingWatcher(this)
                    .forPath("/topology");
        } catch (Exception e) {
            log.error("Error while setting watch for /topology ", e);
        }
    }

}
