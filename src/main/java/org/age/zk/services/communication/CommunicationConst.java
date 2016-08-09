package org.age.zk.services.communication;

import org.age.zk.utils.PathUtils;

public interface CommunicationConst {

    String COMMUNICATION = "communication";

    String COMMUNICATION_NODE_PATH = PathUtils.createPath(COMMUNICATION);

    String MESSAGE = "msg_";

}
