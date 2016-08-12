package org.age.zk.tasks;

import com.google.common.eventbus.EventBus;
import org.age.zk.services.communication.message.Message;
import org.age.zk.services.communication.watcher.events.SendMessageEvent;
import org.age.zk.services.identity.IdentityService;
import org.age.zk.services.topology.TopologyService;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.base.MoreObjects.toStringHelper;

@Component
public class RandomRecipientMessageSender implements Task {

    private static final Logger log = LoggerFactory.getLogger(RandomRecipientMessageSender.class);

    @Autowired
    protected EventBus eventBus;

    @Autowired
    private TopologyService topologyService;

    @Autowired
    private IdentityService identityService;


    @Override
    public void run() {
        log.info("RandomRecipientMessageSender computation.");

        while (true) {
            try {
                eventBus.post(buildMessage());
            } catch (IllegalStateException illegalState) {
                log.debug(illegalState.getMessage());
            } catch (Throwable throwable) {
                log.debug("Error while posting message", throwable);
            }

            try {
                Thread.sleep(RandomUtils.nextLong(1000, 4000));
            } catch (InterruptedException e) {
                log.error("Sleep interrupted");
            }
        }
    }

    private SendMessageEvent buildMessage() {
        String recipientId = getRecipientId();
        long sendTimestamp = System.currentTimeMillis();
        String messageUUID = UUID.randomUUID().toString();
        Message msg = new Message(identityService.getNodeId(), recipientId, messageUUID, sendTimestamp, "RandomRecipientMessageSender message");

        return new SendMessageEvent(msg);
    }

    private String getRecipientId() {
        List<String> otherNodes = topologyService.neighbors()
                .stream()
                .filter(node -> !identityService.getNodeId().equals(node))
                .collect(Collectors.toList());

        if (otherNodes.size() == 0) {
            return null;
        }

        int randomRecipientIdIndex = RandomUtils.nextInt(0, otherNodes.size());
        return otherNodes.get(randomRecipientIdIndex);
    }

    @Override
    public String toString() {
        return toStringHelper(this).toString();
    }

}
