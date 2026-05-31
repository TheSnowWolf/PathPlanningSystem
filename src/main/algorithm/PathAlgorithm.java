package main.algorithm;

import main.model.Graph;
import main.model.PathResult;

public interface PathAlgorithm {
    PathResult findPath(Graph graph, int startId, int endId);

    String getName();
}
