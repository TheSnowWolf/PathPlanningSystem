package main.benchmark;

import main.algorithm.AStar;
import main.algorithm.Dijkstra;
import main.algorithm.PathAlgorithm;
import main.model.Graph;
import main.model.PathResult;

/*
AlgorithmBenchmarkMain
算法性能测试入口。

运行方式：
直接运行本类 main 方法。

默认会测试：
100 x 100
200 x 200
300 x 300
500 x 500
1000 x 1000

也可以通过命令行参数指定规模：
AlgorithmBenchmarkMain 500 500

注意：
这个测试不经过 Swing 界面，不经过 SQLite 数据库，
只测试算法本身的性能。
*/

public class AlgorithmBenchmarkMain {
    private static final int[][] DEFAULT_CASES = {
            {100, 100},
            {200, 200},
            {300, 300},
            {500, 500},
            {1000, 1000}
    };

    public static void main(String[] args) {
        printTitle();

        warmUp();

        if (args.length == 2) {
            int rows = Integer.parseInt(args[0]);
            int cols = Integer.parseInt(args[1]);
            runCase(rows, cols);
            return;
        }

        for (int[] testCase : DEFAULT_CASES) {
            runCase(testCase[0], testCase[1]);
        }
    }

    private static void printTitle() {
        System.out.println("======================================================");
        System.out.println("Dijkstra 与 A* 大规模图性能测试");
        System.out.println("测试图：8方向网格图");
        System.out.println("边权：横竖 = 1，对角线 = sqrt(2)");
        System.out.println("起点：左上角");
        System.out.println("终点：右下角");
        System.out.println("======================================================");
        System.out.println();
    }

    private static void warmUp() {
        Graph graph = BenchmarkGraphFactory.createEightDirectionGridGraph(30, 30);

        int startId = BenchmarkGraphFactory.getNodeId(0, 0, 30);
        int endId = BenchmarkGraphFactory.getNodeId(29, 29, 30);

        new Dijkstra().findPath(graph, startId, endId);
        new AStar().findPath(graph, startId, endId);
    }

    private static void runCase(int rows, int cols) {
        System.out.println();
        System.out.println("------------------------------------------------------");
        System.out.println("测试规模：" + rows + " x " + cols);
        System.out.println("节点数量：" + formatNumber((long) rows * cols));
        System.out.println("有向边数量约：" +
                formatNumber(BenchmarkGraphFactory.estimateDirectedEdgeCount(rows, cols)));
        System.out.println("------------------------------------------------------");

        Graph graph = BenchmarkGraphFactory.createEightDirectionGridGraph(rows, cols);

        int startId = BenchmarkGraphFactory.getNodeId(0, 0, cols);
        int endId = BenchmarkGraphFactory.getNodeId(rows - 1, cols - 1, cols);

        BenchmarkResult dijkstraResult =
                runAlgorithm(new Dijkstra(), graph, startId, endId);

        BenchmarkResult aStarResult =
                runAlgorithm(new AStar(), graph, startId, endId);

        printResultTable(dijkstraResult, aStarResult);
        printConclusion(dijkstraResult, aStarResult);

        System.gc();
    }

    private static BenchmarkResult runAlgorithm(PathAlgorithm algorithm,
                                                Graph graph,
                                                int startId,
                                                int endId) {
        long begin = System.nanoTime();

        PathResult result = algorithm.findPath(graph, startId, endId);

        long end = System.nanoTime();
        double elapsedMillis = (end - begin) / 1_000_000.0;

        return new BenchmarkResult(
                algorithm.getName(),
                result.getTotalDistance(),
                result.getPath().size(),
                result.getVisitedCount(),
                elapsedMillis
        );
    }

    private static void printResultTable(BenchmarkResult dijkstra,
                                         BenchmarkResult aStar) {
        System.out.println();
        System.out.printf(
                "%-12s %15s %15s %15s %15s%n",
                "算法",
                "访问节点数",
                "路径节点数",
                "总距离",
                "耗时(ms)"
        );

        System.out.println("--------------------------------------------------------------------------");

        printSingleResult(dijkstra);
        printSingleResult(aStar);
    }

    private static void printSingleResult(BenchmarkResult result) {
        System.out.printf(
                "%-12s %15s %15s %15.2f %15.3f%n",
                result.algorithmName,
                formatNumber(result.visitedCount),
                formatNumber(result.pathNodeCount),
                result.totalDistance,
                result.elapsedMillis
        );
    }

    private static void printConclusion(BenchmarkResult dijkstra,
                                        BenchmarkResult aStar) {
        System.out.println();

        double visitedRatio = (double) dijkstra.visitedCount / aStar.visitedCount;
        double timeRatio = dijkstra.elapsedMillis / aStar.elapsedMillis;

        System.out.println("对比结论：");
        System.out.printf(
                "A* 的访问节点数约为 Dijkstra 的 %.2f%%。%n",
                100.0 / visitedRatio
        );

        System.out.printf(
                "Dijkstra 的访问节点数约为 A* 的 %.2f 倍。%n",
                visitedRatio
        );

        System.out.printf(
                "Dijkstra 的运行耗时约为 A* 的 %.2f 倍。%n",
                timeRatio
        );
    }

    private static String formatNumber(long number) {
        return String.format("%,d", number);
    }

    private static class BenchmarkResult {
        private final String algorithmName;
        private final double totalDistance;
        private final int pathNodeCount;
        private final int visitedCount;
        private final double elapsedMillis;

        public BenchmarkResult(String algorithmName,
                               double totalDistance,
                               int pathNodeCount,
                               int visitedCount,
                               double elapsedMillis) {
            this.algorithmName = algorithmName;
            this.totalDistance = totalDistance;
            this.pathNodeCount = pathNodeCount;
            this.visitedCount = visitedCount;
            this.elapsedMillis = elapsedMillis;
        }
    }
}