package main.ui;

import main.database.RecordDao;
import main.database.RecordDao.PathRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/*
ver 26.6.16
用于显示路径查询历史记录

功能：
1. 使用 JTable 展示 path_records 表中的数据
2. 支持刷新记录
3. 支持关闭窗口
*/

public class RecordDialog extends JDialog {
    private RecordDao recordDao;
    private JTable table;
    private DefaultTableModel tableModel;

    public RecordDialog(Frame owner, RecordDao recordDao) {
        super(owner, "历史路径记录", true);

        this.recordDao = recordDao;

        initDialog();
        initComponents();
        loadRecords();
    }

    private void initDialog() {
        setSize(900, 400);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(
                new Object[]{
                        "编号",
                        "算法",
                        "起点",
                        "终点",
                        "距离",
                        "路径",
                        "访问节点数",
                        "耗时(ms)",
                        "查询时间"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        setColumnWidth();

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("刷新");
        JButton closeButton = new JButton("关闭");

        refreshButton.addActionListener(e -> loadRecords());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setColumnWidth() {
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(300);
        table.getColumnModel().getColumn(6).setPreferredWidth(90);
        table.getColumnModel().getColumn(7).setPreferredWidth(80);
        table.getColumnModel().getColumn(8).setPreferredWidth(150);
    }

    private void loadRecords() {
        try {
            tableModel.setRowCount(0);

            List<PathRecord> records = recordDao.findAllRecords();

            for (PathRecord record : records) {
                tableModel.addRow(new Object[]{
                        record.getId(),
                        record.getAlgorithmName(),
                        record.getStartNode(),
                        record.getEndNode(),
                        record.getDistanceText(),
                        record.getPath(),
                        record.getVisitedCount(),
                        record.getTimeCostMs(),
                        record.getCreatedAt()
                });
            }

            if (records.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "暂无历史路径记录。",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

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