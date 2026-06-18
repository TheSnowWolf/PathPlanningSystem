package main.ui;

import main.model.Node;
import main.model.PathResult;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/*
26.6.7 ResultPanel负责显示结果
26.6.13 添加清除结果与算法对比显示
*/

public class ResultPanel extends JPanel {
    private final JTextArea textArea;

    public ResultPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 150));

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }

    public void showResult(PathResult result) {
        StringBuilder sb = new StringBuilder();

        if (result.getPath().isEmpty()) {
            sb.append("没有找到路径\n");
            sb.append("算法：").append(result.getAlgorithmName()).append("\n");
            sb.append("访问节点数：").append(result.getVisitedCount()).append("\n");
            sb.append("耗时：").append(result.getTimeMillis()).append(" ms\n");

            textArea.setText(sb.toString());
            return;
        }

        appendSingleResult(sb, result);
        textArea.setText(sb.toString());
    }

    public void showCompareResult(Map<String, PathResult> results) {
        if (results == null || results.isEmpty()) {
            textArea.setText("暂无算法对比结果");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("算法对比结果\n");
        sb.append("====================\n\n");

        for (PathResult result : results.values()) {
            appendSingleResult(sb, result);
            sb.append("\n--------------------\n\n");
        }

        appendCompareConclusion(sb, results);

        textArea.setText(sb.toString());
    }

    public void clear() {
        textArea.setText("");
    }

    private void appendSingleResult(StringBuilder sb, PathResult result) {
        sb.append("算法：").append(result.getAlgorithmName()).append("\n");
        sb.append("总距离：").append(formatDistance(result.getTotalDistance())).append("\n");
        sb.append("耗时：").append(result.getTimeMillis()).append(" ms\n");
        sb.append("访问节点数：").append(result.getVisitedCount()).append("\n");
        sb.append("路径：").append(formatPath(result.getPath())).append("\n");
    }

    private void appendCompareConclusion(StringBuilder sb, Map<String, PathResult> results) {
        PathResult bestVisited = null;
        PathResult bestTime = null;

        for (PathResult result : results.values()) {
            if (result.getPath().isEmpty()) {
                continue;
            }

            if (bestVisited == null || result.getVisitedCount() < bestVisited.getVisitedCount()) {
                bestVisited = result;
            }

            if (bestTime == null || result.getTimeMillis() < bestTime.getTimeMillis()) {
                bestTime = result;
            }
        }

        sb.append("对比结论\n");

        if (bestVisited == null) {
            sb.append("所有算法均未找到可达路径。\n");
            return;
        }

        sb.append("访问节点较少：")
                .append(bestVisited.getAlgorithmName())
                .append("，访问节点数为 ")
                .append(bestVisited.getVisitedCount())
                .append("\n");

        sb.append("运行耗时较短：")
                .append(bestTime.getAlgorithmName())
                .append("，耗时为 ")
                .append(bestTime.getTimeMillis())
                .append(" ms\n");
    }

    private String formatPath(List<Node> path) {
        if (path == null || path.isEmpty()) {
            return "无可达路径";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < path.size(); ++i) {
            sb.append(path.get(i).getName());

            if (i != path.size() - 1) {
                sb.append(" -> ");
            }
        }

        return sb.toString();
    }

    private String formatDistance(double distance) {
        if (Double.isInfinite(distance)) {
            return "不可达";
        }

        return String.format("%.2f", distance);
    }
}