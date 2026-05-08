package T12;

import T12.ui.MainFrame;
import T12.util.FileManager;

import javax.swing.*;

/**
 * Entry point for the TypeAudit application.
 * Initializes the Swing GUI on the Event Dispatch Thread to ensure thread-safe
 * UI rendering.
 * Loads the system look-and-feel for a native appearance and creates the main
 * application frame.
 */
public class MainApp {

    public static void main(String[] args) {
        FileManager.logActivity("Application started.");

        // Initialize GUI on Event Dispatch Thread to ensure thread-safe rendering and
        // event handling
        // This prevents race conditions when updating UI components from multiple
        // threads
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Could not set system look and feel: " + e.getMessage());
            }

            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
