package main.model;

import java.util.List;

public class PathResult {
    private final List<Node> path;
    private final double totalDistance;
    private final long timeMillis;
    private final int visitedCount;
    private final String algorithmName;

    public PathResult(List<Node> path, double totalDistance, long timeMillis,
                      int visitedCount, String algorithmName) {
        this.path = path;
        this.totalDistance = totalDistance;
        this.timeMillis = timeMillis;
        this.visitedCount = visitedCount;
        this.algorithmName = algorithmName;
    }

    public List<Node> getPath() {
        return path;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public int getVisitedCount() {
        return visitedCount;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }
}
