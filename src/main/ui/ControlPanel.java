package main.ui;

import main.database.RecordDao;
import main.model.Graph;
import main.model.Node;
import main.model.PathResult;
import main.service.RouteService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/*
26.6.7 ControlPanel负责选择起点，终点，算法
26.6.13 添加清除路径与算法对比
26.6.13 添加setGraph，用于数据库重新加载地图后刷新下拉框
26.6.16 添加路径查询记录保存
26.6.18 使用 SwingWorker 将寻路任务放入后台线程，避免大规模地图下阻塞 Swing 界面

说明：
1. Swing 的界面更新必须在事件分发线程中完成
2. 算法计算可能耗时，因此放入 SwingWorker#doInBackground
3. 算法执行完成后，在 SwingWorker#done 中更新地图、结果面板和数据库记录
*/

public class ControlPanel extends JPanel {
    private Graph graph;
    private final RouteService routeService;
    private final MapPanel mapPanel;
    private final ResultPanel resultPanel;
    private final RecordDao recordDao;

    private JComboBox<NodeItem> startBox;
    private JComboBox<NodeItem> endBox;
    private JComboBox<String> algorithmBox;

    private JButton findButton;
    private JButton clearButton;
    private JButton compareButton;

    public ControlPanel(Graph graph, RouteService routeService,
                        MapPanel mapPanel, ResultPanel resultPanel) {
        this.graph = graph;
        this.routeService = routeService;
        this.mapPanel = mapPanel;
        this.resultPanel = resultPanel;
        this.recordDao = new RecordDao();

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

        if (endBox.getItemCount() > 1) {
            endBox.setSelectedIndex(1);
        }

        findButton = new JButton("开始寻路");
        clearButton = new JButton("清除路径");
        compareButton = new JButton("算法对比");

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

    public void setGraph(Graph graph) {
        this.graph = graph;
        refreshNodeBoxes();
    }

    private void refreshNodeBoxes() {
        startBox.removeAllItems();
        endBox.removeAllItems();

        for (Node node : graph.getAllNodes()) {
            startBox.addItem(new NodeItem(node));
            endBox.addItem(new NodeItem(node));
        }

        if (endBox.getItemCount() > 1) {
            endBox.setSelectedIndex(1);
        }
    }

    public void findPath() {
        try {
            Node startNode = getSelectedStartNode();
            Node endNode = getSelectedEndNode();
            String algorithm = (String) algorithmBox.getSelectedItem();

            setControlsEnabled(false);
            findButton.setText("寻路中...");

            SwingWorker<PathResult, Void> worker = new SwingWorker<>() {
                @Override
                protected PathResult doInBackground() {
                    return routeService.findPath(
                            startNode.getId(),
                            endNode.getId(),
                            algorithm
                    );
                }

                @Override
                protected void done() {
                    try {
                        PathResult result = get();

                        mapPanel.setPath(result.getPath());
                        resultPanel.showResult(result);

                        recordDao.saveRecord(result, startNode, endNode);

                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        showError(new RuntimeException("寻路任务被中断", ex));
                    } catch (ExecutionException ex) {
                        showWorkerError(ex);
                    } finally {
                        findButton.setText("开始寻路");
                        setControlsEnabled(true);
                    }
                }
            };

            worker.execute();

        } catch (Exception ex) {
            findButton.setText("开始寻路");
            setControlsEnabled(true);
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

            setControlsEnabled(false);
            compareButton.setText("对比中...");

            SwingWorker<Map<String, PathResult>, Void> worker = new SwingWorker<>() {
                @Override
                protected Map<String, PathResult> doInBackground() {
                    return routeService.compareAlgorithms(
                            startNode.getId(),
                            endNode.getId()
                    );
                }

                @Override
                protected void done() {
                    try {
                        Map<String, PathResult> results = get();

                        resultPanel.showCompareResult(results);
                        saveCompareRecords(results, startNode, endNode);

                        String selectedAlgorithm = (String) algorithmBox.getSelectedItem();
                        PathResult selectedResult = results.get(selectedAlgorithm);

                        if (selectedResult != null) {
                            mapPanel.setPath(selectedResult.getPath());
                        }

                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        showError(new RuntimeException("算法对比任务被中断", ex));
                    } catch (ExecutionException ex) {
                        showWorkerError(ex);
                    } finally {
                        compareButton.setText("算法对比");
                        setControlsEnabled(true);
                    }
                }
            };

            worker.execute();

        } catch (Exception ex) {
            compareButton.setText("算法对比");
            setControlsEnabled(true);
            showError(ex);
        }
    }

    private void saveCompareRecords(Map<String, PathResult> results,
                                    Node startNode, Node endNode) {
        if (results == null || results.isEmpty()) {
            return;
        }

        for (PathResult result : results.values()) {
            recordDao.saveRecord(result, startNode, endNode);
        }
    }

    private void setControlsEnabled(boolean enabled) {
        startBox.setEnabled(enabled);
        endBox.setEnabled(enabled);
        algorithmBox.setEnabled(enabled);

        findButton.setEnabled(enabled);
        clearButton.setEnabled(enabled);
        compareButton.setEnabled(enabled);
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

    private void showWorkerError(ExecutionException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof Exception exception) {
            showError(exception);
        } else {
            showError(new RuntimeException("后台任务执行失败", ex));
        }
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
        private final Node node;

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