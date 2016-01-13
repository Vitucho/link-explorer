package org.giordans.graphs;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.vituchon.util.StringUtils;

public class Path<N> {

    private final List<N> nodes;

    Path() {
        this.nodes = new LinkedList<>();
    }

    Path(List<N> nodes) {
        this.nodes = new LinkedList(nodes);
    }


    public List<N> getNodes() {
        return nodes;
    }

    static <N> Path<N> newPath(List<N> nodes) {
        Path<N> path = new Path<>();
        path.getNodes().addAll(nodes);
        return path;
    }

    @Override
    public String toString() {
        return toString(", ");
    }

    public String toString(String joiner) {
        return StringUtils.join(nodes, joiner);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Path)) {
           return false;
        }
        Path<N> otherPath = (Path<N>) other;
        return this.nodes.equals(otherPath.nodes);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.nodes);
        return hash;
    }
    
    
    
}
