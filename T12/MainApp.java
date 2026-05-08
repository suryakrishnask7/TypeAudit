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
        // Log startup.
        FileManager.logActivity("Application started.");

        // Start the GUI on the Swing event thread.
        SwingUtilities.invokeLater(() -> {
            // Keep look-and-feel setup isolated so a UI theme failure does not stop startup.
            try {
                // Set the system look and feel.
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Print if the look and feel fails.
                System.err.println("Could not set system look and feel: " + e.getMessage());
            }

            // Create the main window.
            MainFrame mainFrame = new MainFrame();
            // Display the window.
            mainFrame.setVisible(true);
        });
    }
}
