package org.age.zk.services.zookeeper;

import org.age.zk.utils.graph.Graph;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.Watcher;

import java.util.List;

public interface ZookeeperService {

    boolean isAlive();

    CuratorFramework getClient();

    boolean nodeNotExist(String nodePath);

    boolean nodeExist(String nodePath);

    void setData(String nodePath, byte[] data);

    String getData(String nodePath);

    byte[] getRawData(String nodePath);

    void deleteNode(String nodePath);

    void setWatcher(String nodePath, Watcher watcher);

    List<String> getChildren(String nodePath);

    String getDataAndDelete(String nodePath);

    Graph getGraph(String nodePath);

    void replaceChildrenWithGraph(String nodePath, Graph graph);

    void killChildren(String nodePath);

    void createGraph(String nodePath, Graph graph);
}
