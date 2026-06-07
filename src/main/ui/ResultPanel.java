package main.ui;

import main.model.Node;
import main.model.PathResult;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.List;

/*
26.6.7 ResultPanel负责显示结果
*/

public class ResultPanel extends JPanel{
    private JTextArea textArea;

    public ResultPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 120));

        textArea = new JTextArea();
        textArea.setEditable(false);

        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }

    public void showResult(PathResult result) {
        if (result.getPath().isEmpty()) {
            textArea.setText("没有找到路径");
            return ;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("算法：").append(result.getAlgorithmName()).append("\n");
        sb.append("总距离：").append(result.getTotalDistance()).append("\n");
        sb.append("耗时：").append(result.getTimeMillis()).append("\n");
        sb.append("访问节点数：").append(result.getVisitedCount()).append("\n");
        sb.append("路径：");

        List<Node> path = result.getPath();

        for (int i = 0; i < path.size(); ++ i) {
            sb.append(path.get(i).getName());

            if (i != path.size() - 1) {
                sb.append(" -> ");
            }
        }

        textArea.setText(sb.toString());
    }
}
