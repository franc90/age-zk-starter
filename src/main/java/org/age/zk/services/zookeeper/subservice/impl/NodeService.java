package org.age.zk.services.zookeeper.subservice.impl;

import org.age.zk.services.zookeeper.subservice.ZookeeperSubService;
import org.age.zk.utils.PathUtils;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NodeService extends ZookeeperSubService {

    private static final Logger log = LoggerFactory.getLogger(NodeService.class);

    public boolean nodeExist(String nodePath) {
        try {
            Stat nodeStat = client
                    .checkExists()
                    .forPath(nodePath);
            return nodeStat != null;
        } catch (Exception e) {
            log.error("Could not check if node exists", e);
            return false;
        }
    }

    public void setData(String nodePath, byte[] data) {
        log.debug("Setting data of {}", nodePath);
        try {
            client.setData().forPath(nodePath, data);
        } catch (Exception e) {
            throw new RuntimeException("Exception while setting data of " + nodePath, e);
        }
    }

    public String getData(String nodePath) {
        log.debug("Get data of {} ", nodePath);
        byte[] rawData = getRawData(nodePath);
        return new String(rawData);
    }

    public byte[] getRawData(String nodePath) {
        log.debug("Get raw data of {} ", nodePath);
        try {
            return client.getData().forPath(nodePath);
        } catch (Exception e) {
            throw new RuntimeException("Exception while getting data of " + nodePath, e);
        }
    }

    public void setWatcher(String nodePath, Watcher watcher) {
        log.debug("Set watcher for {}", nodePath);
        try {
            client.getChildren().usingWatcher(watcher).forPath(nodePath);
        } catch (Exception e) {
            log.error("Could not set watcher " + watcher + "to " + nodePath, e);
        }
    }

    public void deleteNode(String nodePath) {
        log.debug("Delete data of {}", nodePath);
        try {
            client.delete().forPath(nodePath);
        } catch (Exception e) {
            throw new RuntimeException("Exception while deleting " + nodePath, e);
        }
    }

    public List<String> getChildren(String nodePath) {
        log.debug("Get children of {}", nodePath);
        try {
            return client.getChildren().forPath(nodePath);
        } catch (Exception e) {
            throw new RuntimeException("Exception while listing children of " + nodePath, e);
        }
    }

    public CuratorTransaction killChildren(String nodePath, CuratorTransaction transaction) {
        log.debug("Deleting children of {}", nodePath);
        try {
            boolean noInputTransaction = transaction == null;
            if (noInputTransaction) {
                transaction = client.inTransaction();
            }

            List<String> children = client.getChildren().forPath(nodePath);
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
}
