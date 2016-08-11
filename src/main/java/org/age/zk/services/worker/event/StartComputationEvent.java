package org.age.zk.services.worker.event;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

public class StartComputationEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .toString();
    }
}
