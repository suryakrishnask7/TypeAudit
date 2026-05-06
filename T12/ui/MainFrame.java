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

    private final StatsManager statsManager;
    private StatsPanel statsPanel;

    public MainFrame() {
        super("TypeAudit");
        
        statsManager = FileManager.loadStatsManager();

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
        
        statsPanel = new StatsPanel(statsManager);
        TypingPanel typingPanel = new TypingPanel(statsManager, statsPanel::refreshData);
        
        add(typingPanel, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.SOUTH);
    }
}
