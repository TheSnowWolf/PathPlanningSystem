package main.ui;

import main.model.Edge;
import main.model.Graph;
import main.model.Node;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
26.6.16 修复无向边重复绘制，增强路径显示效果
26.6.7 MapPanel负责画图与高亮路径

说明：
数据库中无向边以 a->b 和 b->a 两条有向边保存。
绘制地图时需要去重，避免同一条道路被重复绘制。
*/

public class MapPanel extends JPanel {
    private static final int OFFSET_X = 50;
    private static final int OFFSET_Y = 50;
    private static final int NODE_RADIUS = 8;

    private Graph graph;
    private List<Node> path = new ArrayList<>();

    public MapPanel(Graph graph) {
        this.graph = graph;
        setBackground(Color.WHITE);
    }

    public void setPath(List<Node> path) {
        if (path == null) {
            this.path = new ArrayList<>();
        } else {
            this.path = path;
        }

        repaint();
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
        this.path = new ArrayList<>();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (graph == null || graph.isEmpty()) {
            drawEmptyMessage(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();

        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            drawEdges(g2);
            drawPath(g2);
            drawNodes(g2);
        } finally {
            g2.dispose();
        }
    }

    private void drawEmptyMessage(Graphics g) {
        g.setColor(Color.GRAY);
        g.drawString("暂无地图数据", 30, 30);
    }

    private void drawEdges(Graphics2D g2) {
        g2.setColor(Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(1));

        Set<String> drawnEdges = new HashSet<>();

        for (Node node : graph.getAllNodes()) {
            for (Edge edge : graph.getEdges(node.getId())) {
                int fromId = edge.getFrom();
                int toId = edge.getTo();

                String edgeKey = buildUndirectedEdgeKey(fromId, toId);

                if (drawnEdges.contains(edgeKey)) {
                    continue;
                }

                drawnEdges.add(edgeKey);

                Node from = graph.getNode(fromId);
                Node to = graph.getNode(toId);

                if (from != null && to != null) {
                    g2.drawLine(
                            transformX(from),
                            transformY(from),
                            transformX(to),
                            transformY(to)
                    );

                    drawWeight(g2, from, to, edge.getWeight());
                }
            }
        }
    }

    private void drawPath(Graphics2D g2) {
        if (path == null || path.size() < 2) {
            return;
        }

        Stroke oldStroke = g2.getStroke();

        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(3));

        for (int i = 0; i < path.size() - 1; ++i) {
            Node a = path.get(i);
            Node b = path.get(i + 1);

            g2.drawLine(
                    transformX(a),
                    transformY(a),
                    transformX(b),
                    transformY(b)
            );
        }

        g2.setStroke(oldStroke);
    }

    private void drawNodes(Graphics2D g2) {
        for (Node node : graph.getAllNodes()) {
            int x = transformX(node);
            int y = transformY(node);

            if (isNodeInPath(node)) {
                g2.setColor(Color.RED);
            } else {
                g2.setColor(Color.BLUE);
            }

            g2.fillOval(
                    x - NODE_RADIUS,
                    y - NODE_RADIUS,
                    NODE_RADIUS * 2,
                    NODE_RADIUS * 2
            );

            g2.setColor(Color.BLACK);
            g2.drawString(node.getName(), x + 10, y);
        }
    }

    private void drawWeight(Graphics2D g2, Node from, Node to, double weight) {
        int midX = (transformX(from) + transformX(to)) / 2;
        int midY = (transformY(from) + transformY(to)) / 2;

        g2.setColor(Color.DARK_GRAY);
        g2.drawString(String.format("%.0f", weight), midX, midY);
        g2.setColor(Color.LIGHT_GRAY);
    }

    private boolean isNodeInPath(Node node) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        return path.contains(node);
    }

    private String buildUndirectedEdgeKey(int a, int b) {
        int min = Math.min(a, b);
        int max = Math.max(a, b);

        return min + "-" + max;
    }

    private int transformX(Node node) {
        return (int) node.getX() + OFFSET_X;
    }

    private int transformY(Node node) {
        return (int) node.getY() + OFFSET_Y;
    }
}