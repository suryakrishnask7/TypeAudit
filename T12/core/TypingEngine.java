package T12.core;

/**
 * Core engine for typing test calculations.
 */
public class TypingEngine {
    private long startTime;
    private long endTime;
    private boolean isRunning;

    // Start the timer.
    public void start() {
        // Capture the start moment and mark the test as active.
        this.startTime = System.currentTimeMillis();
        this.isRunning = true;
    }

    // Stop the timer.
    public void stop() {
        // Only record an end time if a test is currently running.
        if (isRunning) {
            this.endTime = System.currentTimeMillis();
            this.isRunning = false;
        }
    }

    // Get elapsed time in milliseconds.
    public long getTimeTakenMs() {
        // While the test is running, measure time from the start to right now.
        if (isRunning) {
            return System.currentTimeMillis() - startTime;
        }
        // After the test stops, use the fixed end time.
        return endTime - startTime;
    }

    // Get elapsed time in seconds.
    public double getTimeTakenSeconds() {
        return getTimeTakenMs() / 1000.0;
    }

    // Compute words per minute.
    public double calculateWPM(String typedText) {
        // Blank input should not count as typing speed.
        if (typedText == null || typedText.trim().isEmpty())
            return 0;

        // Convert elapsed seconds into minutes because WPM is words per minute.
        double minutes = getTimeTakenSeconds() / 60.0;
        // Guard against impossible or too-small timing values.
        if (minutes <= 0)
            return 0;

        // A standard typing test treats every 5 characters as one word.
        double words = typedText.length() / 5.0;
        // Speed is estimated words divided by elapsed minutes.
        return words / minutes;
    }

    // Count matching characters.
    public int getCorrectCharactersCount(String expected, String typed) {
        // Missing text means no characters can be compared.
        if (expected == null || typed == null)
            return 0;

        // Compare only the shared length so charAt never goes past either string.
        int correct = 0;
        int minLength = Math.min(expected.length(), typed.length());

        // Count each position where expected and typed characters match, ignoring case.
        for (int i = 0; i < minLength; i++) {
            if (Character.toLowerCase(expected.charAt(i)) == Character.toLowerCase(typed.charAt(i))) {
                correct++;
            }
        }
        return correct;
    }

    // Count incorrect characters.
    public int getIncorrectCharactersCount(String expected, String typed) {
        // Missing text means there is nothing useful to count.
        if (expected == null || typed == null)
            return 0;
        // Every typed character that is not correct is treated as an error.
        return typed.length() - getCorrectCharactersCount(expected, typed);
    }

    // Calculate live accuracy based on typed input.
    public double calculateLiveAccuracy(String expected, String typed) {
        // Live accuracy cannot be calculated without expected text.
        if (expected == null || expected.isEmpty())
            return 0.0;
        // Empty typed text should display zero instead of dividing by zero.
        if (typed == null || typed.isEmpty())
            return 0.0;

        // Live accuracy uses typed length so the percentage updates fairly while typing.
        return (double) getCorrectCharactersCount(expected, typed) / typed.length() * 100.0;
    }

    // Calculate final accuracy based on expected text.
    public double calculateFinalAccuracy(String expected, String typed) {
        // Final accuracy needs the target text as the denominator.
        if (expected == null || expected.isEmpty())
            return 0.0;
        // No typed answer means zero final accuracy.
        if (typed == null || typed.isEmpty())
            return 0.0;

        // Final accuracy compares correct characters against the full expected text.
        return (double) getCorrectCharactersCount(expected, typed) / expected.length() * 100.0;
    }

    // Check if the last word matches.
    public boolean isLastWordCorrect(String expected, String typed) {
        // Extract final words so the test can finish as soon as the target ending is reached.
        String expectedLastWord = getLastWord(expected);
        String typedLastWord = getLastWord(typed);

        // The last word must be present and match regardless of capitalization.
        return !expectedLastWord.isEmpty() && expectedLastWord.equalsIgnoreCase(typedLastWord);
    }

    // Extract the final word from the text.
    private String getLastWord(String text) {
        // Empty text has no last word.
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        // Split on whitespace so punctuation and multiple spaces do not break word selection.
        String[] words = text.trim().split("\\s+");
        // Pick the last token and remove punctuation from its edges.
        String word = words[words.length - 1];
        return word.replaceAll("^[^A-Za-z0-9]+|[^A-Za-z0-9]+$", "");
    }
}
