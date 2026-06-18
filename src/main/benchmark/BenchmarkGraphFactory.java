package main.benchmark;

import main.model.Graph;
import main.model.Node;

/*
BenchmarkGraphFactory
用于生成算法性能测试图。

测试图类型：
8方向网格图。

特点：
1. 每个节点最多连接周围 8 个方向
2. 横向、纵向边权为 1
3. 对角线边权为 sqrt(2)
4. 节点坐标与网格坐标一致
5. A* 使用欧几里得距离作为启发函数时效果明显

为什么这个数据能体现差距：
Dijkstra 不知道终点方向，会从起点向四周扩散；
A* 会利用坐标启发函数，优先朝右下角终点搜索。
*/

public class BenchmarkGraphFactory {
    private static final double STRAIGHT_WEIGHT = 1.0;
    private static final double DIAGONAL_WEIGHT = Math.sqrt(2.0);

    private BenchmarkGraphFactory() {
    }

    public static Graph createEightDirectionGridGraph(int rows, int cols) {
        validateSize(rows, cols);

        Graph graph = new Graph();

        addNodes(graph, rows, cols);
        addEdges(graph, rows, cols);

        return graph;
    }

    private static void addNodes(Graph graph, int rows, int cols) {
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                int id = getNodeId(row, col, cols);
                String name = "N" + id;

                graph.addNode(new Node(id, name, col, row));
            }
        }
    }

    private static void addEdges(Graph graph, int rows, int cols) {
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                int currentId = getNodeId(row, col, cols);

                // 向右
                if (col + 1 < cols) {
                    int rightId = getNodeId(row, col + 1, cols);
                    graph.addUndirectedEdge(currentId, rightId, STRAIGHT_WEIGHT);
                }

                // 向下
                if (row + 1 < rows) {
                    int downId = getNodeId(row + 1, col, cols);
                    graph.addUndirectedEdge(currentId, downId, STRAIGHT_WEIGHT);
                }

                // 右下对角线
                if (row + 1 < rows && col + 1 < cols) {
                    int rightDownId = getNodeId(row + 1, col + 1, cols);
                    graph.addUndirectedEdge(currentId, rightDownId, DIAGONAL_WEIGHT);
                }

                // 左下对角线
                if (row + 1 < rows && col - 1 >= 0) {
                    int leftDownId = getNodeId(row + 1, col - 1, cols);
                    graph.addUndirectedEdge(currentId, leftDownId, DIAGONAL_WEIGHT);
                }
            }
        }
    }

    public static int getNodeId(int row, int col, int cols) {
        return row * cols + col + 1;
    }

    public static long estimateDirectedEdgeCount(int rows, int cols) {
        validateSize(rows, cols);

        long horizontal = (long) rows * (cols - 1);
        long vertical = (long) (rows - 1) * cols;
        long diagonalRightDown = (long) (rows - 1) * (cols - 1);
        long diagonalLeftDown = (long) (rows - 1) * (cols - 1);

        long undirectedEdgeCount =
                horizontal + vertical + diagonalRightDown + diagonalLeftDown;

        return undirectedEdgeCount * 2;
    }

    private static void validateSize(int rows, int cols) {
        if (rows <= 1 || cols <= 1) {
            throw new IllegalArgumentException("行数和列数必须大于 1");
        }
    }
}