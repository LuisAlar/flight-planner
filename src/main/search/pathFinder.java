package main.search;

import main.domain.Edge;
import main.domain.Path;
import main.graph.FlightGraph;

import java.util.*;

public final class pathFinder {

    private static final class Frame {
        int node;              // current city index
        int nextEdgeIndex;     // which neighbor index to try next

        Frame(int node, int nextEdgeIndex) {
            this.node = node;
            this.nextEdgeIndex = nextEdgeIndex;
        }
    }

    /**
     * Enumerate ALL simple paths (no repeated city) from src to dst using
     * iterative DFS with an explicit stack (no recursion).
     */
    public static List<Path> findAll(FlightGraph g, int src, int dst) {
        List<Path> results = new ArrayList<>();

        @SuppressWarnings("unchecked")
        List<Edge>[] adj = g.adjacency();

        int n = g.cityCount();
        boolean[] visited = new boolean[n];
        List<Integer> nodesOnPath = new ArrayList<>();
        double currentCost = 0.0;
        int currentMinutes = 0;

        Deque<Frame> stack = new ArrayDeque<>();

        visited[src] = true;
        nodesOnPath.add(src);
        stack.push(new Frame(src, 0));

        while (!stack.isEmpty()) {
            Frame f = stack.peek();
            int u = f.node;

            // If we've reached the destination, record the current path
            if (u == dst) {
                results.add(new Path(nodesOnPath, currentCost, currentMinutes));

                // Backtrack: pop and undo last step
                stack.pop();
                int removed = nodesOnPath.remove(nodesOnPath.size() - 1);
                visited[removed] = false;

                // also need to subtract cost/time of the last edge taken
                if (!nodesOnPath.isEmpty()) {
                    int prev = nodesOnPath.get(nodesOnPath.size() - 1);
                    Edge lastEdge = findEdge(adj, prev, removed);
                    currentCost -= lastEdge.cost;
                    currentMinutes -= lastEdge.minutes;
                }
                continue;
            }

            // If we've tried all neighbors from u, backtrack
            if (f.nextEdgeIndex >= adj[u].size()) {
                stack.pop();
                int removed = nodesOnPath.remove(nodesOnPath.size() - 1);
                visited[removed] = false;

                if (!nodesOnPath.isEmpty()) {
                    int prev = nodesOnPath.get(nodesOnPath.size() - 1);
                    Edge lastEdge = findEdge(adj, prev, removed);
                    currentCost -= lastEdge.cost;
                    currentMinutes -= lastEdge.minutes;
                }
                continue;
            }

            // Otherwise, try the next neighbor
            Edge e = adj[u].get(f.nextEdgeIndex++);
            int v = e.to;
            if (!visited[v]) {
                visited[v] = true;
                nodesOnPath.add(v);
                currentCost += e.cost;
                currentMinutes += e.minutes;
                stack.push(new Frame(v, 0));
            }
        }

        return results;
    }

    // Helper: find the edge u->v in adjacency list adj[u]
    private static Edge findEdge(List<Edge>[] adj, int u, int v) {
        for (Edge e : adj[u]) {
            if (e.to == v) return e;
        }
        throw new IllegalStateException("Edge not found between " + u + " and " + v);
    }

    private pathFinder() {}
}
