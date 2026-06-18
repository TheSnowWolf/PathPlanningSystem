package main.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
Graph 图结构类

功能：
1. 使用邻接表存储图
2. 使用 LinkedHashMap 保证节点遍历顺序稳定
3. 支持有向边与无向边
4. 在添加边时检查负权边和非法节点

说明：
ver6.16 Dijkstra 和 A* 都要求边权不能为负数，因此在图结构层进行检查。
*/

public class Graph {
    // 节点编号 -> 节点对象
    private final Map<Integer, Node> nodeMap = new LinkedHashMap<>();

    // 节点编号 -> 从该节点出发的所有边
    private final Map<Integer, List<Edge>> adjacencyList = new LinkedHashMap<>();

    public void addNode(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("节点不能为空");
        }

        nodeMap.put(node.getId(), node);
        adjacencyList.putIfAbsent(node.getId(), new ArrayList<>());
    }

    // 加入有向边
    public void addEdge(int from, int to, double weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Dijkstra 和 A* 不支持负权边");
        }

        if (!nodeMap.containsKey(from)) {
            throw new IllegalArgumentException("边的起点不存在：" + from);
        }

        if (!nodeMap.containsKey(to)) {
            throw new IllegalArgumentException("边的终点不存在：" + to);
        }

        Edge edge = new Edge(from, to, weight);
        adjacencyList.get(from).add(edge);
    }

    // 加入无向边
    public void addUndirectedEdge(int a, int b, double weight) {
        addEdge(a, b, weight);
        addEdge(b, a, weight);
    }

    public Node getNode(int id) {
        return nodeMap.get(id);
    }

    public Collection<Node> getAllNodes() {
        return nodeMap.values();
    }

    public List<Edge> getEdges(int nodeId) {
        return adjacencyList.getOrDefault(nodeId, new ArrayList<>());
    }

    public boolean containsNode(int id) {
        return !nodeMap.containsKey(id);
    }

    public boolean isEmpty() {
        return nodeMap.isEmpty();
    }

}