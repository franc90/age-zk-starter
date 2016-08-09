package org.age.zk.services.identity;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdentityService {

    private final String nodeUUID = UUID.randomUUID().toString();

    private String nodeId;

    public String getNodeUUID() {
        return nodeUUID;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

}
