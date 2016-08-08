package org.age.zk.services.lifecycle;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class LifecycleServiceImpl implements LifecycleService {

    private static final Logger log = LoggerFactory.getLogger(LifecycleServiceImpl.class);

    @Value("${zookeeper.connection.string}")
    private String zookeeperConnectionString;

    private AtomicBoolean alive = new AtomicBoolean(false);

    private CuratorFramework client;

    @PostConstruct
    public void init() throws InterruptedException {
        log.info("Joining ZK cluster {}", zookeeperConnectionString);
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        client.start();
        client.blockUntilConnected();
        alive.set(true);

        log.info("Zookeeper state: {}", client.getState());
    }

    @Override
    public boolean isAlive() {
        return alive.get();
    }

    @Override
    public CuratorFramework getClient() {
        return client;
    }

    @PreDestroy
    public void cleanup() {
        alive.set(false);
        if (client != null) {
            client.close();
            log.info("Zookeeper state: {}", client.getState());
        }
    }


}
