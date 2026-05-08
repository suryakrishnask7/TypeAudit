package T12.stats;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Central repository for all typing session statistics.
 * Maintains lifetime aggregates (total keystrokes, words, sessions, characters)
 * using BigInteger for large numbers.
 * Preserves complete history of StatsRecord objects for trend analysis.
 * Enables queries like best session and recent sessions for analytics.
 * Serializable to enable persistence via FileManager.
 */
public class StatsManager implements Serializable {
    private static final long serialVersionUID = 2L;

    private BigInteger totalKeystrokes;
    private BigInteger totalWords;
    private BigInteger totalSessions;
    private BigInteger totalCharactersTyped;

    private ArrayList<StatsRecord> history;

    public StatsManager() {
        this.totalKeystrokes = BigInteger.ZERO;
        this.totalWords = BigInteger.ZERO;
        this.totalSessions = BigInteger.ZERO;
        this.totalCharactersTyped = BigInteger.ZERO;
        this.history = new ArrayList<>();
    }

    public void addRecord(StatsRecord record) {
        history.add(record);

        // Update running totals for lifetime statistics by extracting metrics from the
        // new session
        // Uses BigInteger to support users with extremely high keystroke counts without
        // overflow
        totalKeystrokes = totalKeystrokes.add(BigInteger.valueOf(record.getTotalKeystrokes()));
        totalCharactersTyped = totalCharactersTyped.add(BigInteger.valueOf(record.getTypedText().length()));
        totalSessions = totalSessions.add(BigInteger.ONE);

        long wordsInRecord = (long) Math.ceil(record.getTypedText().length() / 5.0);
        totalWords = totalWords.add(BigInteger.valueOf(wordsInRecord));
    }

    public ArrayList<StatsRecord> getHistory() {
        return history;
    }

    public BigInteger getTotalKeystrokes() {
        return totalKeystrokes;
    }

    public BigInteger getTotalWords() {
        return totalWords;
    }

    public BigInteger getTotalSessions() {
        return totalSessions;
    }

    public BigInteger getTotalCharactersTyped() {
        return totalCharactersTyped;
    }

    // Find the session with highest WPM among all recorded sessions
    // Returns null if history is empty; useful for displaying peak performance
    public StatsRecord getBestSession() {
        if (history.isEmpty())
            return null;
        StatsRecord best = history.get(0);
        for (StatsRecord r : history) {
            if (r.getWpm() > best.getWpm()) {
                best = r;
            }
        }
        return best;
    }

    // Retrieve the last n sessions in chronological order for trend analysis
    // Used by AnalyticsEngine to detect speed or accuracy improvements/declines
    public List<StatsRecord> getRecentSessions(int n) {
        if (history.isEmpty())
            return new ArrayList<>();
        int count = Math.min(n, history.size());
        int start = history.size() - count;
        return new ArrayList<>(history.subList(start, history.size()));
    }
}
