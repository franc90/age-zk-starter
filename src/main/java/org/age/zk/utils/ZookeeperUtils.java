package org.age.zk.utils;

import org.age.zk.services.lifecycle.LifecycleService;
import org.age.zk.services.topology.creator.structure.Graph;
import org.age.zk.services.topology.creator.structure.Node;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ZookeeperUtils {

    private static final Logger log = LoggerFactory.getLogger(ZookeeperUtils.class);

    @Autowired
    private LifecycleService lifecycleService;

    public void setWatcher(String node, Watcher watcher) {
        try {
            lifecycleService.getClient().getChildren().usingWatcher(watcher).forPath(node);
        } catch (Exception e) {
            log.error("Could not add watcher " + watcher + "to " + node, e);
        }
    }

    public List<String> getChildren(String node) {
        try {
            return lifecycleService.getClient().getChildren().forPath(node);
        } catch (Exception e) {
            throw new RuntimeException("Exception while loading children of " + node, e);
        }
    }

    public void killChildren(String nodePath) {
        try {
            List<String> children = lifecycleService.getClient().getChildren().forPath(nodePath);

            for (String child : children) {
                String childPath = PathUtils.appendNode(nodePath, child);
                lifecycleService.getClient().delete().deletingChildrenIfNeeded().forPath(childPath);
            }
        } catch (Exception e) {
            log.error("Could not delete children of " + nodePath, e);
        }
    }

    public void createGraph(String nodePath, Graph graph) {
        CuratorTransaction curatorTransaction = lifecycleService.getClient().inTransaction();
        List<Node> nodes = graph.getNodes();

        //TODO IMPLEMENT
    }
}
