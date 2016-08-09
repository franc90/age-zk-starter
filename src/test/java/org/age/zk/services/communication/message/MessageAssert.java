package org.age.zk.services.communication.message;

import org.assertj.core.api.AbstractAssert;

import java.util.Objects;

public class MessageAssert extends AbstractAssert<MessageAssert, Message> {

    public MessageAssert(Message actual) {
        super(actual, MessageAssert.class);
    }

    public MessageAssert assertThat(Message message) {
        return new MessageAssert(message);
    }

    public MessageAssert hasSenderId(String senderId) {
        isNotNull();

        if (!Objects.equals(actual.getSenderId(), senderId)) {
            failWithMessage("Expected message's senderId to be <%s> but was <%s>", senderId, actual.getSenderId());
        }

        return this;
    }

    public MessageAssert hasRecipientId(String recipientId) {
        isNotNull();

        if (!Objects.equals(actual.getRecipientId(), recipientId)) {
            failWithMessage("Expected message's recipientId to be <%s> but was <%s>", recipientId, actual.getRecipientId());
        }

        return this;
    }

    public MessageAssert hasMessageUUID(String uuid) {
        isNotNull();

        if (!Objects.equals(actual.getMessageUUID(), uuid)) {
            failWithMessage("Expected message's uuid to be <%s> but was <%s>", uuid, actual.getSenderId());
        }

        return this;
    }

    public MessageAssert hasSendTime(long sendTime) {
        isNotNull();

        if (!Objects.equals(actual.getSendTime(), sendTime)) {
            failWithMessage("Expected message's sendTime to be <%s> but was <%s>", sendTime, actual.getSendTime());
        }

        return this;
    }

    public MessageAssert hasBody(String body) {
        isNotNull();

        if (!Objects.equals(actual.getBody(), body)) {
            failWithMessage("Expected message's body to be <%s> but was <%s>", body, actual.getBody());
        }

        return this;
    }
}
