package org.giordans.graphs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Administrador
 */
public class UnweightedGraph implements Graph<String, Void> {

    private final boolean[][] adjacencyMatrix;
    private final String[] nodes;
    private int size;

    public UnweightedGraph(int maxNodes) {
        this.adjacencyMatrix = new boolean[maxNodes][maxNodes];
        for (boolean[] adjacencyMatrix1 : this.adjacencyMatrix) {
            for (int j = 0; j < adjacencyMatrix1.length; j++) {
                adjacencyMatrix1[j] = false;
            }
        }
        this.size = 0;
        this.nodes = new String[maxNodes];
    }

    @Override
    public void addNode(String nodeLabel) {
        if (this.size < this.nodes.length) {
            this.nodes[this.size] = nodeLabel;
            this.size++;
        }
    }

    @Override
    public void addEdge(String origin, String destination, Void data) {
        int indexOrigin = indexOf(origin);
        int indexDestination = indexOf(destination);
        if (indexOrigin >= 0 && indexDestination >= 0) {
            this.adjacencyMatrix[indexOrigin][indexDestination] = true;
        }
    }

    public Path getPath(String origin, String destination) {
        Path path = null;
        int indexOrigin = indexOf(origin);
        int indexDestination = indexOf(destination);
        if (indexOrigin >= 0 && indexDestination >= 0) {
            List<String> nodesBuffer = new LinkedList<>();
            nodesBuffer.add(this.nodes[indexOrigin]);
            if (indexOrigin != indexDestination) {
                boolean found = searchAPath(nodesBuffer, indexOrigin, indexDestination);
                if (!found) {
                    nodesBuffer.remove(this.nodes[indexOrigin]);
                }
            }
            path = new Path(nodesBuffer);
        }
        return path;
    }

    @Override
    public Set<Edge<String, Void>> getEdges() {
        Set<Edge<String, Void>> edges = new HashSet<>();
        for (int i = 0; i < this.size; i++) {
            String nodeOrigin = this.nodes[i];
            for (int j = 0; j < this.size; j++) {
                if (this.adjacencyMatrix[i][j]) {
                    String nodeDestination = this.nodes[j];
                    Edge edge = new Edge(nodeOrigin, nodeDestination, null);
                    edges.add(edge);
                }
            }
        }
        return edges;
    }

    @Override
    public Set<String> getNodes() {
        HashSet<String> setOfNodes = new HashSet<>(Arrays.asList(nodes));
        setOfNodes.remove(null);
        return setOfNodes;
    }

    private int indexOf(String nodeLabel) {
        for (int i = 0; i < this.size; i++) {
            if (this.nodes[i].equals(nodeLabel)) {
                return i;
            }
        }
        return -1;
    }

    private boolean searchAPath(List<String> nodesBuffer, int from, int to) {
        boolean found = false;
        for (int i = 0; !found && i < this.size; i++) {
            if (adjacencyMatrix[from][i]) {
                if (i == to) {
                    nodesBuffer.add(this.nodes[i]);
                    found = true;
                } else {
                    if (!nodesBuffer.contains(this.nodes[i])) {
                        nodesBuffer.add(this.nodes[i]);
                        found = searchAPath(nodesBuffer, i, to);
                        if (!found) {
                            nodesBuffer.remove(this.nodes[i]);
                        }
                    }
                }
            }
        }
        return found;
    }

    public void addEdge(String origin, String destination) {
        addEdge(origin, destination, null);
    }

    @Override
    public Set<Edge<String, Void>> getOutboundEdges(String node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
