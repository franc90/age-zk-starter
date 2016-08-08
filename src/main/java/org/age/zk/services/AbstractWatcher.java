package org.age.zk.services;

import com.google.common.eventbus.EventBus;
import org.age.zk.utils.ZookeeperUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractWatcher {

    protected final ZookeeperUtils zookeeperUtils;

    protected final EventBus eventBus;

    @Autowired
    public AbstractWatcher(ZookeeperUtils zookeeperUtils, EventBus eventBus) {
        this.zookeeperUtils = zookeeperUtils;
        this.eventBus = eventBus;
    }
}
