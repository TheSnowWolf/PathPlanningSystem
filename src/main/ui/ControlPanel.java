package main.ui;

import main.model.Graph;
import main.model.Node;
import main.model.PathResult;
import main.service.RouteService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

/*
26.6.7 ControlPanel负责选择起点，终点，算法
26.6.13 添加清除路径与算法对比
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
        setLayout(new GridLayout(0, 1, 5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        startBox = new JComboBox<>();
        endBox = new JComboBox<>();
        algorithmBox = new JComboBox<>(routeService.getAlgorithmNames());

        for (Node node : graph.getAllNodes()) {
            startBox.addItem(new NodeItem(node));
            endBox.addItem(new NodeItem(node));
        }

        JButton findButton = new JButton("开始寻路");
        JButton clearButton = new JButton("清除路径");
        JButton compareButton = new JButton("算法对比");

        findButton.addActionListener(e -> findPath());
        clearButton.addActionListener(e -> clearPath());
        compareButton.addActionListener(e -> compareAlgorithms());

        add(new JLabel("起点"));
        add(startBox);
        add(new JLabel("终点"));
        add(endBox);
        add(new JLabel("算法"));
        add(algorithmBox);
        add(findButton);
        add(clearButton);
        add(compareButton);
    }

    public void findPath() {
        try {
            Node startNode = getSelectedStartNode();
            Node endNode = getSelectedEndNode();
            String algorithm = (String) algorithmBox.getSelectedItem();

            PathResult result = routeService.findPath(
                    startNode.getId(),
                    endNode.getId(),
                    algorithm
            );

            mapPanel.setPath(result.getPath());
            resultPanel.showResult(result);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearPath() {
        mapPanel.setPath(new ArrayList<>());
        resultPanel.clear();
    }

    public void compareAlgorithms() {
        try {
            Node startNode = getSelectedStartNode();
            Node endNode = getSelectedEndNode();

            Map<String, PathResult> results = routeService.compareAlgorithms(
                    startNode.getId(),
                    endNode.getId()
            );

            resultPanel.showCompareResult(results);

            String selectedAlgorithm = (String) algorithmBox.getSelectedItem();
            PathResult selectedResult = results.get(selectedAlgorithm);

            if (selectedResult != null) {
                mapPanel.setPath(selectedResult.getPath());
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private Node getSelectedStartNode() {
        NodeItem start = (NodeItem) startBox.getSelectedItem();

        if (start == null) {
            throw new IllegalArgumentException("请选择起点");
        }

        return start.getNode();
    }

    private Node getSelectedEndNode() {
        NodeItem end = (NodeItem) endBox.getSelectedItem();

        if (end == null) {
            throw new IllegalArgumentException("请选择终点");
        }

        return end.getNode();
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE
        );
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