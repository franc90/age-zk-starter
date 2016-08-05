package org.age.zk.services.lifecycle;

import org.apache.curator.framework.CuratorFramework;

public interface LifecycleService {

    boolean isAlive();

    CuratorFramework getClient();

}
