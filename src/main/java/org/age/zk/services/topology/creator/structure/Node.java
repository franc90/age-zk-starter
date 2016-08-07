package org.age.zk.services.topology.creator.structure;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.HashSet;
import java.util.Set;

public class Node {

    private String name;

    private String content;

    private Set<Node> children = new HashSet<>();

    public Node(String id) {
        this.name = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<Node> getChildren() {
        return children;
    }

    public void setChildren(Set<Node> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node that = (Node) o;
        return Objects.equal(name, that.name) &&
                Objects.equal(content, that.content) &&
                Objects.equal(children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, content, children);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("content", content)
                .add("children", children)
                .toString();
    }
}
