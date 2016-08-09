package org.age.zk.services;

import com.google.common.eventbus.EventBus;
import org.age.zk.services.zookeeper.ZookeeperService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public abstract class AbstractWatcher {

    @Autowired
    protected ZookeeperService zookeeperService;

    @Autowired
    protected EventBus eventBus;

    @PostConstruct
    public void init() {
        eventBus.register(this);
    }

}
