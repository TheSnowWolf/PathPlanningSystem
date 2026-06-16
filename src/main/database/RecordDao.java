package main.database;

import main.model.Node;
import main.model.PathResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/*
ver 26.6.16
RecordDao负责路径查询记录的数据库操作

功能：
1. 保存单次路径查询结果
2. 查询所有历史记录
3. 清空历史记录

对应数据表：path_records
*/

public class RecordDao {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void saveRecord(PathResult result, Node startNode, Node endNode) {
        if (result == null) {
            throw new IllegalArgumentException("路径结果不能为空");
        }

        if (startNode == null || endNode == null) {
            throw new IllegalArgumentException("起点或终点不能为空");
        }

        DBUtil.initDatabase();

        String sql = """
                INSERT INTO path_records(
                    algorithm_name,
                    start_node,
                    end_node,
                    distance,
                    path,
                    visited_count,
                    time_cost_ms,
                    created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, result.getAlgorithmName());
            statement.setString(2, startNode.getName());
            statement.setString(3, endNode.getName());

            if (Double.isInfinite(result.getTotalDistance())) {
                statement.setNull(4, Types.REAL);
            } else {
                statement.setDouble(4, result.getTotalDistance());
            }

            statement.setString(5, formatPath(result.getPath()));
            statement.setInt(6, result.getVisitedCount());
            statement.setLong(7, result.getTimeMillis());
            statement.setString(8, LocalDateTime.now().format(FORMATTER));

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("保存路径查询记录失败", e);
        }
    }

    public List<PathRecord> findAllRecords() {
        DBUtil.initDatabase();

        String sql = """
                SELECT
                    id,
                    algorithm_name,
                    start_node,
                    end_node,
                    distance,
                    path,
                    visited_count,
                    time_cost_ms,
                    created_at
                FROM path_records
                ORDER BY id DESC
                """;

        List<PathRecord> records = new ArrayList<>();

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                double distanceValue = resultSet.getDouble("distance");
                Double distance = resultSet.wasNull() ? null : distanceValue;

                PathRecord record = new PathRecord(
                        resultSet.getInt("id"),
                        resultSet.getString("algorithm_name"),
                        resultSet.getString("start_node"),
                        resultSet.getString("end_node"),
                        distance,
                        resultSet.getString("path"),
                        resultSet.getInt("visited_count"),
                        resultSet.getLong("time_cost_ms"),
                        resultSet.getString("created_at")
                );

                records.add(record);
            }

            return records;

        } catch (SQLException e) {
            throw new RuntimeException("读取路径查询记录失败", e);
        }
    }

    public void clearRecords() {
        DBUtil.initDatabase();

        String sql = "DELETE FROM path_records";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("清空路径查询记录失败", e);
        }
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

    public static class PathRecord {
        private int id;
        private String algorithmName;
        private String startNode;
        private String endNode;
        private Double distance;
        private String path;
        private int visitedCount;
        private long timeCostMs;
        private String createdAt;

        public PathRecord(int id, String algorithmName, String startNode,
                          String endNode, Double distance, String path,
                          int visitedCount, long timeCostMs, String createdAt) {
            this.id = id;
            this.algorithmName = algorithmName;
            this.startNode = startNode;
            this.endNode = endNode;
            this.distance = distance;
            this.path = path;
            this.visitedCount = visitedCount;
            this.timeCostMs = timeCostMs;
            this.createdAt = createdAt;
        }

        public int getId() {
            return id;
        }

        public String getAlgorithmName() {
            return algorithmName;
        }

        public String getStartNode() {
            return startNode;
        }

        public String getEndNode() {
            return endNode;
        }

        public Double getDistance() {
            return distance;
        }

        public String getPath() {
            return path;
        }

        public int getVisitedCount() {
            return visitedCount;
        }

        public long getTimeCostMs() {
            return timeCostMs;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getDistanceText() {
            if (distance == null) {
                return "不可达";
            }

            return String.format("%.2f", distance);
        }
    }
}