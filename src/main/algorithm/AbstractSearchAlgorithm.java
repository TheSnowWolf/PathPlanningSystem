package main.algorithm;

import main.model.Node;
import main.model.Edge;
import main.model.Graph;
import main.model.PathResult;

import java.util.*;

/*
该抽象父类为Dijkstra与A*核心部分，是二者共用的模板
Dijkstra算法为启发函数为0的A*算法
使用继承简化两个算法
**该模板仅用于DIjkstra与A***
**无法处理负权边**
*/

public abstract class AbstractSearchAlgorithm implements PathAlgorithm {

    @Override
    public PathResult findPath(Graph graph, int startId, int endId) {
        long beginTime = System.currentTimeMillis();

        Map<Integer, Double> gScore = new HashMap<>();
        Map<Integer, Integer> prev = new HashMap<>();
        Set<Integer> visited = new HashSet<>();

        //初始化：所有点距离为无穷大
        for (Node node : graph.getAllNodes()) {
            gScore.put(node.getId(), Double.POSITIVE_INFINITY);
        }

        //搜索起点距离为0
        gScore.put(startId, 0.0);

        Node startNode = graph.getNode(startId);
        Node endNode = graph.getNode(endId);

        //使用优先队列，储存当前可选择的最近节点
        PriorityQueue<SearchNode> queue = new PriorityQueue<>(
                Comparator.comparingDouble(SearchNode :: getDistance)
        );

        //初始化启发函数
        double startPriority = heuristic(startNode, endNode);

        //将起点加入优先队列
        queue.add(new SearchNode(startId, startPriority));

        while (!queue.isEmpty()) {
            SearchNode current = queue.poll();
            int currentId = current.getNodeId();

            //对于已经确定最短路径的点，不再进行处理
            if (visited.contains(currentId)) {
                continue;
            }

            visited.add(currentId);

            //已经到终点，提前结束
            if (currentId == endId) {
                break;
            }

            //遍历currentId出发的所有边
            for (Edge edge : graph.getEdges(currentId)) {
                int to = edge.getTo();

                //已经处理完毕的节点，直接跳过
                if (visited.contains(to)) {
                    continue;
                }

                double newG = gScore.get(currentId) + edge.getWeight();

                //松弛操作，将距离最短节点加入队列
                if (newG < gScore.get(to)) {
                    gScore.put(to, newG);
                    prev.put(to, currentId);

                    //核心：更新启发函数
                    Node toNode = graph.getNode(to);
                    double priority = newG + heuristic(toNode, endNode);

                    queue.add(new SearchNode(to, priority));
                }
            }
        }

        List<Node> path = buildPath(graph, prev, startId, endId);

        long endTime = System.currentTimeMillis();

        double totalDistance = gScore.getOrDefault(endId, Double.POSITIVE_INFINITY);

        //从内部类返回空列表，无合法路径，距离无穷
        if (path.isEmpty()) {
            totalDistance = Double.POSITIVE_INFINITY;
        }

        return new PathResult(
                path,
                totalDistance,
                endTime - beginTime,
                visited.size(),
                getName()
        );
    }

    //声明启发函数
    protected abstract double heuristic(Node current, Node end);

    //内部类：根据prev记录，反向还原最短路径
    private List<Node> buildPath(Graph graph, Map<Integer, Integer> prev,
                                 int startId, int endId) {
        List<Node> path = new ArrayList<>();

        //起点终点相同，路径为自己
        if (startId == endId) {
            path.add(graph.getNode(startId));
            return path;
        }

        //初始化，从终点开始回溯
        int current = endId;

        while (current != startId) {
            Node node = graph.getNode(current);

            //节点不存在，路径异常，没有找到合法路径，返回空列表
            if (node == null) {
                return new ArrayList<>();
            }

            path.add(node);

            //如果找不到当前点的前一个点，则无法从终点抵达起点，路径不合法，返回空列表
            if (!prev.containsKey(current)) {
                return new ArrayList<>();
            }

            //回推
            current = prev.get(current);
        }

        //已经到达起点，将起点加入
        path.add(graph.getNode(startId));

        //反转得到正确路径
        Collections.reverse(path);

        return path;
    }

    //内部类：储存当前节点距离
    private static class SearchNode {
        private int nodeId;
        private double priority;

        public SearchNode(int nodeId, double priority) {
            this.nodeId = nodeId;
            this.priority = priority;
        }

        public int getNodeId() {
            return nodeId;
        }

        public double getDistance() {
            return priority;
        }
    }
}
