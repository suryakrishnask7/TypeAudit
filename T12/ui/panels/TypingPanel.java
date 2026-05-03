package T12.ui.panels;

import T12.core.AccuracyCalculator;
import T12.core.CompletionDetector;
import T12.core.TypingEngine;
import T12.exception.InvalidInputException;
import T12.stats.StatsManager;
import T12.stats.StatsRecord;
import T12.util.FileManager;
import T12.util.TextProvider;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class TypingPanel extends JPanel {
    private JTextArea txtExpected;
    private JTextArea txtInput;
    private JButton btnStart;
    private JButton btnSaveCustom;
    private JCheckBox chkCustomText;
    private JLabel lblLiveWpm;
    private JLabel lblLiveAccuracy;
    private JLabel lblTime;

    private TypingEngine engine;
    private AccuracyCalculator accuracyCalculator;
    private StatsManager statsManager;
    private Timer timer;

    public TypingPanel(StatsManager statsManager) {
        this.statsManager = statsManager;
        this.engine = new TypingEngine();
        this.accuracyCalculator = new AccuracyCalculator();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initUI();
    }

    private void initUI() {
        // Text areas
        txtExpected = new JTextArea(6, 40);
        txtExpected.setLineWrap(true);
        txtExpected.setWrapStyleWord(true);
        txtExpected.setEditable(false);
        txtExpected.setText(TextProvider.getRandomText());
        txtExpected.setFont(new Font("Monospaced", Font.PLAIN, 16));

        txtInput = new JTextArea(6, 40);
        txtInput.setLineWrap(true);
        txtInput.setWrapStyleWord(true);
        txtInput.setEditable(false);
        txtInput.setFont(new Font("Monospaced", Font.PLAIN, 16));
        txtInput.setTransferHandler(null); // Disable copy/paste
        
        // Add listener for real-time processing and auto-submit
        txtInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { handleTyping(); }
            @Override
            public void removeUpdate(DocumentEvent e) { handleTyping(); }
            @Override
            public void changedUpdate(DocumentEvent e) { handleTyping(); }
        });

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        textPanel.add(new JScrollPane(txtExpected));
        textPanel.add(new JScrollPane(txtInput));

        // Controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnStart = new JButton("Start");
        chkCustomText = new JCheckBox("Use Custom Text");
        btnSaveCustom = new JButton("Save Custom Text");
        btnSaveCustom.setEnabled(false);

        btnStart.addActionListener(e -> startTest());
        
        chkCustomText.addActionListener(e -> {
            boolean isCustom = chkCustomText.isSelected();
            txtExpected.setEditable(isCustom);
            btnSaveCustom.setEnabled(isCustom);
            if (isCustom) {
                String saved = TextProvider.loadCustomText();
                txtExpected.setText(saved);
                txtExpected.requestFocus();
            } else {
                txtExpected.setText(TextProvider.getRandomText());
            }
        });
        
        btnSaveCustom.addActionListener(e -> {
            try {
                TextProvider.saveCustomText(txtExpected.getText());
                JOptionPane.showMessageDialog(this, "Custom text saved!");
            } catch (IllegalArgumentException | IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        controlPanel.add(btnStart);
        controlPanel.add(chkCustomText);
        controlPanel.add(btnSaveCustom);

        // Stats Labels
        JPanel statsPanel = new JPanel(new GridLayout(1, 3));
        lblLiveWpm = new JLabel("Live WPM: 0.00", SwingConstants.CENTER);
        lblLiveAccuracy = new JLabel("Live Accuracy: 0.00%", SwingConstants.CENTER);
        lblTime = new JLabel("Time: 0s", SwingConstants.CENTER);
        
        lblLiveWpm.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblLiveAccuracy.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTime.setFont(new Font("SansSerif", Font.BOLD, 14));

        statsPanel.add(lblLiveWpm);
        statsPanel.add(lblLiveAccuracy);
        statsPanel.add(lblTime);

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(controlPanel, BorderLayout.NORTH);
        topPanel.add(statsPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);

        // Timer for updating UI
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateLiveTimeAndWPM();
            }
        });
    }

    private void startTest() {
        if (chkCustomText.isSelected() && txtExpected.getText().trim().length() < 20) {
            JOptionPane.showMessageDialog(this, "Custom text must be at least 20 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!chkCustomText.isSelected()) {
            txtExpected.setText(TextProvider.getRandomText());
        }
        
        txtInput.setText("");
        txtInput.setEditable(true);
        txtInput.requestFocus();
        txtExpected.setEditable(false);

        btnStart.setEnabled(false);
        chkCustomText.setEnabled(false);
        btnSaveCustom.setEnabled(false);

        engine.start();
        timer.start();
        FileManager.logActivity("Typing test started.");
    }
    
    private void handleTyping() {
        if (!btnStart.isEnabled()) { // If test is running
            String expected = txtExpected.getText();
            String typed = txtInput.getText();
            
            // Real-time metrics
            double acc = accuracyCalculator.calculateLiveAccuracy(expected, typed);
            lblLiveAccuracy.setText(String.format("Live Accuracy: %.2f%%", acc));
            
            // Auto submit
            if (CompletionDetector.isComplete(expected, typed)) {
                submitTest();
            }
        }
    }

    private void submitTest() {
        engine.stop();
        timer.stop();

        String typed = txtInput.getText();
        String expected = txtExpected.getText();
        
        txtInput.setEditable(false);
        btnStart.setEnabled(true);
        chkCustomText.setEnabled(true);
        btnSaveCustom.setEnabled(chkCustomText.isSelected());

        double wpm = engine.calculateWPM(typed);
        double accuracy = accuracyCalculator.calculateFinalAccuracy(expected, typed);
        int errors = accuracyCalculator.getIncorrectCharactersCount(expected, typed);
        long durationSec = (long) engine.getTimeTakenSeconds();

        // Update UI
        lblLiveWpm.setText(String.format("WPM: %.2f", wpm));
        lblLiveAccuracy.setText(String.format("Final Accuracy: %.2f%%", accuracy));
        
        // Record stats
        StatsRecord record = new StatsRecord(
                typed,
                expected,
                accuracy,
                wpm,
                System.currentTimeMillis(),
                durationSec,
                errors,
                typed.length()
        );
        
        statsManager.addRecord(record);
        FileManager.saveStatsManager(statsManager);
        FileManager.logActivity("Typing test auto-submitted. WPM: " + wpm + ", Accuracy: " + accuracy);

        JOptionPane.showMessageDialog(this, 
            String.format("Test Finished Successfully!\nWPM: %.2f\nAccuracy: %.2f%%\nErrors: %d\nTime: %ds", 
                          wpm, accuracy, errors, durationSec),
            "Results", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateLiveTimeAndWPM() {
        double seconds = engine.getTimeTakenSeconds();
        lblTime.setText(String.format("Time: %.0fs", seconds));
        
        String typed = txtInput.getText();
        if (typed.length() > 0) {
            double wpm = engine.calculateWPM(typed);
            lblLiveWpm.setText(String.format("Live WPM: %.2f", wpm));
        }
    }
}
