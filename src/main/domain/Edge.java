package main.domain;

public final class Edge {
    public final int to;
    public final double cost;
    public final int minutes;

    // creates Edge object for list
    public Edge(int to , double cost, int minute ){
        this.to = to;
        this.cost = cost;
        this.minutes = minute;
    }

     @Override public String toString() {
        return "(to=" + to + ", $" + cost + ", " + minutes + "m)";
    }
}