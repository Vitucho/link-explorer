/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure.composite.artifacts;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;
import org.giordans.graphs.BaseWeightedGraph;
import org.giordans.graphs.Edge;
import org.giordans.graphs.Path;
import org.giordans.graphs.PathFinder;
import org.giordans.graphs.WeightedGraph;

/**
 *
 * @author Administrador
 */
public class HtmlMap {

    private static final Logger LOGGER = Logger.getLogger(MultiPageLinkInspectorStatus.class.getName());

    private final String root;
    private final WeightedGraph<String> map;

    private static final Number LINK_WEIGHT = new BigDecimal(1);

    HtmlMap(String root) {
        super();
        this.root = root;
        this.map = new BaseWeightedGraph<>();
        this.map.addNode(root);
    }

    public String getRoot() {
        return root;
    }

    void addLink(String from, String to) {
        if (!this.map.getNodes().contains(to)) {
            map.addNode(to);
        }
        map.addEdge(from, to, LINK_WEIGHT);
    }

    public int distanceToRoot(String url) {
        PathFinder<String> pathFinder = map.pathFinder(root);
        return pathFinder.getDist(url).intValue();
    }

    public String toTabbedString() {
        StringBuilder sb = new StringBuilder();
        Set<String> nodes = this.map.getNodes();
        PathFinder<String> pathFinder = this.map.pathFinder(root);
        for (String node : nodes) {
            if (!node.equals(this.root)) {
                Set<Path<String>> paths = pathFinder.getPaths(map, root, node);
                sb.append("From ").append(root).append(" to : ").append(node).append("\n");
                for (Path path : paths) {
                    sb.append("\t").append(path.toString(" -> ")).append("\n");
                }
            }
        }
        return sb.toString();
    }

    public  Map<String, List<String>> toSiteMap() {
        Map<String, List<String>> siteMap = new HashMap<>();
        Set<String> nodes = this.map.getNodes();
        for (String node : nodes) {
            String name = node;
            List<String> links = new LinkedList<>();
            siteMap.put(name, links);
            Set<Edge<String, Number>> edges = this.map.getOutboundEdges(node);
            for (Edge<String, Number> edge : edges) {
                String link = edge.getDestination();
                links.add(link);
            }
        }
        return siteMap;
    }

    public WeightedGraph<String> getMap() {
        return this.map;
    }
}
