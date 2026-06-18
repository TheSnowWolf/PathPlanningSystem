package main;

import main.database.DBUtil;
import main.database.GraphDao;
import main.model.Graph;
import main.service.RouteService;
import main.ui.MainFrame;

import javax.swing.SwingUtilities;

/*
26.6.13 改成启动时从数据库读图
26.6.13 添加更大型的测试数据

程序启动
↓
初始化 SQLite 数据库
↓
如果数据库为空，插入默认校园地图数据
↓
从 nodes 表和 edges 表读取数据
↓
构造 Graph 对象
↓
创建 RouteService
↓
打开 Swing 主窗口
↓
用户选择起点、终点和算法
↓
执行寻路并显示结果
*/


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DBUtil.initDatabase();

            GraphDao graphDao = new GraphDao();
            Graph graph = graphDao.loadGraph();
            RouteService routeService = new RouteService(graph);

            MainFrame frame = new MainFrame(graph, routeService, graphDao);
            frame.setVisible(true);
        });
    }
}

