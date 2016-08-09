package org.age.zk.services.topology;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Set;

public interface TopologyService {

    DirectedGraph<String, DefaultEdge> getTopology();

    Set<String> neighbors();

}
