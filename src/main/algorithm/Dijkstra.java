package main.algorithm;

import main.model.Node;
import main.model.Edge;
import main.model.Graph;
import main.model.PathResult;

import java.util.*;

/*
Ver 26.5.30 实现基础Dijkstra，路径回推
*/

public class Dijkstra {

    //使用Dijkstra方法进行处理
    //**注意：无法处理负权边**
    public PathResult findPath(Graph graph, int startId, int endId) {
        long beginTime = System.currentTimeMillis();

        Map<Integer, Double> dist = new HashMap<>();
        Map<Integer, Integer> prev = new HashMap<>();
        Set<Integer> visited = new HashSet<>();

        //初始化：所有点距离为无穷大
        for (Node node : graph.getAllNodes()) {
            dist.put(node.getId(), Double.POSITIVE_INFINITY);
        }

        //搜索起点距离为0
        dist.put(startId, 0.0);

        //使用优先队列，储存当前可选择的最近节点
        PriorityQueue<NodeDistance> queue = new PriorityQueue<>(
                Comparator.comparingDouble(NodeDistance :: getDistance)
        );

        //将起点加入优先队列
        queue.add(new NodeDistance(startId, 0.0));

        while (!queue.isEmpty()) {
            NodeDistance current = queue.poll();
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

            //核心：根据Dijkstra算法，遍历currentId出发的所有边，贪心方法取得最短路径
            for (Edge edge : graph.getEdges(currentId)) {
                int to = edge.getTo();
                double weight = edge.getWeight();

                //已经处理完毕的节点，直接跳过
                if (visited.contains(to)) {
                    continue;
                }

                double newDistance = dist.get(currentId) + weight;

                //松弛操作，将距离最短节点加入队列
                if (newDistance < dist.get(to)) {
                    dist.put(to, newDistance);
                    prev.put(to, currentId);
                    queue.add(new NodeDistance(to, newDistance));
                }
            }
        }

        List<Node> path = buildPath(graph, prev, startId, endId);

        long endTime = System.currentTimeMillis();

        double totalDistance = dist.getOrDefault(endId, Double.POSITIVE_INFINITY);

        //从内部类返回空列表，无合法路径，距离无穷
        if (path.isEmpty()) {
           totalDistance = Double.POSITIVE_INFINITY;
        }

        return new PathResult(
                path,
                totalDistance,
                endTime - beginTime,
                visited.size(),
                "Dijkstra"
        );
    }

    //内部类：根据prev记录，反向还原Dijkstra推得的最短路径
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
    private static class NodeDistance {
        private int nodeId;
        private double distance;

        public NodeDistance(int nodeId, double distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }

        public int getNodeId() {
            return nodeId;
        }

        public double getDistance() {
            return distance;
        }
    }
 }


