package main.ui;

import main.database.GraphDao;
import main.database.RecordDao;
import main.model.Graph;
import main.service.RouteService;

import javax.swing.*;
import java.awt.*;

/*
26.6.16 添加历史记录菜单，支持查看与清空路径查询记录
26.6.16 清理未完成的菜单占位项
26.6.13 接入数据库读写
26.6.13 添加路径菜单事件
26.6.7 MainFrame主窗口，基于JSwing

最终菜单结构：

文件
├── 从数据库读取地图
├── 保存地图到数据库
└── 退出

路径
├── 开始寻路
├── 清除路径
└── 算法对比

记录
├── 查看历史记录
└── 清空历史记录

帮助
├── 使用说明
└── 关于
*/

public class MainFrame extends JFrame {
    private Graph graph;
    private RouteService routeService;
    private GraphDao graphDao;
    private RecordDao recordDao;

    private MapPanel mapPanel;
    private ControlPanel controlPanel;
    private ResultPanel resultPanel;

    public MainFrame(Graph graph, RouteService routeService, GraphDao graphDao) {
        this.graph = graph;
        this.routeService = routeService;
        this.graphDao = graphDao;
        this.recordDao = new RecordDao();

        initFrame();
        initComponents();
        initMenuBar();
    }

    private void initFrame() {
        setTitle("路径规划系统");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initComponents() {
        mapPanel = new MapPanel(graph);
        resultPanel = new ResultPanel();
        controlPanel = new ControlPanel(graph, routeService, mapPanel, resultPanel);

        setLayout(new BorderLayout());
        add(mapPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.WEST);
        add(resultPanel, BorderLayout.SOUTH);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = createFileMenu();
        JMenu pathMenu = createPathMenu();
        JMenu recordMenu = createRecordMenu();
        JMenu helpMenu = createHelpMenu();

        menuBar.add(fileMenu);
        menuBar.add(pathMenu);
        menuBar.add(recordMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("文件");

        JMenuItem openItem = new JMenuItem("从数据库读取地图");
        JMenuItem saveItem = new JMenuItem("保存地图到数据库");
        JMenuItem exitItem = new JMenuItem("退出");

        openItem.addActionListener(e -> loadGraphFromDatabase());
        saveItem.addActionListener(e -> saveGraphToDatabase());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        return fileMenu;
    }

    private JMenu createPathMenu() {
        JMenu pathMenu = new JMenu("路径");

        JMenuItem findPathItem = new JMenuItem("开始寻路");
        JMenuItem clearPathItem = new JMenuItem("清除路径");
        JMenuItem compareItem = new JMenuItem("算法对比");

        findPathItem.addActionListener(e -> controlPanel.findPath());
        clearPathItem.addActionListener(e -> controlPanel.clearPath());
        compareItem.addActionListener(e -> controlPanel.compareAlgorithms());

        pathMenu.add(findPathItem);
        pathMenu.add(clearPathItem);
        pathMenu.add(compareItem);

        return pathMenu;
    }

    private JMenu createRecordMenu() {
        JMenu recordMenu = new JMenu("记录");

        JMenuItem viewItem = new JMenuItem("查看历史记录");
        JMenuItem clearItem = new JMenuItem("清空历史记录");

        viewItem.addActionListener(e -> showRecordDialog());
        clearItem.addActionListener(e -> clearRecords());

        recordMenu.add(viewItem);
        recordMenu.add(clearItem);

        return recordMenu;
    }

    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("帮助");

        JMenuItem usageItem = new JMenuItem("使用说明");
        JMenuItem aboutItem = new JMenuItem("关于");

        usageItem.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                """
                        使用步骤：
                        1. 在左侧选择起点和终点
                        2. 选择 Dijkstra 或 A* 算法
                        3. 点击开始寻路查看路径
                        4. 点击算法对比查看不同算法的访问节点数和耗时
                        5. 通过“记录 -> 查看历史记录”查看查询结果""",
                "使用说明",
                JOptionPane.INFORMATION_MESSAGE
        ));

        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                """
                        基于最短路算法的路径规划系统
                        支持 Dijkstra 与 A* 算法
                        支持 SQLite 地图数据与路径记录持久化""",
                "关于",
                JOptionPane.INFORMATION_MESSAGE
        ));

        helpMenu.add(usageItem);
        helpMenu.add(aboutItem);

        return helpMenu;
    }

    private void loadGraphFromDatabase() {
        try {
            Graph newGraph = graphDao.loadGraph();

            this.graph = newGraph;
            routeService.setGraph(newGraph);
            mapPanel.setGraph(newGraph);
            controlPanel.setGraph(newGraph);
            resultPanel.clear();

            JOptionPane.showMessageDialog(
                    this,
                    "已从数据库读取地图数据。",
                    "读取成功",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void saveGraphToDatabase() {
        try {
            graphDao.saveGraph(graph);

            JOptionPane.showMessageDialog(
                    this,
                    "当前地图已保存到数据库。",
                    "保存成功",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showRecordDialog() {
        try {
            RecordDialog dialog = new RecordDialog(this, recordDao);
            dialog.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void clearRecords() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "确定要清空所有历史路径记录吗？",
                "确认清空",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            recordDao.clearRecords();

            JOptionPane.showMessageDialog(
                    this,
                    "历史路径记录已清空。",
                    "清空成功",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            showError(ex);
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
}