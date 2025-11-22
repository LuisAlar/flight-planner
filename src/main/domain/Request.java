package main.domain;

public final class Request {
    private final String origin;
    private final String destination;
    private final Metric metric;  // TIME or COST

    public Request(String origin, String destination, Metric metric) {
        this.origin = origin.trim();
        this.destination = destination.trim();
        this.metric = metric;
    }

    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public Metric getMetric() { return metric; }

    @Override
    public String toString() {
        return "Request{" + origin + " -> " + destination + ", " + metric + "}";
    }
}

