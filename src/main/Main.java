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

Main
↓
DBUtil.initDatabase()
↓
如果 campus_path.db 不存在，自动创建
↓
如果 nodes 表为空，自动插入默认地图
↓
GraphDao.loadGraph()
↓
从 nodes / edges 表构造 Graph
↓
RouteService 使用 Graph
↓
MainFrame 显示 Graph
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

