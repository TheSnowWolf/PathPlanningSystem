package main.database;

import main.model.Edge;
import main.model.Graph;
import main.model.Node;
import org.sqlite.core.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
ver 26.6.13 GraphDao负责Graph对象与数据库之间的转换

数据库 -> Graph：
读取nodes表和edges表，构造Graph

Graph -> 数据库：
清空旧数据，再保存当前Graph中的节点和边
*/

public class GraphDao {

    public Graph loadGraph() {
        DBUtil.initDatabase();

        Graph graph = new Graph();

        loadNodes(graph);
        loadEdges(graph);

        return graph;
    }

    public void saveGraph(Graph graph) {
        DBUtil.initDatabase();

        String deleteEdges = "DELETE FROM edges";
        String deleteNodes = "DELETE FROM nodes";

        String insertNode = """
                INSERT INTO nodes(id, name, x, y)
                VALUES (?, ?, ?, ?)
                """;

        String insertEdge = """
                INSERT INTO edges(from_id, to_id, weight)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DBUtil.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement deleteEdgeStatement = connection.prepareStatement(deleteEdges);
                PreparedStatement deleteNodeStatement = connection.prepareStatement(deleteNodes);
                PreparedStatement nodeStatement = connection.prepareStatement(insertNode);
                PreparedStatement edgeStatement = connection.prepareStatement(insertEdge)) {

                deleteEdgeStatement.executeUpdate();
                deleteNodeStatement.executeUpdate();

                for (Node node : graph.getAllNodes()) {
                    nodeStatement.setInt(1, node.getId());
                    nodeStatement.setString(2, node.getName());
                    nodeStatement.setDouble(3, node.getX());
                    nodeStatement.setDouble(4, node.getY());
                    nodeStatement.addBatch();
                }

                nodeStatement.executeBatch();

                for (Node node : graph.getAllNodes()) {
                    for (Edge edge : graph.getEdges(node.getId())) {
                        edgeStatement.setInt(1, edge.getFrom());
                        edgeStatement.setInt(2, edge.getTo());
                        edgeStatement.setDouble(3, edge.getWeight());
                        edgeStatement.addBatch();
                    }
                }

                edgeStatement.executeBatch();

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("保存地图到数据库失败", e);
        }
    }

    private void loadNodes(Graph graph) {
        String sql = "SELECT id, name, x, y FROM nodes ORDER BY id";

        try (Connection connection = DBUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double x = resultSet.getDouble("x");
                double y = resultSet.getDouble("y");

                graph.addNode(new Node(id, name, x, y));
            }

        } catch (SQLException e) {
            throw new RuntimeException("读取节点数据失败", e);
        }
    }

    private void loadEdges(Graph graph) {
        String sql = "SELECT from_id, to_id, weight FROM edges ORDER BY id";

        try (Connection connection = DBUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int from = resultSet.getInt("from_id");
                int to = resultSet.getInt("to_id");
                double weight = resultSet.getDouble("weight");

                graph.addEdge(from, to, weight);
            }
        } catch (SQLException e) {
            throw new RuntimeException("读取边数据失败", e);
        }
    }
}
