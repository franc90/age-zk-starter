package org.age.zk.services.topology;

import com.google.common.eventbus.EventBus;
import org.age.zk.services.identity.IdentityService;
import org.age.zk.services.lifecycle.LifecycleService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class TopologyService {

    private static final Logger log = LoggerFactory.getLogger(TopologyService.class);

    @Autowired
    private LifecycleService lifecycleService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private EventBus eventBus;

    @Autowired
    private TopologyUpdatesWatcher watcher;

    @PostConstruct
    public void init() throws Exception {
        CuratorFramework client = lifecycleService.getClient();

        String nodeId = identityService.getNodeId();
        String result = client
                .create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath("/topology/node-", nodeId.getBytes());

        log.info("Topology created ephemeral sequential {}", result);

        List<String> strings = client.getChildren().usingWatcher(watcher).forPath("/topology");
        log.info("/topology children {}", strings.stream().reduce((x, y) -> x + y).orElse(null));

        TimeUnit.SECONDS.sleep(10);
    }


}
