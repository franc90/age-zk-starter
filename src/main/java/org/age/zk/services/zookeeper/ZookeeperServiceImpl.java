package org.age.zk.services.zookeeper;

import org.age.zk.services.zookeeper.subservice.impl.GraphService;
import org.age.zk.services.zookeeper.subservice.impl.NodeService;
import org.age.zk.utils.graph.Graph;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ZookeeperServiceImpl implements ZookeeperService {

    private static final Logger log = LoggerFactory.getLogger(ZookeeperService.class);

    private final AtomicBoolean alive = new AtomicBoolean(false);

    private final NodeService nodeService;

    private final GraphService graphService;

    @Value("${zookeeper.connection.string}")
    private String zookeeperConnectionString;

    private CuratorFramework client;

    @Autowired
    public ZookeeperServiceImpl(GraphService graphService,
                                NodeService nodeService) {
        this.graphService = graphService;
        this.nodeService = nodeService;
    }

    @PostConstruct
    public void init() throws InterruptedException {
        log.info("Joining ZK cluster {}", zookeeperConnectionString);
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        client.start();
        client.blockUntilConnected();
        alive.set(true);

        nodeService.setClient(client);
        graphService.setClient(client);

        log.info("Zookeeper state: {}", client.getState());
    }

    @PreDestroy
    public void cleanup() {
        alive.set(false);
        if (client != null) {
            client.close();
            log.info("Zookeeper state: {}", client.getState());
        }
    }

    @Override
    public boolean isAlive() {
        return alive.get();
    }

    @Override
    public CuratorFramework getClient() {
        return client;
    }

    @Override
    public boolean nodeNotExist(String nodePath) {
        return !nodeService.nodeExist(nodePath);
    }

    @Override
    public boolean nodeExist(String nodePath) {
        return nodeService.nodeExist(nodePath);
    }

    @Override
    public void setData(String nodePath, byte[] data) {
        nodeService.setData(nodePath, data);
    }

    @Override
    public String getData(String nodePath) {
        return nodeService.getData(nodePath);
    }

    @Override
    public byte[] getRawData(String nodePath) {
        return nodeService.getRawData(nodePath);
    }

    @Override
    public void deleteNode(String nodePath) {
        nodeService.deleteNode(nodePath);
    }

    @Override
    public void setWatcher(String nodePath, Watcher watcher) {
        nodeService.setWatcher(nodePath, watcher);
    }

    @Override
    public List<String> getChildren(String nodePath) {
        return nodeService.getChildren(nodePath);
    }

    @Override
    public String getDataAndDelete(String nodePath) {
        String data = getData(nodePath);
        deleteNode(nodePath);
        return data;
    }

    @Override
    public void killChildren(String nodePath) {
        nodeService.killChildren(nodePath, null);
    }

    @Override
    public Graph getGraph(String nodePath) {
        return graphService.getGraph(nodePath);
    }


    @Override
    public void createGraph(String nodePath, Graph graph) {
        graphService.createGraph(nodePath, graph, null);
    }

    @Override
    public void replaceChildrenWithGraph(String nodePath, Graph graph) {
        try {
            CuratorTransaction curatorTransaction = client.inTransaction();

            nodeService.killChildren(nodePath, curatorTransaction);
            graphService.createGraph(nodePath, graph, curatorTransaction);

            if (curatorTransaction instanceof CuratorTransactionFinal) {
                ((CuratorTransactionFinal) curatorTransaction).commit();
            }
        } catch (Exception e) {
            log.error("Could not commit transaction for {}, {}", nodePath, graph);
        }
    }

}
