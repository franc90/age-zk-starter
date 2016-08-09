package org.age.zk.services.topology.watcher;

import org.age.zk.services.AbstractWatcher;
import org.age.zk.services.topology.TopologyConst;
import org.age.zk.services.topology.watcher.events.TopologyUpdatedEvent;
import org.apache.zookeeper.WatchedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TopologyWatcherImpl extends AbstractWatcher implements TopologyWatcher {

    private static final Logger log = LoggerFactory.getLogger(TopologyWatcherImpl.class);

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case NodeChildrenChanged:
                log.debug("Received {} of type {}. Sending children updated event.", event, event.getType());
                eventBus.post(new TopologyUpdatedEvent());
                break;
            default:
                log.debug("Received {} of type {}. Not doing anything about it.", event, event.getType());
        }

        zookeeperService.setWatcher(TopologyConst.TOPOLOGY_NODE_PATH, this);
    }

}
