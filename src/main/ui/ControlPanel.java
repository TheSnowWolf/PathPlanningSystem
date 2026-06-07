package main.ui;

import main.model.Graph;
import main.model.Node;
import main.model.PathResult;
import main.service.RouteService;
import org.w3c.dom.traversal.NodeIterator;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

/*
26.6.7 ControlPanel负责选择起点，终点，算法
*/

public class ControlPanel extends JPanel {
    private Graph graph;
    private RouteService routeService;
    private MapPanel mapPanel;
    private ResultPanel resultPanel;

    private JComboBox<NodeItem> startBox;
    private JComboBox<NodeItem> endBox;
    private JComboBox<String> algorithmBox;

    public ControlPanel(Graph graph, RouteService routeService,
                        MapPanel mapPanel, ResultPanel resultPanel) {
        this.graph = graph;
        this.routeService = routeService;
        this.mapPanel = mapPanel;
        this.resultPanel = resultPanel;

        initComponents();
    }

    private void initComponents() {
        setPreferredSize(new Dimension(220, 0));
        setLayout(new GridLayout(8, 1, 5, 5));

        startBox = new JComboBox<>();
        endBox = new JComboBox<>();
        algorithmBox = new JComboBox<>(new String[]{"Dijkstra", "A*"});

        for (Node node : graph.getAllNodes()) {
            startBox.addItem(new NodeItem(node));
            endBox.addItem(new NodeItem(node));
        }

        JButton findButton = new JButton("开始寻路");
        findButton.addActionListener(e -> findPath());

        add(new JLabel("起点"));
        add(startBox);
        add(new JLabel("终点"));
        add(endBox);
        add(new JLabel("算法"));
        add(algorithmBox);
        add(findButton);
    }

    private void findPath() {
        try {
            NodeItem start = (NodeItem) startBox.getSelectedItem();
            NodeItem end = (NodeItem) endBox.getSelectedItem();
            String algorithm = (String) algorithmBox.getSelectedItem();

            PathResult result = routeService.findPath(
                    start.getNode().getId(),
                    end.getNode().getId(),
                    algorithm
            );

            mapPanel.setPath(result.getPath());
            resultPanel.showResult(result);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class NodeItem {
        private Node node;

        public NodeItem(Node node) {
            this.node = node;
        }

        public Node getNode() {
            return node;
        }

        @Override
        public String toString() {
            return node.getName();
        }
    }
}
