package org.age.zk.services.communication.watcher;

import org.apache.zookeeper.Watcher;

public interface CommunicationWatcher extends Watcher {

    void setInboxPath(String inboxPath);

}
