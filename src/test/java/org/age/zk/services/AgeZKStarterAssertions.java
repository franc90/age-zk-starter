package org.age.zk.services;

import org.age.zk.services.communication.message.Message;
import org.age.zk.services.communication.message.MessageAssert;

public class AgeZKStarterAssertions {

    public static MessageAssert assertThat(Message message) {
        return new MessageAssert(message);
    }

}
