package org.age.zk.services.worker;

import org.age.zk.utils.PathUtils;

public interface WorkerConst {

    String STATE = "state";

    String COMPUTATION = "computation";

    String COMPUTATION_MANAGEMENT = "management";

    String COMPUTATION_NODE_PATH = PathUtils.createPath(COMPUTATION);

    String COMPUTATION_MANAGEMENT_NODE_PATH = PathUtils.appendNode(COMPUTATION_NODE_PATH, COMPUTATION_MANAGEMENT);

    String COMPUTATION_STATE_NODE_PATH = PathUtils.appendNode(COMPUTATION_MANAGEMENT_NODE_PATH, STATE);


}
