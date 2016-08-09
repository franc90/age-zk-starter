package org.age.zk.services.zookeeper.subservice;

import com.google.common.base.Preconditions;
import org.apache.curator.framework.CuratorFramework;

public abstract class ZookeeperSubService {

    protected CuratorFramework client;

    public void setClient(CuratorFramework client) {
        this.client = client;
    }

    public void checkClientNotNull() {
        Preconditions.checkNotNull(client, "Client cannot be null");
    }

}
