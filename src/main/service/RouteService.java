package main.service;

import main.algorithm.*;
import main.model.Graph;
import main.model.PathResult;

import java.util.LinkedHashMap;
import java.util.Map;

/*
使用Map管理算法，负责统一调用算法
ver26.6.13 添加setGraph，用于数据库重新加载地图
ver26.6.13 添加算法对比接口
ver26.5.31 添加调用Dijkstra，A*
*/

public class RouteService {
    private Graph graph;
    private final Map<String, PathAlgorithm> algorithmMap = new LinkedHashMap<>();

    public RouteService(Graph graph) {
        this.graph = graph;

        algorithmMap.put("Dijkstra", new Dijkstra());
        algorithmMap.put("A*", new AStar());
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public PathResult findPath(int startId, int endId, String algorithmName) {
        validateNode(startId, endId);

        PathAlgorithm algorithm = algorithmMap.get(algorithmName);

        if (algorithm == null) {
            throw new IllegalArgumentException("未知算法：" + algorithmName);
        }

        return algorithm.findPath(graph, startId, endId);
    }

    public Map<String, PathResult> compareAlgorithms(int startId, int endId) {
        validateNode(startId, endId);

        Map<String, PathResult> results = new LinkedHashMap<>();

        for (Map.Entry<String, PathAlgorithm> entry : algorithmMap.entrySet()) {
            String algorithmName = entry.getKey();
            PathAlgorithm algorithm = entry.getValue();

            PathResult result = algorithm.findPath(graph, startId, endId);
            results.put(algorithmName, result);
        }

        return results;
    }

    public String[] getAlgorithmNames() {
        return algorithmMap.keySet().toArray(new String[0]);
    }

    private void validateNode(int startId, int endId) {
        if (graph.containsNode(startId)) {
            throw new IllegalArgumentException("起点不存在：" + startId);
        }

        if (graph.containsNode(endId)) {
            throw new IllegalArgumentException("终点不存在：" + endId);
        }
    }
}