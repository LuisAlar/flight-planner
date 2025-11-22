package main.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import main.graph.FlightGraph;
import main.graph.GraphBuilder;
import main.graph.GraphBuilder.Row;
import main.io.RequestParser;
import main.domain.Metric;
import main.domain.Path;
import main.domain.Request;
import main.search.pathFinder;
import main.sort.HeapSort;

public class Main {

    public static final String FLIGHTS_DATA_PATH = "src/main/data/flights.txt";
    public static final String REQUSTED_PATH     = "src/main/data/RequestedPath.txt";

    public static void main(String[] args) throws IOException {
        // 1) Load flight rows and build graph
        List<Row> rows = loadRows();
        FlightGraph g = GraphBuilder.build(rows);

        // 2) Parse requested flights
        List<Request> requests = RequestParser.parse(REQUSTED_PATH);

        System.out.println("=== GRAPH CHECK ===");
        System.out.println("Cities: " + g.cityNames());
        g.prettyPrint();

        System.out.println("\n=== REQUESTS CHECK ===");
        for (int i = 0; i < requests.size(); i++) {
            Request r = requests.get(i);
            System.out.println("Request " + (i + 1) + ": "
                    + r.getOrigin() + " -> " + r.getDestination()
                    + " (" + r.getMetric() + ")");
        }

        // 3) Process each request
        for (int i = 0; i < requests.size(); i++) {
            Request r = requests.get(i);
            Metric m = r.getMetric();

            int src = g.indexOf(r.getOrigin());
            int dst = g.indexOf(r.getDestination());

            // find all paths for this request
            List<Path> allPaths = pathFinder.findAll(g, src, dst);

            System.out.println("\n=== FLIGHT " + (i + 1) + ": "
                    + r.getOrigin() + " to " + r.getDestination()
                    + " (" + m + ") ===");
            System.out.println("Total paths found: " + allPaths.size());

            if (allPaths.isEmpty()) {
                System.out.println("No available flight plan.");
                continue;
            }

            // 4) Choose comparator based on Metric
            Comparator<Path> cmp;
            if (m == Metric.TIME) {
                cmp = Comparator
                        .comparingInt(Path::getTotalMinutes)
                        .thenComparingDouble(Path::getTotalCost);
            } else { // Metric.COST
                cmp = Comparator
                        .comparingDouble(Path::getTotalCost)
                        .thenComparingInt(Path::getTotalMinutes);
            }

            // 5) Sort with your HeapSort
            HeapSort.sort(allPaths, cmp);

            // 6) Print top 3
            int limit = Math.min(3, allPaths.size());
            for (int j = 0; j < limit; j++) {
                Path p = allPaths.get(j);
                System.out.println("Path " + (j + 1) + ": "
                        + p.toRouteString(g::nameOf)
                        + ". Time: " + p.getTotalMinutes()
                        + " Cost: " + String.format("%.2f", p.getTotalCost()));
            }
        }
    }

    private static List<Row> loadRows() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(
                Paths.get(FLIGHTS_DATA_PATH), StandardCharsets.UTF_8)) {

            String header = reader.readLine();
            if (header == null) {
                throw new IOException("Flights file is empty: " + FLIGHTS_DATA_PATH);
            }

            int expectedEntries = Integer.parseInt(header.trim());
            List<Row> rows = new ArrayList<>(expectedEntries);

            String line;
            while ((line = reader.readLine()) != null && rows.size() < expectedEntries) {
                if (line.isBlank()) continue;

                int firstSep = line.indexOf('|');
                int secondSep = line.indexOf('|', firstSep + 1);
                int thirdSep = line.indexOf('|', secondSep + 1);

                if (firstSep < 0 || secondSep < 0 || thirdSep < 0) {
                    throw new IOException("Malformed flight row: " + line);
                }

                String from = line.substring(0, firstSep).trim();
                String to   = line.substring(firstSep + 1, secondSep).trim();
                double cost = Double.parseDouble(line.substring(secondSep + 1, thirdSep).trim());
                int minutes = Integer.parseInt(line.substring(thirdSep + 1).trim());

                rows.add(new Row(from, to, cost, minutes));
            }

            if (rows.size() != expectedEntries) {
                throw new IOException(
                        "Expected " + expectedEntries + " entries but found " + rows.size());
            }

            return rows;
        }
    }
}
