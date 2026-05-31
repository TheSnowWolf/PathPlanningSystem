package main;

import main.model.Graph;
import main.model.Node;
import main.model.PathResult;
import main.service.RouteService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Graph graph = new Graph();

        graph.addNode(new Node(1, "图书馆", 0, 0));
        graph.addNode(new Node(2, "教学楼", 100, 0));
        graph.addNode(new Node(3, "食堂", 200, 100));
        graph.addNode(new Node(4, "宿舍楼", 300, 100));

        graph.addUndirectedEdge(1, 2, 100);
        graph.addUndirectedEdge(2, 3, 120);
        graph.addUndirectedEdge(3, 4, 80);
        graph.addUndirectedEdge(1, 4, 400);

        RouteService routeService = new RouteService(graph);

        PathResult result = routeService.findPath(1, 4, "Dijkstra");

        printResult(result);
    }

    private static void printResult(PathResult result) {
        if (result.getPath().isEmpty()) {
            System.out.println("没有找到路径");
            return;
        }

        System.out.println("算法：" + result.getAlgorithmName());
        System.out.println("总距离：" + result.getTotalDistance());
        System.out.println("耗时：" + result.getTimeMillis() + " ms");
        System.out.println("访问节点数：" + result.getVisitedCount());

        System.out.print("路径：");

        List<Node> path = result.getPath();

        for (int i = 0; i < path.size(); i++) {
            System.out.print(path.get(i).getName());

            if (i != path.size() - 1) {
                System.out.print(" -> ");
            }
        }
    }
}

