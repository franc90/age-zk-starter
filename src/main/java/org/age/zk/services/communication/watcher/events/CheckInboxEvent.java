package org.age.zk.services.communication.watcher.events;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

public class CheckInboxEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .toString();
    }
}
