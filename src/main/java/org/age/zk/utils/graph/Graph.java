package org.age.zk.utils.graph;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.List;

public class Graph {

    private List<Node> nodes;

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Graph topologyGraph = (Graph) o;
        return Objects.equal(nodes, topologyGraph.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nodes);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("nodes", nodes)
                .toString();
    }
}
