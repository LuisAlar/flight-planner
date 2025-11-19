package main.io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FlightDataParser {
   public static class Row {
        public final String from, to; public final double cost; public final int minutes;
        public Row(String f, String t, double c, int m) { from=f; to=t; cost=c; minutes=m; }
    }

    public static List<Row> parse(String path) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(path));
        if (lines.isEmpty()) throw new IOException("Empty file: " + path);
        int n = Integer.parseInt(lines.get(0).trim());
        List<Row> rows = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) {
            String[] parts = lines.get(i).trim().split("\\|");
            if (parts.length != 4) throw new IOException("Bad row at line " + (i+1));
            String from = parts[0].trim();
            String to   = parts[1].trim();
            double cost = Double.parseDouble(parts[2].trim());
            int minutes = Integer.parseInt(parts[3].trim());
            rows.add(new Row(from, to, cost, minutes));
        }
        return rows;
    }
}