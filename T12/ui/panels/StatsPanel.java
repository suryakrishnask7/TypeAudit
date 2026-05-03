package T12.ui.panels;

import T12.stats.AnalyticsEngine;
import T12.stats.StatsManager;
import T12.stats.StatsRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StatsPanel extends JPanel {
    private StatsManager statsManager;
    private AnalyticsEngine analyticsEngine;
    private DefaultTableModel tableModel;
    
    // Overall performance labels
    private JLabel lblTotalSessions;
    private JLabel lblBestWpm;
    private JLabel lblAvgWpm;
    private JLabel lblAvgAccuracy;
    private JLabel lblSpeedTrend;
    private JLabel lblAccuracyTrend;

    public StatsPanel(StatsManager statsManager) {
        this.statsManager = statsManager;
        this.analyticsEngine = new AnalyticsEngine(statsManager);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel for "Overall Performance"
        JPanel topPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Overall Performance"));
        
        lblTotalSessions = new JLabel("Total Sessions: 0");
        lblBestWpm = new JLabel("Best WPM: 0.00");
        lblAvgWpm = new JLabel("Avg WPM: 0.00");
        lblAvgAccuracy = new JLabel("Avg Accuracy: 0.00%");
        lblSpeedTrend = new JLabel("Speed Trend: -");
        lblAccuracyTrend = new JLabel("Accuracy Trend: -");
        
        Font boldFont = new Font("SansSerif", Font.BOLD, 12);
        lblTotalSessions.setFont(boldFont);
        lblBestWpm.setFont(boldFont);
        lblAvgWpm.setFont(boldFont);
        lblAvgAccuracy.setFont(boldFont);
        lblSpeedTrend.setFont(boldFont);
        lblAccuracyTrend.setFont(boldFont);

        topPanel.add(lblTotalSessions);
        topPanel.add(lblBestWpm);
        topPanel.add(lblAvgWpm);
        topPanel.add(lblAvgAccuracy);
        topPanel.add(lblSpeedTrend);
        topPanel.add(lblAccuracyTrend);

        // Center table for history
        String[] columns = {"Date", "WPM", "Accuracy (%)", "Errors", "Duration (s)"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        table.setEnabled(false); // read-only
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Session History"));

        // Bottom panel for BigInteger totals
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JLabel lblTotalKeystrokes = new JLabel("Total Keystrokes (Lifetime): " + statsManager.getTotalKeystrokes());
        JLabel lblTotalChars = new JLabel("Total Characters Typed: " + statsManager.getTotalCharactersTyped());
        bottomPanel.add(lblTotalKeystrokes);
        bottomPanel.add(lblTotalChars);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshData();
    }

    public void refreshData() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Populate from history
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (StatsRecord record : statsManager.getHistory()) {
            Object[] row = {
                sdf.format(new Date(record.getTimestamp())),
                String.format("%.2f", record.getWpm()),
                String.format("%.2f", record.getAccuracy()),
                record.getErrorCount(),
                record.getDuration()
            };
            tableModel.addRow(row);
        }

        // Update overall performance
        lblTotalSessions.setText("Total Sessions: " + statsManager.getTotalSessions().toString());
        StatsRecord best = statsManager.getBestSession();
        lblBestWpm.setText(String.format("Best WPM: %.2f", (best != null ? best.getWpm() : 0.0)));
        lblAvgWpm.setText(String.format("Avg WPM: %.2f", analyticsEngine.getAverageWpm()));
        lblAvgAccuracy.setText(String.format("Avg Accuracy: %.2f%%", analyticsEngine.getAverageAccuracy()));
        lblSpeedTrend.setText("Speed Trend: " + analyticsEngine.getSpeedTrend());
        lblAccuracyTrend.setText("Accuracy Trend: " + analyticsEngine.getAccuracyTrend());

        // Update bottom totals
        Component[] bottomComps = ((JPanel)getComponent(2)).getComponents();
        if (bottomComps.length >= 2) {
            ((JLabel)bottomComps[0]).setText("Total Keystrokes (Lifetime): " + statsManager.getTotalKeystrokes().toString());
            ((JLabel)bottomComps[1]).setText("Total Characters Typed: " + statsManager.getTotalCharactersTyped().toString());
        }
    }
}
