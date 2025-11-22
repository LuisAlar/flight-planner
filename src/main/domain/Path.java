package main.domain;

import java.util.*;
import java.util.function.IntFunction;

public class Path {
    private final List<Integer> nodes;
    private final double totalCost;
    private final int totalMinutes;

    
    public Path(List<Integer> nodes, double totalCost, int totalMinutes) {
        this.nodes = List.copyOf(nodes); // make it immutable copy
        this.totalCost = totalCost;
        this.totalMinutes = totalMinutes;
    }

    public List<Integer> getNodes() { return nodes; }
    public double getTotalCost() { return totalCost; }
    public int getTotalMinutes() { return totalMinutes; }

    public int length() { return nodes.size(); } // # of cities in the path

    /** Turn 0,1,3,5 into "Dallas -> Austin -> Houston", etc. */
    public String toRouteString(IntFunction<String> nameOf) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodes.size(); i++) {
            if (i > 0) sb.append(" -> ");
            sb.append(nameOf.apply(nodes.get(i)));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Path{nodes=" + nodes + ", cost=" + totalCost + ", minutes=" + totalMinutes + "}";
    }

}
