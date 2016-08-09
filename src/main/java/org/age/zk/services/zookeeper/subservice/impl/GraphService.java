package org.age.zk.services.zookeeper.subservice.impl;

import org.age.zk.services.zookeeper.subservice.ZookeeperSubService;
import org.age.zk.utils.PathUtils;
import org.age.zk.utils.graph.Graph;
import org.age.zk.utils.graph.Node;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GraphService extends ZookeeperSubService {

    private static final Logger log = LoggerFactory.getLogger(GraphService.class);

    public CuratorTransaction createGraph(String nodePath, Graph graph, CuratorTransaction transaction) {
        log.debug("Adding to {} graph {}", nodePath, graph);
        try {
            boolean noInputTransaction = transaction == null;
            if (noInputTransaction) {
                transaction = client.inTransaction();
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
