package main.graph;
import java.util.*;

/** Builds a FlightGraph from (from,to,cost,minutes) rows. */
public final class GraphBuilder {

    public static final class Row {
        public final String from, to;
        public final double cost;
        public final int minutes;

        // assembles
        public Row(String from, String to, double cost, int minutes) {
            this.from = Objects.requireNonNull(from);
            this.to = Objects.requireNonNull(to);
            this.cost = cost;
            this.minutes = minutes;
        }
    }

    public static FlightGraph build(List<Row> rows) {
        // collect unique city names 
        // TreeSet keeps elements in sorted order 
        // gives you a balanced tree, therfore basic operations are O(log n )
        TreeSet<String> unique = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Row r : rows) { unique.add(r.from); unique.add(r.to); }

        FlightGraph g = new FlightGraph(unique);
        for (Row r : rows) g.addUndirected(r.from, r.to, r.cost, r.minutes);
        return g;
    }

    private GraphBuilder() {}
}