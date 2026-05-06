package T12.ui.panels;

import T12.core.TypingEngine;
import T12.stats.StatsManager;
import T12.stats.StatsRecord;
import T12.util.FileManager;
import T12.util.TextProvider;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class TypingPanel extends JPanel {
    private final JTextArea txtExpected;
    private final JTextArea txtInput;
    private final JButton btnStart;
    private final JLabel lblWpm;
    private final JLabel lblAccuracy;
    private final JLabel lblTime;
    private final JLabel lblStatus;

    private final TypingEngine engine;
    private final StatsManager statsManager;
    private final Runnable onStatsChanged;
    private final Timer timer;
    private boolean running;

    public TypingPanel(StatsManager statsManager, Runnable onStatsChanged) {
        this.statsManager = statsManager;
        this.onStatsChanged = onStatsChanged;
        this.engine = new TypingEngine();
        this.txtExpected = createTextArea(false);
        this.txtInput = createTextArea(false);
        this.btnStart = new JButton("Start");
        this.lblWpm = new JLabel("WPM: 0.00");
        this.lblAccuracy = new JLabel("Accuracy: 0.00%");
        this.lblTime = new JLabel("Time: 0s");
        this.lblStatus = new JLabel("Press Start and type the text exactly.");
        this.timer = new Timer(500, e -> updateLiveMetrics());

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
    }

    private void initUI() {
        txtExpected.setText(TextProvider.getRandomText());

        txtInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { handleTyping(); }
            @Override public void removeUpdate(DocumentEvent e) { handleTyping(); }
            @Override public void changedUpdate(DocumentEvent e) { handleTyping(); }
        });
        txtInput.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "finishTest");
        txtInput.getActionMap().put("finishTest", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (running) {
                    finishTest();
                }
            }
        });

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnStart.addActionListener(e -> startTest());
        controls.add(btnStart);
        controls.add(lblStatus);

        JPanel metrics = new JPanel(new GridLayout(1, 3, 8, 0));
        metrics.add(lblWpm);
        metrics.add(lblAccuracy);
        metrics.add(lblTime);

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.add(controls, BorderLayout.NORTH);
        top.add(metrics, BorderLayout.SOUTH);

        JPanel text = new JPanel(new GridLayout(2, 1, 8, 8));
        text.add(new JScrollPane(txtExpected));
        text.add(new JScrollPane(txtInput));

        add(top, BorderLayout.NORTH);
        add(text, BorderLayout.CENTER);
    }

    private void startTest() {
        txtExpected.setText(TextProvider.getRandomText());
        txtInput.setText("");
        txtInput.setEditable(true);
        txtInput.requestFocus();

        btnStart.setEnabled(false);
        running = true;
        engine.start();
        timer.start();
        updateLiveMetrics();
        lblStatus.setText("Typing...");
        FileManager.logActivity("Typing test started.");
    }

    private void handleTyping() {
        if (!running) {
            return;
        }

        updateLiveMetrics();
        if (engine.isLastWordCorrect(txtExpected.getText(), txtInput.getText())) {
            finishTest();
        }
    }

    private void finishTest() {
        engine.stop();
        timer.stop();
        running = false;

        String typed = txtInput.getText();
        String expected = txtExpected.getText();
        txtInput.setEditable(false);
        btnStart.setEnabled(true);

        double wpm = engine.calculateWPM(typed);
        double accuracy = engine.calculateFinalAccuracy(expected, typed);
        int errors = engine.getIncorrectCharactersCount(expected, typed);
        long durationSec = (long) engine.getTimeTakenSeconds();

        lblWpm.setText(String.format("WPM: %.2f", wpm));
        lblAccuracy.setText(String.format("Accuracy: %.2f%%", accuracy));
        lblTime.setText(String.format("Time: %ds", durationSec));
        lblStatus.setText(String.format("Done. Errors: %d", errors));

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
        onStatsChanged.run();
        FileManager.logActivity("Typing test completed. WPM: " + wpm + ", Accuracy: " + accuracy);
    }

    private void updateLiveMetrics() {
        String typed = txtInput.getText();
        lblTime.setText(String.format("Time: %.0fs", engine.getTimeTakenSeconds()));
        lblWpm.setText(String.format("WPM: %.2f", engine.calculateWPM(typed)));
        lblAccuracy.setText(String.format(
                "Accuracy: %.2f%%",
                engine.calculateLiveAccuracy(txtExpected.getText(), typed)
        ));
    }

    private JTextArea createTextArea(boolean editable) {
        JTextArea textArea = new JTextArea(5, 40);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(editable);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        textArea.setTransferHandler(null);
        return textArea;
    }
}
