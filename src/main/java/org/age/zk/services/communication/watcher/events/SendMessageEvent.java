package org.age.zk.services.communication.watcher.events;

import com.google.common.base.MoreObjects;
import org.age.zk.services.communication.message.Message;

import java.io.Serializable;

public class SendMessageEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Message message;

    public SendMessageEvent(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("message", message)
                .toString();
    }
}
