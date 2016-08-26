package org.age.zk.services.test;

import org.age.zk.services.discovery.DiscoveryConsts;
import org.age.zk.services.zookeeper.ZookeeperService;
import org.age.zk.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MembershipTracker {

    private static final Logger log = LoggerFactory.getLogger(MembershipTracker.class);

    @Autowired
    protected ZookeeperService zookeeperService;

    private List<String> members = new ArrayList<>();

    public void checkMembershipChange() {
        List<String> newMembers = zookeeperService.getChildren(DiscoveryConsts.DISCOVERY_NODE_PATH);

        newMembers.stream()
                .filter(e -> !members.contains(e))
                .forEach(e -> {
                    long timestamp = System.currentTimeMillis();
                    log.warn("{},add,{},{}", TimeUtils.toString(timestamp), timestamp, e);
                });

        members.stream()
                .filter(e -> !newMembers.contains(e))
                .forEach(e -> {
                    long timestamp = System.currentTimeMillis();
                    log.warn("{},rmv,{},{}", TimeUtils.toString(timestamp), timestamp, e);
                });

        members = newMembers;
    }

}
