package T12.ui;

import T12.stats.StatsManager;
import T12.ui.panels.StatsPanel;
import T12.ui.panels.TypingPanel;
import T12.util.FileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    private StatsManager statsManager;
    private TypingPanel typingPanel;
    private StatsPanel statsPanel;

    public MainFrame() {
        super("T12 TypeAudit - Typing Speed & Accuracy Analyzer");
        
        // Load stats from file
        statsManager = FileManager.loadStatsManager();

        initUI();
        
        // Handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Ensure latest stats are saved before exit
                FileManager.saveStatsManager(statsManager);
                FileManager.logActivity("Application exited.");
                System.exit(0);
            }
        });
    }

    private void initUI() {
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // handled by WindowAdapter
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        typingPanel = new TypingPanel(statsManager);
        statsPanel = new StatsPanel(statsManager);
        
        tabbedPane.addTab("Typing Test", typingPanel);
        tabbedPane.addTab("Statistics", statsPanel);
        
        // Add listener to refresh stats panel when its tab is selected
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == statsPanel) {
                statsPanel.refreshData();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }
}
