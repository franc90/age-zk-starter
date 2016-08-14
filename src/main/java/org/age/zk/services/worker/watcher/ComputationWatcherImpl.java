package org.age.zk.services.worker.watcher;

import org.age.zk.services.AbstractWatcher;
import org.age.zk.services.worker.WorkerConst;
import org.age.zk.services.worker.event.InitializeEvent;
import org.apache.zookeeper.WatchedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ComputationWatcherImpl extends AbstractWatcher implements ComputationWatcher {

    private static final Logger log = LoggerFactory.getLogger(ComputationWatcherImpl.class);

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case NodeChildrenChanged:
                log.debug("Received {} of type {}. Sending children updated event.", event, event.getType());
                eventBus.post(new InitializeEvent());
                break;
            default:
                log.debug("Received {} of type {}. Not doing anything about it.", event, event.getType());
        }

        zookeeperService.setWatcher(WorkerConst.COMPUTATION_STATE_NODE_PATH, this);
    }

}
