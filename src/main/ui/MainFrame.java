package main.ui;

import main.database.GraphDao;
import main.model.Graph;
import main.service.RouteService;

import javax.swing.*;
import java.awt.*;

/*
26.6.13 接入数据库读写
26.6.13 添加路径菜单事件，补充菜单占位提示
26.6.7 MainFrame主窗口，基于JSwing

框架：
文件
├── 从数据库读取地图
├── 保存地图到数据库
├── 导入地图文件
├── 导出查询结果
└── 退出

路径
├── 开始寻路
├── 清除路径
└── 算法对比

设置
├── 算法默认选择
├── 语言设置
├── 界面主题
└── 数据库设置

帮助
├── 使用说明
└── 关于
*/

public class MainFrame extends JFrame {
    private Graph graph;
    private RouteService routeService;
    private GraphDao graphDao; //6.13

    private MapPanel mapPanel;
    private ControlPanel controlPanel;
    private ResultPanel resultPanel;

    public MainFrame(Graph graph, RouteService routeService, GraphDao graphDao) {
        this.graph = graph;
        this.routeService = routeService;
        this.graphDao = graphDao;

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
        JMenu settingMenu = createSettingMenu();
        JMenu helpMenu = createHelpMenu();

        menuBar.add(fileMenu);
        menuBar.add(pathMenu);
        menuBar.add(settingMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("文件");

        JMenuItem openItem = new JMenuItem("从数据库读取地图");
        JMenuItem saveItem = new JMenuItem("保存地图到数据库");
        JMenuItem importItem = new JMenuItem("导入地图文件");
        JMenuItem exportItem = new JMenuItem("导出查询结果");
        JMenuItem exitItem = new JMenuItem("退出");

        openItem.addActionListener(e -> loadGraphFromDatabase());
        saveItem.addActionListener(e -> saveGraphToDatabase());
        importItem.addActionListener(e -> showTodo("导入地图文件"));
        exportItem.addActionListener(e -> showTodo("导出查询结果"));
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(importItem);
        fileMenu.add(exportItem);
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

    private JMenu createSettingMenu() {
        JMenu settingMenu = new JMenu("设置");

        JMenuItem defaultAlgorithmItem = new JMenuItem("算法默认选择");
        JMenuItem languageItem = new JMenuItem("语言设置");
        JMenuItem themeItem = new JMenuItem("界面主题");
        JMenuItem databaseItem = new JMenuItem("数据库设置");

        defaultAlgorithmItem.addActionListener(e -> showTodo("算法默认选择"));
        languageItem.addActionListener(e -> showTodo("语言设置"));
        themeItem.addActionListener(e -> showTodo("界面主题"));
        databaseItem.addActionListener(e -> showTodo("数据库设置"));

        settingMenu.add(defaultAlgorithmItem);
        settingMenu.add(languageItem);
        settingMenu.add(themeItem);
        settingMenu.add(databaseItem);

        return settingMenu;
    }

    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("帮助");

        JMenuItem usageItem = new JMenuItem("使用说明");
        JMenuItem aboutItem = new JMenuItem("关于");

        usageItem.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "使用步骤：\n" +
                        "1. 在左侧选择起点和终点\n" +
                        "2. 选择 Dijkstra 或 A* 算法\n" +
                        "3. 点击开始寻路查看路径\n" +
                        "4. 点击算法对比查看不同算法的访问节点数和耗时",
                "使用说明",
                JOptionPane.INFORMATION_MESSAGE
        ));

        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "基于最短路算法的路径规划系统\n支持 Dijkstra 与 A*",
                "关于",
                JOptionPane.INFORMATION_MESSAGE
        ));

        helpMenu.add(usageItem);
        helpMenu.add(aboutItem);

        return helpMenu;
    }

    private void loadGraphFromDatabase() { //6.13
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

    private void showTodo(String functionName) {
        JOptionPane.showMessageDialog(
                this,
                functionName + " 功能将在后续版本实现。",
                "功能开发中",
                JOptionPane.INFORMATION_MESSAGE
        );
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