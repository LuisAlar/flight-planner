classDiagram
    %% =======================
    %% High-level orchestration
    %% =======================
    class Main {
        +FLIGHTS_DATA_PATH : String
        +REQUSTED_PATH : String
        +main(args: String[]) void
        -loadRows() List<Row>
    }

    Main ..> FlightGraph : builds and uses
    Main ..> Request : processes
    Main ..> RequestParser : uses
    Main ..> PathFinder : uses
    Main ..> HeapSort : uses
    Main ..> DataSetFormatter : optional preprocessing

    %% =======================
    %% Graph / adjacency list
    %% =======================
    class FlightGraph {
        -indexOf : Map~String, Integer~
        -cities : List~String~
        -adj : LinkedList~Edge~[]
        +FlightGraph(uniqueCities: Collection~String~)
        +addUndirected(from: String, to: String, cost: double, minutes: int) void
        +cityCount() int
        +indexOf(cityName: String) int
        +nameOf(index: int) String
        +cityNames() List~String~
        +adjacency() LinkedList~Edge~[]
        +prettyPrint() void
    }

    class GraphBuilder {
        +build(rows: List~Row~) FlightGraph
    }

    class Row {
        +from : String
        +to : String
        +cost : double
        +minutes : int
        +Row(from: String, to: String, cost: double, minutes: int)
    }

    class Edge {
        +to : int
        +cost : double
        +minutes : int
        +Edge(to: int, cost: double, minutes: int)
    }

    GraphBuilder ..> Row
    GraphBuilder ..> FlightGraph
    FlightGraph o--> Edge
    FlightGraph o--> "1..*" Row : built from

    %% =======================
    %% Domain: requests & metric
    %% =======================
    class Request {
        -origin : String
        -destination : String
        -metric : Metric
        +Request(origin: String, destination: String, metric: Metric)
        +getOrigin() String
        +getDestination() String
        +getMetric() Metric
    }

    class Metric {
        <<enumeration>>
        TIME
        COST
    }

    Request --> Metric

    %% =======================
    %% Domain: path
    %% =======================
    class Path {
        -nodes : List~Integer~
        -totalCost : double
        -totalMinutes : int
        +Path(nodes: List~Integer~, totalCost: double, totalMinutes: int)
        +getNodes() List~Integer~
        +getTotalCost() double
        +getTotalMinutes() int
        +length() int
        +toRouteString(nameOf: IntFunction~String~) String
    }

    %% =======================
    %% IO / parsing
    %% =======================
    class RequestParser {
        +parse(filePath: String) List~Request~
    }

    Main ..> Row : via loadRows()
    Main ..> RequestParser

    %% =======================
    %% Search / algorithm
    %% =======================
    class PathFinder {
        +findAll(g: FlightGraph, src: int, dst: int) List~Path~
    }

    PathFinder ..> FlightGraph
    PathFinder ..> Path
    PathFinder ..> Edge

    %% =======================
    %% Sorting (HeapSort)
    %% =======================
    class HeapSort {
        +sort~T~(list: List~T~, cmp: Comparator~T~) void
    }

    HeapSort ..> Path : sorts paths
    HeapSort ..> Comparator~Path~ : uses

    %% =======================
    %% Data formatting script
    %% =======================
    class DataSetFormatter {
        +formatRawData(rawInputPath: String, outputFlightsPath: String, filterCriteria: String) void
        %% e.g. reads some external dataset, filters,
        %% and writes lines in "Origin|Destination|Cost|Time" format
    }

    Main ..> DataSetFormatter : pre-process raw data
    DataSetFormatter ..> Row : produces rows compatible with GraphBuilder