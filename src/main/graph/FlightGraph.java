package main.graph;

import java.util.*;
import main.domain.*;


    /** Array-of-linked-lists adjacency graph using String city names. */
    public class FlightGraph {
    // map normalized city name -> index
    private final Map<String,Integer> indexOf = new HashMap<>();  //name of city
    // index -> display city name
    private final List<String> cities = new ArrayList<>();
    // adjacency lists
    private final LinkedList<Edge>[] adj;

    @SuppressWarnings("unchecked")
    public FlightGraph(Collection<String> uniqueCities) {
        for (String name : uniqueCities) {
            String disp = displayName(name);
            String key  = norm(name);
            indexOf.put(key, cities.size());
            cities.add(disp);
        }
        adj = new LinkedList[cities.size()];
        for (int i = 0; i < adj.length; i++) adj[i] = new LinkedList<>();
    }

    /** Add bidirectional edge. City names must already exist in this graph. */
    public void addUndirected(String from, String to, double cost, int minutes) {
        int u = requireIndex(from);
        int v = requireIndex(to);
        adj[u].add(new Edge(v, cost, minutes));
        adj[v].add(new Edge(u, cost, minutes));
    }

    public int cityCount() { return adj.length; }
    public int indexOf(String city) { return requireIndex(city); }
    public String nameOf(int idx) { return cities.get(idx); }
    public LinkedList<Edge>[] adjacency() { return adj; }
    public List<String> cityNames() { return Collections.unmodifiableList(cities); }

    public void prettyPrint() {
        for (int i = 0; i < adj.length; i++) {
            System.out.print(nameOf(i) + " -> ");
            Iterator<Edge> it = adj[i].iterator();
            while (it.hasNext()) {
                Edge e = it.next();
                System.out.print(nameOf(e.to) + "($" + e.cost + "," + e.minutes + "m)");
                if (it.hasNext()) System.out.print(" , ");
            }
            System.out.println();
        }
    }

    private int requireIndex(String name) {
        Integer idx = indexOf.get(norm(name));
        if (idx == null) throw new IllegalArgumentException("City not in graph: " + name);
        return idx;
    }

    private static String norm(String s) {
        return s.trim().toLowerCase(Locale.ROOT);
    }

    private static String displayName(String s) {
        String t = s.trim().toLowerCase(Locale.ROOT);
        String[] parts = t.split("\\s+");
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty()) continue;
            b.append(Character.toUpperCase(parts[i].charAt(0)));
            if (parts[i].length() > 1) b.append(parts[i].substring(1));
            if (i + 1 < parts.length) b.append(' ');
        }
        return b.toString();
    }
}

