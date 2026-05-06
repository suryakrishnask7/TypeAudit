package T12.core;

public class TypingEngine {
    private long startTime;
    private long endTime;
    private boolean isRunning;

    public void start() {
        this.startTime = System.currentTimeMillis();
        this.isRunning = true;
    }

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

    public double calculateWPM(String typedText) {
        if (typedText == null || typedText.trim().isEmpty()) return 0;
        
        double minutes = getTimeTakenSeconds() / 60.0;
        if (minutes <= 0) return 0;
        
        double words = typedText.length() / 5.0;
        return words / minutes;
    }

    public int getCorrectCharactersCount(String expected, String typed) {
        if (expected == null || typed == null) return 0;

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
        if (expected == null || typed == null) return 0;
        return typed.length() - getCorrectCharactersCount(expected, typed);
    }

    public double calculateLiveAccuracy(String expected, String typed) {
        if (expected == null || expected.isEmpty()) return 0.0;
        if (typed == null || typed.isEmpty()) return 0.0;

        return (double) getCorrectCharactersCount(expected, typed) / typed.length() * 100.0;
    }

    public double calculateFinalAccuracy(String expected, String typed) {
        if (expected == null || expected.isEmpty()) return 0.0;
        if (typed == null || typed.isEmpty()) return 0.0;

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
