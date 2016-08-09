package org.age.zk.services.communication.message;

import com.google.common.base.MoreObjects;

public class Message {

    private final String senderId;

    private final String recipientId;

    private final String messageUUID;

    private final long sendTime;

    private final String body;

    public Message(String senderId, String recipientId, String messageUUID, long sendTime, String body) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.messageUUID = messageUUID;
        this.sendTime = sendTime;
        this.body = body;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public String getMessageUUID() {
        return messageUUID;
    }

    public long getSendTime() {
        return sendTime;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("senderId", senderId)
                .add("recipientId", recipientId)
                .add("messageUUID", messageUUID)
                .add("sendTime", sendTime)
                .add("body", body)
                .toString();
    }
}
