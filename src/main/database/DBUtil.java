package main.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
DBUtil负责数据库连接、建表、初始化默认地图数据
数据库文件：campus_path.db

26.6.13 添加大型默认校园测试地图
*/

public class DBUtil {
    private static final String DB_URL = "jdbc:sqlite:campus_path.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("未找到 SQLite JDBC 驱动，请先添加 sqlite-jdbc.jar", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL);

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        return connection;
    }

    public static void initDatabase() {
        createTables();
        insertDefaultDataIfEmpty();
    }

    private static void createTables() {
        String createNodeTable = """
                CREATE TABLE IF NOT EXISTS nodes (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    x REAL NOT NULL,
                    y REAL NOT NULL
                )
                """;

        String createEdgeTable = """
                CREATE TABLE IF NOT EXISTS edges (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    from_id INTEGER NOT NULL,
                    to_id INTEGER NOT NULL,
                    weight REAL NOT NULL,
                    FOREIGN KEY (from_id) REFERENCES nodes(id),
                    FOREIGN KEY (to_id) REFERENCES nodes(id)
                )
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(createNodeTable);
            statement.execute(createEdgeTable);

        } catch (SQLException e) {
            throw new RuntimeException("数据库建表失败", e);
        }
    }

    private static void insertDefaultDataIfEmpty() {
        if (!isNodeTableEmpty()) {
            return;
        }

        insertDefaultNodes();
        insertDefaultEdges();
    }

    private static void insertDefaultNodes() {
        String sql = """
                INSERT INTO nodes(id, name, x, y)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            addNode(statement, 1, "南门", 40, 220);
            addNode(statement, 2, "行政楼", 140, 220);
            addNode(statement, 3, "图书馆", 260, 170);
            addNode(statement, 4, "第一教学楼", 260, 80);
            addNode(statement, 5, "第二教学楼", 380, 80);
            addNode(statement, 6, "实验楼", 500, 100);
            addNode(statement, 7, "计算机学院", 610, 120);
            addNode(statement, 8, "大礼堂", 380, 190);
            addNode(statement, 9, "第一食堂", 260, 300);
            addNode(statement, 10, "第二食堂", 420, 310);
            addNode(statement, 11, "宿舍A区", 120, 360);
            addNode(statement, 12, "宿舍B区", 240, 420);
            addNode(statement, 13, "宿舍C区", 380, 420);
            addNode(statement, 14, "体育馆", 540, 360);
            addNode(statement, 15, "操场", 660, 330);
            addNode(statement, 16, "医务室", 120, 120);
            addNode(statement, 17, "超市", 190, 300);
            addNode(statement, 18, "湖心公园", 500, 220);
            addNode(statement, 19, "创新中心", 630, 230);
            addNode(statement, 20, "停车场", 40, 90);

            statement.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException("初始化默认节点数据失败", e);
        }
    }

    private static void insertDefaultEdges() {
        String sql = """
                INSERT INTO edges(from_id, to_id, weight)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            addUndirectedEdge(statement, 1, 2, 100);
            addUndirectedEdge(statement, 2, 3, 130);
            addUndirectedEdge(statement, 3, 4, 90);
            addUndirectedEdge(statement, 4, 5, 120);
            addUndirectedEdge(statement, 5, 6, 122);
            addUndirectedEdge(statement, 6, 7, 112);

            addUndirectedEdge(statement, 3, 8, 122);
            addUndirectedEdge(statement, 5, 8, 110);
            addUndirectedEdge(statement, 6, 18, 120);
            addUndirectedEdge(statement, 8, 18, 122);
            addUndirectedEdge(statement, 18, 19, 132);
            addUndirectedEdge(statement, 7, 19, 110);

            addUndirectedEdge(statement, 2, 16, 102);
            addUndirectedEdge(statement, 16, 20, 100);
            addUndirectedEdge(statement, 1, 20, 130);

            addUndirectedEdge(statement, 2, 17, 94);
            addUndirectedEdge(statement, 17, 9, 70);
            addUndirectedEdge(statement, 2, 9, 128);
            addUndirectedEdge(statement, 3, 9, 130);

            addUndirectedEdge(statement, 9, 10, 160);
            addUndirectedEdge(statement, 8, 10, 126);
            addUndirectedEdge(statement, 10, 18, 120);
            addUndirectedEdge(statement, 10, 14, 130);

            addUndirectedEdge(statement, 14, 15, 124);
            addUndirectedEdge(statement, 19, 15, 104);
            addUndirectedEdge(statement, 14, 18, 146);

            addUndirectedEdge(statement, 11, 17, 92);
            addUndirectedEdge(statement, 11, 12, 134);
            addUndirectedEdge(statement, 12, 13, 140);
            addUndirectedEdge(statement, 13, 14, 171);
            addUndirectedEdge(statement, 9, 12, 122);
            addUndirectedEdge(statement, 10, 13, 117);

            statement.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException("初始化默认道路数据失败", e);
        }
    }

    private static void addNode(PreparedStatement statement, int id, String name,
                                double x, double y) throws SQLException {
        statement.setInt(1, id);
        statement.setString(2, name);
        statement.setDouble(3, x);
        statement.setDouble(4, y);
        statement.addBatch();
    }

    private static void addUndirectedEdge(PreparedStatement statement, int a, int b,
                                          double weight) throws SQLException {
        addDirectedEdge(statement, a, b, weight);
        addDirectedEdge(statement, b, a, weight);
    }

    private static void addDirectedEdge(PreparedStatement statement, int from, int to,
                                        double weight) throws SQLException {
        statement.setInt(1, from);
        statement.setInt(2, to);
        statement.setDouble(3, weight);
        statement.addBatch();
    }

    private static boolean isNodeTableEmpty() {
        String sql = "SELECT COUNT(*) FROM nodes";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            if (resultSet.next()) {
                return resultSet.getInt(1) == 0;
            }

            return true;

        } catch (SQLException e) {
            throw new RuntimeException("检查节点表数据失败", e);
        }
    }
}