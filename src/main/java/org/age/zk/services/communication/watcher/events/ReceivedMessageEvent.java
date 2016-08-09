package org.age.zk.services.communication.watcher.events;

import com.google.common.base.MoreObjects;
import org.age.zk.services.communication.message.Message;

import java.io.Serializable;

public class ReceivedMessageEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Message message;

    private final long receiveTime;

    public ReceivedMessageEvent(Message message) {
        this.message = message;
        this.receiveTime = System.currentTimeMillis();
    }

    public ReceivedMessageEvent(Message message, long receiveTime) {
        this.message = message;
        this.receiveTime = receiveTime;
    }

    public Message getMessage() {
        return message;
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("message", message)
                .add("receiveTime", receiveTime)
                .toString();
    }
}
