package main.model;

import java.util.*;

public class Graph {
    //使用邻接表存图
    //nodeMap: 节点编号 -> 节点对象
    //hashMap: 节点编号 -> 从这个点出发的所有边
    private Map<Integer, Node> nodeMap = new HashMap<>();
    private Map<Integer, List<Edge>> adjacencyList = new HashMap<>();

    public void addNode(Node node) {
        nodeMap.put(node.getId(), node);
        adjacencyList.put(node.getId(), new ArrayList<>());
    }

    //加入有向边
    public void addEdge(int from, int to, double weight) {
        Edge edge = new Edge(from, to, weight);
        adjacencyList.putIfAbsent(from, new ArrayList<>());
        adjacencyList.get(from).add(edge); //对于编号为get(from)的节点，把from -> to的边加入
    }

    //加入无向边
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
        return nodeMap.containsKey(id);
    }
}
