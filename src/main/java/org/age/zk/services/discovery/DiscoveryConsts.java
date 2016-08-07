package org.age.zk.services.discovery;

import org.age.zk.utils.PathUtils;

public interface DiscoveryConsts {

    String DISCOVERY = "discovery";

    String DISCOVERY_NODE_PATH = PathUtils.createPath(DISCOVERY);

    String MEMBER = "node_";

    String DISCOVERY_MEMBER_PATH = PathUtils.createPath(DISCOVERY, MEMBER);
}
