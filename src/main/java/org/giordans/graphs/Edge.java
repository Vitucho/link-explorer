package org.giordans.graphs;

/**
 * Edge of a generic graph.
 *
 * @param <N>
 * the type of elements connected by this edge
 * @param <E>
 * the data type associated with this edge
 */
public class Edge<N, E> {

    /**
     * Source node.
     */
    private final N source;
    /**
     * Destination node.
     */
    private final N destination;
    /**
     * Edge data.
     */
    private final E data;

    /**
     * Creates a new edge.
     *
     * @param source
     * the source node
     * @param destination
     * the destination node
     * @param data
     * the edge data
     */
    Edge(N source, N destination, E data) {
        this.source = source;
        this.destination = destination;
        this.data = data;
    }

    /**
     * @return The source node.
     */
    public N getSource() {
        return source;
    }

    /**
     * @return The destination node.
     */
    public N getDestination() {
        return destination;
    }

    /**
     * @return The edge data.
     */
    public E getData() {
        return data;
    }
}
