package org.age.zk.utils;

import org.age.zk.services.communication.message.Message;
import org.age.zk.services.communication.watcher.events.ReceivedMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReceivedMessageProcessor {

    private static final Logger log = LoggerFactory.getLogger(ReceivedMessageProcessor.class);

    public void process(ReceivedMessageEvent receiveEvent) {
        Message message = receiveEvent.getMessage();
        String sendTime = TimeUtils.toString(message.getSendTime());
        long receiveTimestamp = System.currentTimeMillis();
        String receiveTime = TimeUtils.toString(receiveTimestamp);

        log.warn("{},rcv,{},{},{}", TimeUtils.toString(receiveTimestamp), receiveTimestamp, message.getSendTime(), message.getMessageUUID());

        log.info("Received:\n" +
                        "MSG_ID:    {}\n\n" +
                        "FROM:      {}\n" +
                        "           {} [{}]\n\n" +
                        "TO:        {}\n" +
                        "           {} [{}]",
                message.getMessageUUID(),
                message.getSenderId(),
                sendTime,
                message.getSendTime(),
                message.getRecipientId(),
                receiveTime,
                receiveTimestamp);
    }

}
