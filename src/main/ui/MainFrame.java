package main.ui;

import main.model.Graph;
import main.service.RouteService;

import javax.management.remote.JMXAddressable;
import javax.swing.*;
import java.awt.*;

/*
26.6.7 MainFrame主窗口，基于JSWing

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

public class MainFrame extends JFrame{
    private Graph graph;
    private RouteService routeService;

    private MapPanel mapPanel;
    private ControlPanel controlPanel;
    private ResultPanel resultPanel;

    public MainFrame(Graph graph, RouteService routeService) {
        this.graph = graph;
        this.routeService = routeService;

        initFrame();
        initMenuBar();
        initComponents();
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

        JMenu fileMenu = new JMenu("文件");
        JMenuItem openItem = new JMenuItem("从数据库读取地图");
        JMenuItem saveItem = new JMenuItem("保存地图到数据库");
        JMenuItem exitItem = new JMenuItem("退出");

        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu settingMenu = new JMenu("设置");
        JMenuItem languageItem = new JMenuItem("语言设置");
        JMenuItem preferenceItem = new JMenuItem("系统设置");

        settingMenu.add(languageItem);
        settingMenu.add(preferenceItem);

        JMenu helpMenu = new JMenu("帮助");
        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "基于最短路算法的路径规划系统\n支持 Dijkstra 与 A*",
                "关于",
                JOptionPane.INFORMATION_MESSAGE
        ));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(settingMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }
}
