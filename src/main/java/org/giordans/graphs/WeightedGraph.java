/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.giordans.graphs;

import java.util.Set;

/**
 * Defines the interface for graph with weighted edges.
 * @param <N>
 *            the type of elements maintained by this graph
 */
public interface WeightedGraph<N> extends Graph<N,Number>  {

    /**
     * Adds an edge to this graph.
     *
     * @param source
     *            source node
     * @param destination
     *            destination node
     * @param weight
     *            edge weight
     *
     * @throws NullPointerException
     *             if source is null
     * @throws NullPointerException
     *             if destination is null
     * @throws IllegalArgumentException
     *             if weight is less than one
     * @throws IllegalArgumentException
     *             if source is equal to destination (no loops allowed)
     * @throws IllegalArgumentException
     *             if source does not belong to this graph
     * @throws IllegalArgumentException
     *             if destination does not belong to this graph
     * @throws IllegalArgumentException
     *             if this graph already contains an edge with this source and
     *             destination
     */
    @Override
    void addEdge(N source, N destination, Number weight);
    

    public PathFinder<N> pathFinder (N from);

}
