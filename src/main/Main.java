package main;

import main.model.Graph;
import main.model.Node;
import main.service.RouteService;
import main.ui.MainFrame;

import javax.swing.SwingUtilities;


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Graph graph = createTestGraph();
            RouteService routeService = new RouteService(graph);

            MainFrame frame = new MainFrame(graph, routeService);
            frame.setVisible(true);
        });
    }

    private static Graph createTestGraph() {
        Graph graph = new Graph();

        graph.addNode(new Node(1, "图书馆", 0, 0));
        graph.addNode(new Node(2, "教学楼", 100, 0));
        graph.addNode(new Node(3, "食堂", 200, 100));
        graph.addNode(new Node(4, "宿舍楼", 300, 100));

        graph.addUndirectedEdge(1, 2, 100);
        graph.addUndirectedEdge(2, 3, 120);
        graph.addUndirectedEdge(3, 4, 80);
        graph.addUndirectedEdge(1, 4, 400);

        return graph;
    }
}

