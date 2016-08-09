package org.age.zk.services.leadership;

import org.age.zk.services.AbstractService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class LeadershipServiceImpl extends AbstractService implements LeadershipService {

    private static final Logger log = LoggerFactory.getLogger(LeadershipServiceImpl.class);

    private final AtomicBoolean master = new AtomicBoolean(false);

    private LeaderSelector leaderSelector;

    @PostConstruct
    public void init() throws InterruptedException {
        log.info("Initializing leadership service");
        while (!zookeeperService.isAlive()) {
            log.debug("Initialization: waiting for zookeeperService to start");
            TimeUnit.MILLISECONDS.sleep(100);
        }
        leaderSelector = new LeaderSelector(zookeeperService.getClient(), LeadershipConst.LIFECYCLE_PATH, new LeaderSelectorListener());
        leaderSelector.autoRequeue();
        log.info("Leadership initialized");
    }

    @Override
    public void stop(Runnable callback) {
        log.debug("Stop leadership service with callback");
        stop();
        callback.run();
    }

    @Override
    public void start() {
        log.debug("Start leadership service");
        leaderSelector.start();
        running.set(true);
        log.debug("Leadership started");
    }

    @Override
    public void stop() {
        log.debug("Stop leadership service");
        running.set(false);
        leaderSelector.close();
        log.debug("Leadership stopped");
    }

    @Override
    public int getPhase() {
        return Integer.MIN_VALUE + 5;
    }

    @Override
    public boolean isMaster() {
        return master.get();
    }

    private class LeaderSelectorListener extends LeaderSelectorListenerAdapter {
        @Override
        public void takeLeadership(CuratorFramework client) throws Exception {
            master.set(true);
            log.info("I am new master");
            try {
                while (true) {
                    TimeUnit.MILLISECONDS.sleep(100);
                }
            } catch (InterruptedException ex) {
                log.info("Relinquishing leadership");
            } finally {
                master.set(false);
            }
        }
    }
}

