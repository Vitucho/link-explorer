/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.giordans.graphs;

import java.util.Set;

/**
 * Defines behaviour for generic graphs.
 *
 * @param <N>
 * @param <E>
 */
public interface Graph<N, E> {

    /**
     * Gets all the edges of this Graph.
     *
     * @return A set of {@link Edge}
     */
    public abstract Set<Edge<N, E>> getEdges();

    /**
     * Gets all the nodes of this Graph.
     *
     * @return A set of nodes.
     */
    public abstract Set<N> getNodes();

    /**
     * Adds a node to this graph.
     *
     * @param node The data of the node.
     */
    public abstract void addNode(N node);

    /**
     * Adds an edge to this graph, linking one node <code>source</code> with another node <code>destination</code>.
     *
     * @param source
     * source node
     * @param destination
     * destination node
     * @param data
     * intrisic data of the edge.
     */
    public abstract void addEdge(N source, N destination, E data);
    
     /**
     * Gets all the outbound edges for a given node.
     */
    public abstract Set<Edge<N, E>> getOutboundEdges(N node);
}
