package main.ui;

import main.model.Edge;
import main.model.Graph;
import main.model.Node;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/*
26.6.7 MapPanel负责画图与高亮路径，绘制遍历

**无向边会被绘制两次**
*/

public class MapPanel extends JPanel{
    private Graph graph;
    private List<Node> path = new ArrayList<>();

    public MapPanel(Graph graph) {
        this.graph = graph;
        setBackground(Color.WHITE);
    }

    public void setPath(List<Node> path) {
        this.path = path;
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

        drawEdges(g);
        drawPath(g);
        drawNodes(g);
    }

    private void drawEdges(Graphics g) {
        g.setColor(Color.GRAY);

        for (Node node : graph.getAllNodes()) {
            for (Edge edge : graph.getEdges(node.getId())) {
                Node from = graph.getNode(edge.getFrom());
                Node to = graph.getNode(edge.getTo());

                if (from != null && to != null) {
                    g.drawLine((int) from.getX() + 50, (int) from.getY() + 50,
                               (int) to.getX() + 50, (int) to.getY() + 50);
                }
            }
        }
    }

    private void drawNodes(Graphics g) {
        for (Node node : graph.getAllNodes()) {
            int x = (int) node.getX() + 50;
            int y = (int) node.getY() + 50;

            g.setColor(Color.BLUE);
            g.fillOval(x - 8, y - 8, 16, 16);

            g.setColor(Color.BLACK);
            g.drawString(node.getName(), x + 10, y);
        }
    }

    private void drawPath(Graphics g) {
        if (path == null || path.size() < 2) {
            return ;
        }

        g.setColor(Color.RED);

        for (int i = 0; i < path.size() - 1; ++ i) {
            Node a = path.get(i);
            Node b = path.get(i + 1);

            g.drawLine((int) a.getX() + 50, (int) a.getY() + 50,
                       (int) b.getX() + 50, (int) b.getY() + 50);
        }
    }
}
