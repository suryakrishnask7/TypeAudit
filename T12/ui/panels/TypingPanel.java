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

/**
 * Panel for typing tests and live performance display.
 */
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
        // Keep the shared stats manager so this panel can save completed tests.
        this.statsManager = statsManager;
        // Store the callback used to tell the stats panel when new data is available.
        this.onStatsChanged = onStatsChanged;
        // Create the typing engine that handles timing, WPM, and accuracy calculations.
        this.engine = new TypingEngine();
        // Build the text areas, labels, button, and live update timer used by the test screen.
        this.txtExpected = createTextArea(false);
        this.txtInput = createTextArea(false);
        this.btnStart = new JButton("Start");
        this.lblWpm = new JLabel("WPM: 0.00");
        this.lblAccuracy = new JLabel("Accuracy: 0.00%");
        this.lblTime = new JLabel("Time: 0s");
        this.lblStatus = new JLabel("Press Start and type the text exactly.");
        this.timer = new Timer(500, e -> updateLiveMetrics());

        // Use a border layout so controls stay above the typing areas.
        setLayout(new BorderLayout(10, 10));
        // Add padding around the panel so controls are not pressed against the window edge.
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // Finish wiring the panel after all components exist.
        initUI();
    }

    private void initUI() {
        // Show a random passage before the user starts typing.
        txtExpected.setText(TextProvider.getRandomText());

        // Listen for any typing changes so live WPM and accuracy update immediately.
        txtInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleTyping();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleTyping();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handleTyping();
            }
        });
        // Map the Enter key to finishing the test instead of inserting a newline.
        txtInput.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "finishTest");
        txtInput.getActionMap().put("finishTest", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // Only finish from Enter if a test is currently active.
                if (running) {
                    finishTest();
                }
            }
        });

        // Put the start button and status message in one row.
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnStart.addActionListener(e -> startTest());
        controls.add(btnStart);
        controls.add(lblStatus);

        // Put live measurements in a three-column row.
        JPanel metrics = new JPanel(new GridLayout(1, 3, 8, 0));
        metrics.add(lblWpm);
        metrics.add(lblAccuracy);
        metrics.add(lblTime);

        // Stack controls above metrics at the top of the panel.
        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.add(controls, BorderLayout.NORTH);
        top.add(metrics, BorderLayout.SOUTH);

        // Stack the target passage over the user's input area.
        JPanel text = new JPanel(new GridLayout(2, 1, 8, 8));
        text.add(new JScrollPane(txtExpected));
        text.add(new JScrollPane(txtInput));

        // Add the top controls and main text area section to the panel.
        add(top, BorderLayout.NORTH);
        add(text, BorderLayout.CENTER);
    }

    // Start a new typing test.
    private void startTest() {
        // Reset the passage and clear any previous typed answer.
        txtExpected.setText(TextProvider.getRandomText());
        txtInput.setText("");
        // Enable typing and send the cursor straight into the input box.
        txtInput.setEditable(true);
        txtInput.requestFocus();

        // Lock the start button while a test is running.
        btnStart.setEnabled(false);
        running = true;
        // Start both the stopwatch and the screen refresh timer.
        engine.start();
        timer.start();
        updateLiveMetrics();
        // Update the UI and log so the app reflects the new test state.
        lblStatus.setText("Typing...");
        FileManager.logActivity("Typing test started.");
    }

    // Handle each typing update while running.
    private void handleTyping() {
        // Ignore document events that happen while no test is active.
        if (!running) {
            return;
        }

        // Recalculate the live metrics after each typing change.
        updateLiveMetrics();
        // Finish automatically once the typed final word matches the passage's final word.
        if (engine.isLastWordCorrect(txtExpected.getText(), txtInput.getText())) {
            finishTest();
        }
    }

    // Finish the typing test and save results.
    private void finishTest() {
        // Freeze timing and stop refreshing live metrics.
        engine.stop();
        timer.stop();
        running = false;

        // Capture the completed input and target text for scoring and saving.
        String typed = txtInput.getText();
        String expected = txtExpected.getText();
        // Prevent more typing until the next test starts.
        txtInput.setEditable(false);
        btnStart.setEnabled(true);

        // Calculate metrics for the completed test.
        double wpm = engine.calculateWPM(typed);
        double accuracy = engine.calculateFinalAccuracy(expected, typed);
        int errors = engine.getIncorrectCharactersCount(expected, typed);
        long durationSec = (long) engine.getTimeTakenSeconds();

        // Show the final score summary on screen.
        lblWpm.setText(String.format("WPM: %.2f", wpm));
        lblAccuracy.setText(String.format("Accuracy: %.2f%%", accuracy));
        lblTime.setText(String.format("Time: %ds", durationSec));
        lblStatus.setText(String.format("Done. Errors: %d", errors));

        // Create and store new session record with all computed metrics
        // UUID sessionId generated automatically; timestamp captured at completion;
        // duration in seconds
        StatsRecord record = new StatsRecord(
                typed,
                expected,
                accuracy,
                wpm,
                System.currentTimeMillis(),
                durationSec,
                errors,
                typed.length());

        statsManager.addRecord(record);
        // Save stats immediately so results are not lost if the app closes.
        FileManager.saveStatsManager(statsManager);
        // Notify parent components (StatsPanel) to refresh statistics display.
        onStatsChanged.run();
        // Add a completion line to the activity log.
        FileManager.logActivity("Typing test completed. WPM: " + wpm + ", Accuracy: " + accuracy);
    }

    // Update live performance metrics on screen.
    private void updateLiveMetrics() {
        // Read the current typed text and calculate metrics from the current timer value.
        String typed = txtInput.getText();
        lblTime.setText(String.format("Time: %.0fs", engine.getTimeTakenSeconds()));
        lblWpm.setText(String.format("WPM: %.2f", engine.calculateWPM(typed)));
        lblAccuracy.setText(String.format(
                "Accuracy: %.2f%%",
                engine.calculateLiveAccuracy(txtExpected.getText(), typed)));
    }

    private JTextArea createTextArea(boolean editable) {
        // Use one helper so both text areas have matching size and behavior.
        JTextArea textArea = new JTextArea(5, 40);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(editable);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        // Disable paste/drag transfer so typing tests measure actual typing.
        textArea.setTransferHandler(null);
        return textArea;
    }
}
