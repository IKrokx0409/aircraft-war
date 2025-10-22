package edu.hitsz.ui;

import edu.hitsz.dao.GameRecord;
import edu.hitsz.dao.GameRecordDao;
import edu.hitsz.dao.GameRecordDaoImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.List;

public class RankingPanel extends JPanel {

    private JLabel headerLabel;
    private JTable rankingTable;
    private JButton deleteButton;
    private JScrollPane tableScrollPane;
    private DefaultTableModel tableModel;

    private final GameRecordDao gameRecordDao;
    private JTable JTable;
    private JScrollPane JScrollPane;
    private final String difficulty;

    public RankingPanel(String difficulty) {
        this.gameRecordDao = new GameRecordDaoImpl();
        this.difficulty = difficulty;
        initializeUI(difficulty);
        loadRankingData();
    }

    private void initializeUI(String difficulty) {
        this.setLayout(new BorderLayout());

        headerLabel = new JLabel(difficulty.toUpperCase() + " MODE" + " RANKING LIST", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        this.add(headerLabel, BorderLayout.NORTH); // 标题放在顶部

        String[] columnNames = {"Rank", "Player", "Score", "Time"};
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        rankingTable = new JTable(tableModel);
        rankingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableScrollPane = new JScrollPane(rankingTable);
        this.add(tableScrollPane, BorderLayout.CENTER);

        deleteButton = new JButton("Delete selected records");
        deleteButton.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(deleteButton);
        this.add(bottomPanel, BorderLayout.SOUTH);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = rankingTable.getSelectedRow();
                if (selectedRow != -1) {
                    int confirm = JOptionPane.showConfirmDialog(
                            RankingPanel.this,
                            "Sure to delete the selected player record?",
                            "Confirm deletion",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        String playerName = (String) tableModel.getValueAt(selectedRow, 1);
                        int score = (int) tableModel.getValueAt(selectedRow, 2);
                        String time = (String) tableModel.getValueAt(selectedRow, 3);

                        gameRecordDao.deleteRecord(playerName, score, time, difficulty);
                        loadRankingData();
                    }
                } else {
                    // 没有选中行
                    JOptionPane.showMessageDialog(RankingPanel.this, "Please select the record to be deleted first!");
                }
            }
        });
    }

    /**
     * 从 DAO 加载数据、排序并更新到 JTable
     */
    public void loadRankingData() {
        List<GameRecord> records = gameRecordDao.getAllRecords(this.difficulty);
        records.sort(Comparator.comparingInt(GameRecord::getScore).reversed());

        tableModel.setRowCount(0);
        int rank = 1;
        for (GameRecord record : records) {
            Object[] rowData = {
                    rank++,
                    record.getPlayerName(),
                    record.getScore(),
                    record.getFormattedTimestamp()
            };
            tableModel.addRow(rowData);
        }
    }

}