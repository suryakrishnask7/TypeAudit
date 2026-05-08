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
 * Panel for aggregated stats and recent session history.
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
        // Keep the shared manager so this panel always reads the latest saved stats.
        this.statsManager = statsManager;
        // Build analytics on top of the same manager history.
        this.analyticsEngine = new AnalyticsEngine(statsManager);
        // Create the labels and list model that will be filled during refreshData().
        this.lblTotalSessions = new JLabel();
        this.lblBestWpm = new JLabel();
        this.lblAvgWpm = new JLabel();
        this.lblAvgAccuracy = new JLabel();
        this.lblTrend = new JLabel();
        this.lblLifetime = new JLabel();
        this.recentModel = new DefaultListModel<>();

        // Lay out the summary area beside the recent-sessions list.
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(0, 150));
        // Build the Swing components, then fill them with current data.
        initUI();
        refreshData();
    }

    private void initUI() {
        // Create the analytics summary grid shown at the bottom of the app.
        JPanel summary = new JPanel(new GridLayout(2, 3, 8, 4));
        summary.setBorder(BorderFactory.createTitledBorder("Analytics"));
        summary.add(lblTotalSessions);
        summary.add(lblBestWpm);
        summary.add(lblAvgWpm);
        summary.add(lblAvgAccuracy);
        summary.add(lblTrend);
        summary.add(lblLifetime);

        // Create a small list that displays the latest completed sessions.
        JList<String> recentList = new JList<>(recentModel);
        recentList.setVisibleRowCount(3);
        JScrollPane recentScroll = new JScrollPane(recentList);
        recentScroll.setPreferredSize(new Dimension(230, 0));
        recentScroll.setBorder(BorderFactory.createTitledBorder("Recent"));

        // Place the summary in the main area and recent history on the right.
        add(summary, BorderLayout.CENTER);
        add(recentScroll, BorderLayout.EAST);
    }

    // Refresh stats display from current history.
    public void refreshData() {
        // Update aggregate stats by querying current totals and analytics
        lblTotalSessions.setText("Sessions: " + statsManager.getTotalSessions());
        // Find the best session once so the label can handle missing history cleanly.
        StatsRecord best = statsManager.getBestSession();
        lblBestWpm.setText(String.format("Best WPM: %.2f", best == null ? 0.0 : best.getWpm()));
        lblAvgWpm.setText(String.format("Avg WPM: %.2f", analyticsEngine.getAverageWpm()));
        lblAvgAccuracy.setText(String.format("Avg Accuracy: %.2f%%", analyticsEngine.getAverageAccuracy()));
        lblTrend.setText("Trend: " + analyticsEngine.getSpeedTrend());
        lblLifetime.setText("Chars Typed: " + statsManager.getTotalCharactersTyped());

        // Populate recent sessions list.
        // Clear old rows before adding the newest three sessions.
        recentModel.clear();
        // Use a compact timestamp format so recent rows fit in the panel.
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        List<StatsRecord> recent = statsManager.getRecentSessions(3);
        // Display newest sessions first by walking backward through the recent list.
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
