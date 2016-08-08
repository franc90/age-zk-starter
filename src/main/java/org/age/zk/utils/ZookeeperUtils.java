package org.age.zk.utils;

import org.age.zk.services.lifecycle.LifecycleService;
import org.age.zk.utils.graph.Graph;
import org.age.zk.utils.graph.Node;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ZookeeperUtils {

    private static final Logger log = LoggerFactory.getLogger(ZookeeperUtils.class);

    private final LifecycleService lifecycleService;

    @Autowired
    public ZookeeperUtils(LifecycleService lifecycleService) {
        this.lifecycleService = lifecycleService;
    }

    public void setWatcher(String node, Watcher watcher) {
        try {
            lifecycleService.getClient().getChildren().usingWatcher(watcher).forPath(node);
        } catch (Exception e) {
            log.error("Could not set watcher " + watcher + "to " + node, e);
        }
    }

    public List<String> getChildren(String node) {
        try {
            return lifecycleService.getClient().getChildren().forPath(node);
        } catch (Exception e) {
            throw new RuntimeException("Exception while listing children of " + node, e);
        }
    }

    public void clearNodeAndUpdateWithGraph(String nodePath, Graph graph) {
        try {
            CuratorTransaction curatorTransaction = lifecycleService.getClient().inTransaction();

            killChildren(nodePath, curatorTransaction);
            createGraph(nodePath, graph, curatorTransaction);

            if (curatorTransaction instanceof CuratorTransactionFinal) {
                ((CuratorTransactionFinal) curatorTransaction).commit();
            }
        } catch (Exception e) {
            log.error("Could not commit transaction for {}, {}", nodePath, graph);
        }
    }

    public void killChildren(String nodePath) {
        killChildren(nodePath, null);
    }

    private CuratorTransaction killChildren(String nodePath, CuratorTransaction transaction) {
        log.debug("Deleting children of {}", nodePath);
        try {
            boolean noInputTransaction = transaction == null;
            if (noInputTransaction) {
                transaction = lifecycleService.getClient().inTransaction();
            }

            List<String> children = lifecycleService.getClient().getChildren().forPath(nodePath);
            for (String child : children) {
                String childPath = PathUtils.appendNode(nodePath, child);
                transaction = transaction
                        .delete()
                        .forPath(childPath)
                        .and();
            }
            if (noInputTransaction) {
                if (transaction instanceof CuratorTransactionFinal) {
                    ((CuratorTransactionFinal) transaction).commit();
                }
                return null;
            }
            return transaction;
        } catch (Exception e) {
            log.error("Could not delete children of " + nodePath, e);
            return null;
        }
    }

    public void createGraph(String nodePath, Graph graph) {
        createGraph(nodePath, graph, null);
    }

    private CuratorTransaction createGraph(String nodePath, Graph graph, CuratorTransaction transaction) {
        log.debug("Adding to {} graph {}", nodePath, graph);
        try {
            boolean noInputTransaction = transaction == null;
            if (noInputTransaction) {
                transaction = lifecycleService.getClient().inTransaction();
            }
            List<Node> nodes = graph.getNodes();
            for (Node node : nodes) {
                String path = PathUtils.appendNode(nodePath, node.getName());
                byte[] data = node.getData().getBytes();
                transaction = transaction
                        .create()
                        .forPath(path, data)
                        .and();
            }
            if (noInputTransaction) {
                if (transaction instanceof CuratorTransactionFinal) {
                    ((CuratorTransactionFinal) transaction).commit();
                }
                return null;
            }
            return transaction;
        } catch (Exception e) {
            log.error("Could not add graph to " + nodePath, e);
            return null;
        }
    }

    public Graph getGraph(String nodePath) {
        log.debug("Loading graph from children of {}", nodePath);
        try {
            CuratorFramework client = lifecycleService.getClient();
            List<String> children = client.getChildren().forPath(nodePath);

            Graph graph = new Graph();

            List<Node> nodes = new ArrayList<>(children.size());
            for (String child : children) {
                String path = PathUtils.appendNode(nodePath, child);
                byte[] bytes = client.getData().forPath(path);
                String data = new String(bytes);
                nodes.add(new Node(child, data));
            }
            graph.setNodes(nodes);

            return graph;
        } catch (Exception e) {
            log.error("Error while loading graph", e);
            return null;
        }
    }
}
