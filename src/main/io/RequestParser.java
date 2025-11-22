package main.io;

import main.domain.Metric;
import main.domain.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class RequestParser {

    /**
     * Reads a requested-flights file with format:
     *   N
     *   Origin|Destination|T_or_C
     *   ...
     * and returns a list of Request objects.
     */
    public static List<Request> parse(String filePath) throws IOException {
        Path path = Path.of(filePath);
        List<Request> requests = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line = br.readLine();
            if (line == null) {
                throw new IOException("Empty requests file: " + filePath);
            }

            int expectedCount;
            try {
                expectedCount = Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                throw new IOException("First line must be an integer (number of requests). Got: " + line);
            }

            int lineNum = 1;
            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) continue; // skip blank lines

                String[] parts = line.split("\\|");
                if (parts.length != 3) {
                    throw new IOException("Bad format at line " + lineNum + ": " + line);
                }

                String origin = parts[0].trim();
                String dest   = parts[1].trim();
                String metricChar = parts[2].trim().toUpperCase();

                Metric metric;
                if (metricChar.equals("T")) {
                    metric = Metric.TIME;
                } else if (metricChar.equals("C")) {
                    metric = Metric.COST;
                } else {
                    throw new IOException("Unknown metric '" + metricChar + "' at line " + lineNum);
                }

                requests.add(new Request(origin, dest, metric));
            }

            if (requests.size() != expectedCount) {
                // not fatal, but useful: up to you if you want to throw or just warn
                System.err.println("Warning: expected " + expectedCount +
                                   " requests, but found " + requests.size());
            }
        }

        return requests;
    }

    private RequestParser() {}
}
