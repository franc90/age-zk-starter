package org.age.zk.services.topology;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public interface TopologyService {

    DirectedGraph<String, DefaultEdge> getTopology();

}
