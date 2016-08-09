package org.age.zk.services.communication.watcher;

import com.google.common.eventbus.Subscribe;
import org.age.zk.services.AbstractWatcher;
import org.age.zk.services.communication.message.MessageSerializer;
import org.age.zk.services.communication.watcher.events.CheckInboxEvent;
import org.age.zk.services.communication.watcher.events.ReceivedMessageEvent;
import org.age.zk.utils.PathUtils;
import org.apache.zookeeper.WatchedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommunicationWatcherImpl extends AbstractWatcher implements CommunicationWatcher {

    private static final Logger log = LoggerFactory.getLogger(CommunicationWatcherImpl.class);

    private String inboxPath;

    @Override
    public void setInboxPath(String inboxPath) {
        this.inboxPath = inboxPath;
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case NodeChildrenChanged:
                log.debug("New message", event, event.getType());
                processInboxMessages();
                break;
            default:
                log.debug("Received {} of type {}. Not doing anything about it.", event, event.getType());
        }

        zookeeperService.setWatcher(inboxPath, this);
    }

    @Subscribe
    public void checkInbox(CheckInboxEvent event) {
        processInboxMessages();
    }

    private void processInboxMessages() {
        List<String> msgs = zookeeperService.getChildren(inboxPath);
        log.debug("Messages in inbox: {}", msgs.size());

        msgs.stream()
                .sorted()
                .map(msg -> PathUtils.appendNode(inboxPath, msg))
                .map(zookeeperService::getDataAndDelete)
                .map(MessageSerializer::deserialize)
                .map(ReceivedMessageEvent::new)
                .forEach(eventBus::post);
    }
}
