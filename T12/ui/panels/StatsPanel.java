package T12.ui.panels;

import T12.stats.AnalyticsEngine;
import T12.stats.StatsManager;
import T12.stats.StatsRecord;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * UI panel displaying aggregated typing test statistics and analytics.
 * Shows aggregate metrics (total sessions, best WPM, averages, trends) in upper
 * section.
 * Lists recent sessions (last 3) with timestamps, WPM, and accuracy in
 * scrollable list below.
 * Updates dynamically when TypingPanel completes a test via refreshData()
 * callback.
 * Uses AnalyticsEngine to compute trends indicating user improvement or
 * decline.
 */
public class StatsPanel extends JPanel {
    private final StatsManager statsManager;
    private final AnalyticsEngine analyticsEngine;
    private final JLabel lblTotalSessions;
    private final JLabel lblBestWpm;
    private final JLabel lblAvgWpm;
    private final JLabel lblAvgAccuracy;
    private final JLabel lblTrend;
    private final JLabel lblLifetime;
    private final DefaultListModel<String> recentModel;

    public StatsPanel(StatsManager statsManager) {
        this.statsManager = statsManager;
        this.analyticsEngine = new AnalyticsEngine(statsManager);
        this.lblTotalSessions = new JLabel();
        this.lblBestWpm = new JLabel();
        this.lblAvgWpm = new JLabel();
        this.lblAvgAccuracy = new JLabel();
        this.lblTrend = new JLabel();
        this.lblLifetime = new JLabel();
        this.recentModel = new DefaultListModel<>();

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(0, 150));
        initUI();
        refreshData();
    }

    private void initUI() {
        JPanel summary = new JPanel(new GridLayout(2, 3, 8, 4));
        summary.setBorder(BorderFactory.createTitledBorder("Analytics"));
        summary.add(lblTotalSessions);
        summary.add(lblBestWpm);
        summary.add(lblAvgWpm);
        summary.add(lblAvgAccuracy);
        summary.add(lblTrend);
        summary.add(lblLifetime);

        JList<String> recentList = new JList<>(recentModel);
        recentList.setVisibleRowCount(3);
        JScrollPane recentScroll = new JScrollPane(recentList);
        recentScroll.setPreferredSize(new Dimension(230, 0));
        recentScroll.setBorder(BorderFactory.createTitledBorder("Recent"));

        add(summary, BorderLayout.CENTER);
        add(recentScroll, BorderLayout.EAST);
    }

    // Refresh all statistics displays by querying StatsManager and AnalyticsEngine
    // Updates aggregate stats (sessions, best WPM, averages, trend, lifetime chars)
    // Populates recent sessions list (last 3 sessions) with formatted timestamps
    // and metrics
    // Called after each completed typing test to reflect new data
    public void refreshData() {
        // Update aggregate stats by querying current totals and analytics
        lblTotalSessions.setText("Sessions: " + statsManager.getTotalSessions());
        StatsRecord best = statsManager.getBestSession();
        lblBestWpm.setText(String.format("Best WPM: %.2f", best == null ? 0.0 : best.getWpm()));
        lblAvgWpm.setText(String.format("Avg WPM: %.2f", analyticsEngine.getAverageWpm()));
        lblAvgAccuracy.setText(String.format("Avg Accuracy: %.2f%%", analyticsEngine.getAverageAccuracy()));
        lblTrend.setText("Trend: " + analyticsEngine.getSpeedTrend());
        lblLifetime.setText("Chars Typed: " + statsManager.getTotalCharactersTyped());

        // Populate recent sessions list with last 3 sessions in reverse chronological
        // order
        // Each entry shows formatted timestamp, WPM, and accuracy percentage
        recentModel.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        List<StatsRecord> recent = statsManager.getRecentSessions(3);
        for (int i = recent.size() - 1; i >= 0; i--) {
            StatsRecord record = recent.get(i);
            recentModel.addElement(String.format(
                    "%s  %.1f WPM  %.1f%%",
                    sdf.format(new Date(record.getTimestamp())),
                    record.getWpm(),
                    record.getAccuracy()));
        }
        // Show placeholder message if no sessions have been completed yet
        if (recentModel.isEmpty()) {
            recentModel.addElement("No sessions yet");
        }
    }
}
