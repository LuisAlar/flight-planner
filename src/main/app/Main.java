package main.app;

import java.util.ArrayList;
import java.util.List;
import main.graph.FlightGraph;
import main.graph.GraphBuilder;
import main.graph.GraphBuilder.Row;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Main {
    public static void main(String[] args) {
        private static final String FLIGHTS_PATH = "src/main/data/flights.txt";

        List<Row> rows = loadRows();
         
        
        FlightGraph g = GraphBuilder.build(rows);

        System.out.println("Cities: " + g.cityCount());
        System.out.println("City list: " + g.cityNames());
        System.out.println("--- Adjacency ---");
        g.prettyPrint();

        System.out.println("Index of Dallas: " + g.indexOf("Dallas"));
        System.out.println("Index of Houston: " + g.indexOf("Houston"));
    }

    
private static List<Row> loadRows() throws IOException {
        List<FlightDataParser.Row> parsed = FlightDataParser.parse(FLIGHTS_DATA_PATH);
        List<Row> rows = new ArrayList<>(parsed.size());
        for (FlightDataParser.Row r : parsed) {
            rows.add(new Row(r.from, r.to, r.cost, r.minutes));
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
                if (line.isBlank()) {
                    continue;
                }
                int firstSep = line.indexOf('|');
                int secondSep = line.indexOf('|', firstSep + 1);
                int thirdSep = line.indexOf('|', secondSep + 1);
                if (firstSep <= 0 || secondSep <= firstSep || thirdSep <= secondSep) {
                    throw new IOException("Malformed flight row: " + line);
                }
                String from = line.substring(0, firstSep).trim();
                String to = line.substring(firstSep + 1, secondSep).trim();
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
