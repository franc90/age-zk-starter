package org.age.zk.tasks;

import com.google.common.eventbus.Subscribe;
import org.age.zk.services.communication.watcher.events.ReceivedMessageEvent;
import org.age.zk.utils.ReceivedMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RandomSenderAndReceiver extends RandomRecipientMessageSender {

    private static final Logger log = LoggerFactory.getLogger(RandomSenderAndReceiver.class);

    private final ReceivedMessageProcessor receivedMessageProcessor;

    private boolean running;

    @Autowired
    public RandomSenderAndReceiver(ReceivedMessageProcessor receivedMessageProcessor) {
        this.receivedMessageProcessor = receivedMessageProcessor;
    }

    @PostConstruct
    public void init() {
        eventBus.register(this);
    }

    @Override
    public void run() {
        log.info("RandomSenderAndReceiver computation.");
        running = true;
        super.run();
    }

    @Subscribe
    public void receiveMessage(ReceivedMessageEvent event) {
        if (!running) {
            return;
        }

        receivedMessageProcessor.process(event);
    }

}
