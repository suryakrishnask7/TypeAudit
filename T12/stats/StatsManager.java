package T12.stats;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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
        
        // Update cumulative stats using BigInteger
        totalKeystrokes = totalKeystrokes.add(BigInteger.valueOf(record.getTotalKeystrokes()));
        totalCharactersTyped = totalCharactersTyped.add(BigInteger.valueOf(record.getTypedText().length()));
        totalSessions = totalSessions.add(BigInteger.ONE);
        
        long wordsInRecord = (long) Math.ceil(record.getTypedText().length() / 5.0);
        totalWords = totalWords.add(BigInteger.valueOf(wordsInRecord));
    }

    public ArrayList<StatsRecord> getHistory() {
        return history;
    }

    public BigInteger getTotalKeystrokes() { return totalKeystrokes; }
    public BigInteger getTotalWords() { return totalWords; }
    public BigInteger getTotalSessions() { return totalSessions; }
    public BigInteger getTotalCharactersTyped() { return totalCharactersTyped; }
    
    public StatsRecord getBestSession() {
        if (history.isEmpty()) return null;
        StatsRecord best = history.get(0);
        for (StatsRecord r : history) {
            if (r.getWpm() > best.getWpm()) {
                best = r;
            }
        }
        return best;
    }
    
    public List<StatsRecord> getRecentSessions(int n) {
        if (history.isEmpty()) return new ArrayList<>();
        int count = Math.min(n, history.size());
        int start = history.size() - count;
        return new ArrayList<>(history.subList(start, history.size()));
    }
}
