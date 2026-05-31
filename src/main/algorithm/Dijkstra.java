package main.algorithm;

import main.model.Node;

/*
Ver 26.5.30 实现基础Dijkstra，路径回推
Ver 26.5.31 将Dijkstra的模板移至AbstractSearchAlgorithm，减少代码重复
*/

public class Dijkstra extends AbstractSearchAlgorithm {

    //Dijkstra是启发函数为0的A*
    @Override
    protected double heuristic(Node current, Node end) {
        return 0;
    }

    @Override
    public String getName() {
        return "Dijkstra";
    }
 }


