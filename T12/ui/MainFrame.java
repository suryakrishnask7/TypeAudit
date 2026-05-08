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
 * Main application window.
 */
public class MainFrame extends JFrame {

    private final StatsManager statsManager;
    private StatsPanel statsPanel;

    public MainFrame() {
        super("TypeAudit");

        // Load saved statistics.
        statsManager = FileManager.loadStatsManager();

        // Set up the UI.
        initUI();

        // Intercept window closing so stats are saved before the process exits.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Save stats before exit.
                FileManager.saveStatsManager(statsManager);
                // Log exit.
                FileManager.logActivity("Application exited.");
                // Close the app.
                System.exit(0);
            }
        });
    }

    private void initUI() {
        // Set window size.
        setSize(760, 520);
        // Center the window.
        setLocationRelativeTo(null);
        // Do not close automatically.
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Create the stats panel.
        statsPanel = new StatsPanel(statsManager);
        // Create the typing panel with stats refresh callback.
        TypingPanel typingPanel = new TypingPanel(statsManager, statsPanel::refreshData);

        // Place typing panel in center.
        add(typingPanel, BorderLayout.CENTER);
        // Place stats panel at bottom.
        add(statsPanel, BorderLayout.SOUTH);
    }
}
