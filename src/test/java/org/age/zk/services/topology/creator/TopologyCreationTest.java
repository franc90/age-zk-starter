package org.age.zk.services.topology.creator;

import com.google.common.collect.Sets;
import org.age.zk.services.discovery.DiscoveryService;
import org.age.zk.utils.graph.Graph;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TopologyCreationTest {

    @Mock
    private DiscoveryService discoveryService;

    @InjectMocks
    private TopologyCreator topologyCreator;

    @Before
    public void init() {
        when(discoveryService.getAllMembers()).thenReturn(Sets.newHashSet("node1", "node2", "node3", "node4", "node5"));
    }

    @Test
    public void testSetUp() {
        assertThat(topologyCreator).isNotNull();
    }

    @Test
    public void testCreatingTopology() {
        DirectedGraph<String, DefaultEdge> topologyGraph = topologyCreator.createTopologyGraph();
        assertThat(topologyGraph).isNotNull();
        assertThat(topologyGraph.vertexSet()).hasSize(5).containsExactlyInAnyOrder("node1", "node2", "node3", "node4", "node5");
    }

    @Test
    public void testConvertingTopologyGraph() {
        DirectedGraph<String, DefaultEdge> topologyGraph = topologyCreator.createTopologyGraph();

        Graph convertedTopology = TopologyAssembler.convert(topologyGraph);
        DirectedGraph<String, DefaultEdge> newTopology = TopologyAssembler.convert(convertedTopology);

        assertThat(newTopology)
                .isNotNull();
        assertThat(newTopology.vertexSet())
                .hasSize(topologyGraph.vertexSet().size())
                .containsExactlyElementsOf(topologyGraph.vertexSet());
    }

}