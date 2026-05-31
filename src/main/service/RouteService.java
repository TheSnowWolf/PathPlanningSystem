package main.service;

import main.model.Graph;
import main.model.PathResult;
import main.algorithm.*;

import java.util.HashMap;
import java.util.Map;

/*
使用Map管理算法负责统一调用算法
ver26.5.31 添加调用Dijkstra，A*
*/

public class RouteService {
    private Graph graph;
    private Map<String, PathAlgorithm> algorithmMap = new HashMap<>();

    public RouteService(Graph graph) {
        this.graph = graph;

        algorithmMap.put("Dijkstra", new Dijkstra());
        algorithmMap.put("A*", new AStar());
    }

    public PathResult findPath(int startId, int endId, String algorithmName) {
        if (!graph.containsNode(startId)) {
            throw new IllegalArgumentException("起点不存在：" + startId);
        }

        if (!graph.containsNode(endId)) {
            throw new IllegalArgumentException("终点不存在：" + endId);
        }

        PathAlgorithm algorithm = algorithmMap.get(algorithmName);

        if (algorithm == null) {
            throw new IllegalArgumentException("未知算法：" + algorithmName);
        }

        return algorithm.findPath(graph, startId, endId);
    }
}
