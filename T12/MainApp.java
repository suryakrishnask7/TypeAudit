package T12;

import T12.ui.MainFrame;
import T12.util.FileManager;

import javax.swing.*;

public class MainApp {

    public static void main(String[] args) {
        FileManager.logActivity("Application started.");
        
        // Ensure GUI is created on Event Dispatch Thread
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
