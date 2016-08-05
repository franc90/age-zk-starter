package org.age.zk.services.identity;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdentityService {

    private final String nodeId = UUID.randomUUID().toString();

    public String getNodeId() {
        return nodeId;
    }

}
