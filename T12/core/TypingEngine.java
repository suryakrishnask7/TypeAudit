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
        
        // Standard WPM calculation: (characters / 5) / minutes
        double minutes = getTimeTakenSeconds() / 60.0;
        if (minutes <= 0) return 0;
        
        double words = typedText.length() / 5.0;
        return words / minutes;
    }
}
