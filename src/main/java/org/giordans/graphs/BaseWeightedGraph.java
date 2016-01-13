package org.giordans.graphs;

import static java.math.BigDecimal.ZERO;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Directed graph with weighted edges, based on Maximus implementaion.
 *
 * @param <N>
 * the type of elements maintained by this graph
 */
public class BaseWeightedGraph<N> implements WeightedGraph<N> {

    /**
     * Maps each node to its outbound edges.
     */
    private final Map<N, Set<Edge<N, Number>>> edges;

    /**
     * Creates an empty graph.
     */
    public BaseWeightedGraph() {
        edges = new HashMap<>();
    }

    /**
     * Adds a node to this graph.
     *
     * @param node
     * element to be added to this graph
     *
     * @throws NullPointerException
     * if node is null
     * @throws IllegalArgumentException
     * if the graph already contains the given node
     */
    @Override
    public void addNode(N node) {
        checkAddNodeArgs(node);

        edges.put(node, new HashSet<Edge<N, Number>>());
    }

    private void checkAddNodeArgs(N node) {
        if (node == null) {
            throw new NullPointerException("node is null");
        }

        if (edges.containsKey(node)) {
            throw new IllegalArgumentException("already existing node");
        }
    }

    /**
     * Adds an edge to this graph.
     *
     * @param source
     * source node
     * @param destination
     * destination node
     * @param weight
     * edge weight
     *
     * @throws NullPointerException
     * if source is null
     * @throws NullPointerException
     * if destination is null
     * @throws IllegalArgumentException
     * if weight is less than one
     * @throws IllegalArgumentException
     * if source is equal to destination (no loops allowed)
     * @throws IllegalArgumentException
     * if source does not belong to this graph
     * @throws IllegalArgumentException
     * if destination does not belong to this graph
     * @throws IllegalArgumentException
     * if this graph already contains an edge with this source and
     * destination
     */
    @Override
    public void addEdge(N source, N destination, Number weight) {
        checkAddEdgeArgs(source, destination, weight);

        edges.get(source).add(new Edge<>(source, destination, weight));
    }

    private void checkAddEdgeArgs(N source, N destination, Number weight) {
        if (source == null) {
            throw new NullPointerException("source is null");
        }

        if (destination == null) {
            throw new NullPointerException("destination is null");
        }

        if (NumberOperator.INSTANCE.compare(weight,ZERO) <= 0) {
            throw new IllegalArgumentException("weight is less or equals than zero");
        }

        if (source.equals(destination)) {
            throw new IllegalArgumentException("source is equal to destination");
        }

        if (!edges.containsKey(source)) {
            throw new IllegalArgumentException("source not found");
        }

        if (!edges.containsKey(destination)) {
            throw new IllegalArgumentException("destination not found");
        }

        boolean exists = false;
        Iterator<Edge<N, Number>> iterator = edges.get(source).iterator();
        while (iterator.hasNext() && !exists) {
            Edge<N, Number> edge = iterator.next();
            exists = edge.getDestination().equals(destination);
        }
        if (exists) {
            throw new IllegalArgumentException("already existing edge");
        }
    }

    /**
     * Gets all the nodes of this graph.
     *
     * @return the nodes.
     */
    @Override
    public Set<N> getNodes() {
        return edges.keySet();
    }


    @Override
    public Set<Edge<N, Number>> getOutboundEdges(N node) {
        return edges.get(node);
    }

    @Override
    public Set<Edge<N, Number>> getEdges() {
        Set<Edge<N, Number>> allEdges = new HashSet<>();
        for (Set<Edge<N, Number>> edges : edges.values()) {
            allEdges.addAll(edges);
        }
        return allEdges;
    }

    @Override
    public PathFinder<N> pathFinder (N from) {
        PathFinder<N> pathFinder = new PathFinder<>();
        pathFinder.findPath(this, from);
        return pathFinder;
    }
}
