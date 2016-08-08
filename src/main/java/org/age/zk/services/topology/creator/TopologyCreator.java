package org.age.zk.services.topology.creator;

import org.age.zk.services.discovery.DiscoveryService;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.UnmodifiableDirectedGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Iterables.getLast;

@Component
public class TopologyCreator {

    private final DiscoveryService discoveryService;

    @Autowired
    public TopologyCreator(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }


    public DirectedGraph<String, DefaultEdge> createTopologyGraph() {
        final DefaultDirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        Set<String> allMembers = discoveryService.getAllMembers();
        allMembers.forEach(graph::addVertex);

        final List<String> sortedIds = allMembers.stream()
                .sorted()
                .collect(Collectors.toList());

        sortedIds
                .stream()
                .reduce(getLast(sortedIds), (nodeIdentity1, nodeIdentity2) -> {
                    graph.addEdge(nodeIdentity1, nodeIdentity2);
                    return nodeIdentity2;
                });

        return new UnmodifiableDirectedGraph<>(graph);
    }

}
