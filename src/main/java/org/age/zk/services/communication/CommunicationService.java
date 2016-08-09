package org.age.zk.services.communication;

import org.age.zk.services.communication.watcher.events.SendMessageEvent;

public interface CommunicationService {

    void sendMessage(SendMessageEvent message);

}
