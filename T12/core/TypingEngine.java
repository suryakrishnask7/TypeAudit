package T12.core;

/**
 * Core engine for typing test mechanics and performance calculations.
 * Tracks elapsed time for a typing session and computes metrics like WPM and
 * accuracy.
 * Provides both live accuracy (user-typed length) and final accuracy (expected
 * text length).
 * Character comparison is case-insensitive to allow flexible typing standards.
 */
public class TypingEngine {
    private long startTime;
    private long endTime;
    private boolean isRunning;

    // Record start timestamp when typing test begins; enables elapsed time
    // calculations
    public void start() {
        this.startTime = System.currentTimeMillis();
        this.isRunning = true;
    }

    // Record end timestamp when typing test stops; prevents further time
    // accumulation
    public void stop() {
        if (isRunning) {
            this.endTime = System.currentTimeMillis();
            this.isRunning = false;
        }
    }

    public long getTimeTakenMs() {
        if (isRunning) {
            return System.currentTimeMillis() - startTime;
        }
        return endTime - startTime;
    }

    public double getTimeTakenSeconds() {
        return getTimeTakenMs() / 1000.0;
    }

    // Calculate words per minute using standard formula: (characters typed / 5) /
    // time in minutes
    // Treats empty/null input as 0 WPM; requires non-zero duration to avoid
    // division errors
    public double calculateWPM(String typedText) {
        if (typedText == null || typedText.trim().isEmpty())
            return 0;

        double minutes = getTimeTakenSeconds() / 60.0;
        if (minutes <= 0)
            return 0;

        double words = typedText.length() / 5.0;
        return words / minutes;
    }

    public int getCorrectCharactersCount(String expected, String typed) {
        if (expected == null || typed == null)
            return 0;

        int correct = 0;
        int minLength = Math.min(expected.length(), typed.length());

        for (int i = 0; i < minLength; i++) {
            if (Character.toLowerCase(expected.charAt(i)) == Character.toLowerCase(typed.charAt(i))) {
                correct++;
            }
        }
        return correct;
    }

    public int getIncorrectCharactersCount(String expected, String typed) {
        if (expected == null || typed == null)
            return 0;
        return typed.length() - getCorrectCharactersCount(expected, typed);
    }

    // Live accuracy: percentage of correctly typed characters relative to what user
    // has typed
    // Updates dynamically as user types; denominator is user input length (not
    // expected text length)
    public double calculateLiveAccuracy(String expected, String typed) {
        if (expected == null || expected.isEmpty())
            return 0.0;
        if (typed == null || typed.isEmpty())
            return 0.0;

        return (double) getCorrectCharactersCount(expected, typed) / typed.length() * 100.0;
    }

    // Final accuracy: percentage of correctly typed characters relative to expected
    // text
    // Denominator is expected text length; provides true correctness assessment at
    // test end
    public double calculateFinalAccuracy(String expected, String typed) {
        if (expected == null || expected.isEmpty())
            return 0.0;
        if (typed == null || typed.isEmpty())
            return 0.0;

        return (double) getCorrectCharactersCount(expected, typed) / expected.length() * 100.0;
    }

    public boolean isLastWordCorrect(String expected, String typed) {
        String expectedLastWord = getLastWord(expected);
        String typedLastWord = getLastWord(typed);

        return !expectedLastWord.isEmpty() && expectedLastWord.equalsIgnoreCase(typedLastWord);
    }

    private String getLastWord(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        String[] words = text.trim().split("\\s+");
        String word = words[words.length - 1];
        return word.replaceAll("^[^A-Za-z0-9]+|[^A-Za-z0-9]+$", "");
    }
}
