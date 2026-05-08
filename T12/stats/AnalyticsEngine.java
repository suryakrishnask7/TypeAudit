package T12.stats;

import java.util.List;

/**
 * Computes analytics from typing history.
 */
public class AnalyticsEngine {

    private StatsManager manager;

    public AnalyticsEngine(StatsManager manager) {
        this.manager = manager;
    }

    // Calculate average WPM.
    public double getAverageWpm() {
        // Avoid dividing by zero when no typing sessions have been saved yet.
        if (manager.getHistory().isEmpty())
            return 0.0;
        // Add every session's WPM so the total can be averaged.
        double total = 0;
        for (StatsRecord r : manager.getHistory()) {
            total += r.getWpm();
        }
        // Divide by the number of sessions to get the average speed.
        return total / manager.getHistory().size();
    }

    // Calculate average accuracy.
    public double getAverageAccuracy() {
        // Avoid dividing by zero when there is no accuracy history.
        if (manager.getHistory().isEmpty())
            return 0.0;
        // Add every session's accuracy percentage.
        double total = 0;
        for (StatsRecord r : manager.getHistory()) {
            total += r.getAccuracy();
        }
        // Divide by the number of sessions to get the average accuracy.
        return total / manager.getHistory().size();
    }

    // Compute speed trend from the last 5 sessions.
    public String getSpeedTrend() {
        // Only the most recent sessions are used so the trend reflects current performance.
        List<StatsRecord> recent = manager.getRecentSessions(5);
        // A trend needs at least two points to compare.
        if (recent.size() < 2)
            return "Not enough data for trend";

        // Compare the oldest and newest values in the recent-session window.
        double first = recent.get(0).getWpm();
        double last = recent.get(recent.size() - 1).getWpm();

        // Report whether the user's speed went up, down, or stayed the same.
        if (last > first) {
            return String.format("Improving (+%.1f WPM)", last - first);
        } else if (last < first) {
            return String.format("Declining (%.1f WPM)", last - first);
        } else {
            return "Stable";
        }
    }

    // Compute accuracy trend from the last 5 sessions.
    public String getAccuracyTrend() {
        // Use the same recent window so accuracy trend matches the speed trend period.
        List<StatsRecord> recent = manager.getRecentSessions(5);
        // A trend needs at least two completed tests.
        if (recent.size() < 2)
            return "Not enough data";

        // Compare the earliest and latest accuracy values in the recent list.
        double first = recent.get(0).getAccuracy();
        double last = recent.get(recent.size() - 1).getAccuracy();

        // Report whether the user's accuracy improved, dropped, or stayed stable.
        if (last > first) {
            return String.format("Improving (+%.1f%%)", last - first);
        } else if (last < first) {
            return String.format("Declining (%.1f%%)", last - first);
        } else {
            return "Stable";
        }
    }
}
