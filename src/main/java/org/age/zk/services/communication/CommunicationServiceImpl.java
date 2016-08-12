package org.age.zk.services.communication;

import com.google.common.eventbus.Subscribe;
import org.age.zk.services.AbstractService;
import org.age.zk.services.communication.message.Message;
import org.age.zk.services.communication.message.MessageSerializer;
import org.age.zk.services.communication.watcher.CommunicationWatcher;
import org.age.zk.services.communication.watcher.events.CheckInboxEvent;
import org.age.zk.services.communication.watcher.events.SendMessageEvent;
import org.age.zk.utils.PathUtils;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CommunicationServiceImpl extends AbstractService implements CommunicationService {

    private static final Logger log = LoggerFactory.getLogger(CommunicationServiceImpl.class);

    private final CommunicationWatcher communicationWatcher;

    private String inboxPath;

    @Autowired
    public CommunicationServiceImpl(CommunicationWatcher communicationWatcher) {
        this.communicationWatcher = communicationWatcher;
    }

    @Override
    public void start() {
        log.debug("Start communication service");
        running.set(true);

        try {
            createServiceNode(CommunicationConst.COMMUNICATION_NODE_PATH);
            createMemberNode();
        } catch (Exception e) {
            log.error("Error while starting communication service", e);
        }
        communicationWatcher.setInboxPath(inboxPath);
        zookeeperService.setWatcher(inboxPath, communicationWatcher);
        eventBus.post(new CheckInboxEvent());

        log.debug("Communication started");
    }

    private void createMemberNode() throws Exception {
        String nodeId;
        while ((nodeId = identityService.getNodeId()) == null) {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        inboxPath = PathUtils.appendNode(CommunicationConst.COMMUNICATION_NODE_PATH, nodeId);

        if (zookeeperService.nodeNotExist(inboxPath)) {
            zookeeperService.getClient()
                    .create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(inboxPath);
        }

        log.info("Created member node {}", inboxPath);
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public void stop() {
        log.debug("Stopping communication service");
        running.set(false);
        log.debug("Communication service stopped");
    }

    @Override
    public int getPhase() {
        return Integer.MIN_VALUE + 10;
    }

    @Override
    @Subscribe
    public void sendMessage(SendMessageEvent sendMessageEvent) {
        Message message = sendMessageEvent.getMessage();
        if (message.getRecipientId() == null) {
            log.warn("Recipient is null. Aborting sending message {}", message);
            return;
        }

        String path = PathUtils.createPath(CommunicationConst.COMMUNICATION, message.getRecipientId(), CommunicationConst.MESSAGE);
        String json = MessageSerializer.serialize(message);
        log.info("Sending {} to {}", json, path);

        byte[] data = json.getBytes();

        try {
            zookeeperService
                    .getClient()
                    .create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                    .forPath(path, data);
        } catch (Exception e) {
            log.error("Error while sending " + message, e);
        }
    }

}
