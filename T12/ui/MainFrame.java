package T12.ui;

import T12.stats.StatsManager;
import T12.ui.panels.StatsPanel;
import T12.ui.panels.TypingPanel;
import T12.util.FileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application window container.
 * Manages the overall UI layout by composing TypingPanel (typing test
 * interface) and StatsPanel (statistics display).
 * Handles persistence by loading stats on startup and saving them on
 * application exit.
 * Coordinates data flow: TypingPanel updates statistics which triggers
 * StatsPanel refresh.
 */
public class MainFrame extends JFrame {

    private final StatsManager statsManager;
    private StatsPanel statsPanel;

    public MainFrame() {
        super("TypeAudit");

        // Load persisted statistics from previous sessions to maintain historical data
        // across application restarts
        statsManager = FileManager.loadStatsManager();

        // Build the UI layout with typing panel (top/center) and stats panel (bottom)
        initUI();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                FileManager.saveStatsManager(statsManager);
                FileManager.logActivity("Application exited.");
                System.exit(0);
            }
        });
    }

    private void initUI() {
        setSize(760, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Create stats panel and typing panel with callback: when test completes,
        // refresh stats display
        // This creates a responsive feedback loop where test results immediately update
        // analytics
        statsPanel = new StatsPanel(statsManager);
        TypingPanel typingPanel = new TypingPanel(statsManager, statsPanel::refreshData);

        add(typingPanel, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.SOUTH);
    }
}
