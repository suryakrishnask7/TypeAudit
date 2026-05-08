package T12.stats;

import java.util.List;

/**
 * Computes analytics and trends from typing session history stored in
 * StatsManager.
 * Calculates aggregate metrics: average WPM, average accuracy.
 * Detects performance trends: improving/declining/stable based on recent
 * sessions.
 * Used by StatsPanel to display insights and encourage user improvement through
 * feedback.
 */
public class AnalyticsEngine {

    private StatsManager manager;

    public AnalyticsEngine(StatsManager manager) {
        this.manager = manager;
    }

    // Calculate mean WPM across all sessions by summing WPM values and dividing by
    // session count
    // Returns 0.0 if no sessions recorded; essential for displaying average
    // performance metric
    public double getAverageWpm() {
        if (manager.getHistory().isEmpty())
            return 0.0;
        double total = 0;
        for (StatsRecord r : manager.getHistory()) {
            total += r.getWpm();
        }
        return total / manager.getHistory().size();
    }

    // Calculate mean accuracy across all sessions by summing accuracy values and
    // dividing by session count
    // Returns 0.0 if no sessions recorded; used to display consistency in typing
    // accuracy
    public double getAverageAccuracy() {
        if (manager.getHistory().isEmpty())
            return 0.0;
        double total = 0;
        for (StatsRecord r : manager.getHistory()) {
            total += r.getAccuracy();
        }
        return total / manager.getHistory().size();
    }

    // Compare WPM in recent sessions (last 5) to detect improvement or decline in
    // typing speed
    // Returns status string describing trend direction and magnitude
    // Requires at least 2 sessions to calculate meaningful trend; returns status
    // message otherwise
    public String getSpeedTrend() {
        List<StatsRecord> recent = manager.getRecentSessions(5);
        if (recent.size() < 2)
            return "Not enough data for trend";

        double first = recent.get(0).getWpm();
        double last = recent.get(recent.size() - 1).getWpm();

        if (last > first) {
            return String.format("Improving (+%.1f WPM)", last - first);
        } else if (last < first) {
            return String.format("Declining (%.1f WPM)", last - first);
        } else {
            return "Stable";
        }
    }

    public String getAccuracyTrend() {
        List<StatsRecord> recent = manager.getRecentSessions(5);
        if (recent.size() < 2)
            return "Not enough data";

        double first = recent.get(0).getAccuracy();
        double last = recent.get(recent.size() - 1).getAccuracy();

        if (last > first) {
            return String.format("Improving (+%.1f%%)", last - first);
        } else if (last < first) {
            return String.format("Declining (%.1f%%)", last - first);
        } else {
            return "Stable";
        }
    }
}
