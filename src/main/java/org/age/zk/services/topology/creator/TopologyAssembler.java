package org.age.zk.services.topology.creator;

import org.age.zk.services.topology.creator.structure.Graph;
import org.age.zk.services.topology.creator.structure.Node;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TopologyAssembler {

    private static final Function<DirectedGraph<String, DefaultEdge>, Function<Node, Node>> curriableSetContent =
            graph -> node -> setContent(graph, node);

    public static Graph convert(DirectedGraph<String, DefaultEdge> topology) {
        final Function<Node, Node> curriedSetContent = curriableSetContent.apply(topology);
        List<Node> nodes = topology
                .vertexSet()
                .stream()
                .map(Node::new)
                .map(curriedSetContent)
                .collect(Collectors.toList());

        Graph graph = new Graph();
        graph.setNodes(nodes);
        return graph;
    }

    private static Node setContent(DirectedGraph<String, DefaultEdge> topology, Node node) {
        String content = topology
                .outgoingEdgesOf(node.getName())
                .stream()
                .map(topology::getEdgeTarget)
                .reduce((x, y) -> x + "," + y)
                .orElse("");
        node.setContent(content);
        return node;
    }

    public static DirectedGraph<String, DefaultEdge> convert(Graph graph) {
        final DefaultDirectedGraph<String, DefaultEdge> topology = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (Node node : graph.getNodes()) {
            topology.addVertex(node.getName());
        }

        for (Node node : graph.getNodes()) {
            String[] neighbors = node.getContent().split(",");
            for (String neighbor : neighbors) {
                topology.getEdge(node.getName(), neighbor);
            }
        }

        return topology;
    }

}
