package main.algorithm;

import main.model.Node;

public class AStar extends AbstractSearchAlgorithm {

    @Override
    protected double heuristic(Node current, Node end) {
        if (current == null || end == null) {
           return 0;
        }

        //使用欧几里得距离作为启发函数
        //对于大多数图论最短路问题具有普适性
        double dx = current.getX() - end.getX();
        double dy = current.getY() - end.getY();

        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String getName() {
        return "A*";
    }
}
