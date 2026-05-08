package T12.stats;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for typing session statistics.
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
        // Keep the full session so it can appear in history and analytics later.
        history.add(record);

        // Update total keystrokes.
        totalKeystrokes = totalKeystrokes.add(BigInteger.valueOf(record.getTotalKeystrokes()));
        // Update total characters typed.
        totalCharactersTyped = totalCharactersTyped.add(BigInteger.valueOf(record.getTypedText().length()));
        // Increase total session count.
        totalSessions = totalSessions.add(BigInteger.ONE);

        // Estimate words using the typing-test convention of 5 characters per word.
        long wordsInRecord = (long) Math.ceil(record.getTypedText().length() / 5.0);
        // Update total words estimate.
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

    // Find the session with the highest WPM.
    public StatsRecord getBestSession() {
        // If there is no history, there cannot be a best session yet.
        if (history.isEmpty())
            return null;
        // Start with the first session and replace it whenever a faster one appears.
        StatsRecord best = history.get(0);
        for (StatsRecord r : history) {
            if (r.getWpm() > best.getWpm()) {
                best = r;
            }
        }
        // Return the fastest session found.
        return best;
    }

    // Retrieve the last n recorded sessions.
    public List<StatsRecord> getRecentSessions(int n) {
        // Return an empty list instead of null so callers can loop safely.
        if (history.isEmpty())
            return new ArrayList<>();
        // Do not request more sessions than the history actually contains.
        int count = Math.min(n, history.size());
        // Calculate where the recent slice begins.
        int start = history.size() - count;
        // Return a copy so outside code cannot accidentally edit the original history list.
        return new ArrayList<>(history.subList(start, history.size()));
    }
}
