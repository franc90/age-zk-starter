package org.age.zk.services.discovery.watcher;

import com.google.common.eventbus.EventBus;
import org.age.zk.services.discovery.DiscoveryConsts;
import org.age.zk.services.discovery.watcher.events.MembersUpdatedEvent;
import org.age.zk.services.discovery.watcher.events.StopApplicationEvent;
import org.age.zk.services.lifecycle.LifecycleService;
import org.age.zk.utils.ZookeeperUtils;
import org.apache.zookeeper.WatchedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LifecycleWatcherImpl implements LifecycleWatcher {

    private static final Logger log = LoggerFactory.getLogger(LifecycleWatcherImpl.class);

    @Autowired
    private LifecycleService lifecycleService;

    @Autowired
    private ZookeeperUtils zookeeperUtils;

    @Autowired
    private EventBus eventBus;

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case NodeChildrenChanged:
                log.debug("Received {} of type {}. Sending children updated event.", event, event.getType());
                eventBus.post(new MembersUpdatedEvent());
                break;
            case NodeDeleted:
                log.debug("Received {} of type {}. Sending stop application event.", event, event.getType());
                eventBus.post(new StopApplicationEvent());
                break;
            default:
                log.debug("Received {} of type {}. Not doing anything about it.", event, event.getType());
        }

        zookeeperUtils.setWatcher(DiscoveryConsts.DISCOVERY_NODE_PATH, this);
    }

}
