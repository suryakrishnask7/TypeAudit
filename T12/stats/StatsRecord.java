package T12.stats;

import java.io.Serializable;
import java.util.UUID;

/**
 * Data model representing a single completed typing test session.
 * Captures user input, performance metrics (WPM, accuracy, errors), timing, and
 * generates a unique session ID.
 * Implements Serializable to enable persistence through FileManager
 * serialization.
 * Immutable after construction to maintain data integrity in historical
 * records.
 */
public class StatsRecord implements Serializable {
    private static final long serialVersionUID = 2L; // Updated version

    private String sessionId;
    private String typedText;
    private String expectedText;
    private double accuracy;
    private double wpm;
    private long timestamp;
    private long duration; // in seconds
    private int errorCount;
    private int totalKeystrokes;

    public StatsRecord(String typedText, String expectedText, double accuracy, double wpm, long timestamp,
            long duration, int errorCount, int totalKeystrokes) {
        // Generate unique UUID for this session to enable precise session tracking and
        // auditing
        this.sessionId = UUID.randomUUID().toString();
        this.typedText = typedText;
        this.expectedText = expectedText;
        this.accuracy = accuracy;
        this.wpm = wpm;
        this.timestamp = timestamp;
        this.duration = duration;
        this.errorCount = errorCount;
        this.totalKeystrokes = totalKeystrokes;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getTypedText() {
        return typedText;
    }

    public String getExpectedText() {
        return expectedText;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getWpm() {
        return wpm;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getDuration() {
        return duration;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public int getTotalKeystrokes() {
        return totalKeystrokes;
    }
}

class Student {
    int rollno;
}