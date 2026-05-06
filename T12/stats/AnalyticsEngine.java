package T12.stats;

import java.util.List;

public class AnalyticsEngine {
    
    private StatsManager manager;

    public AnalyticsEngine(StatsManager manager) {
        this.manager = manager;
    }

    public double getAverageWpm() {
        if (manager.getHistory().isEmpty()) return 0.0;
        double total = 0;
        for (StatsRecord r : manager.getHistory()) {
            total += r.getWpm();
        }
        return total / manager.getHistory().size();
    }
    
    public double getAverageAccuracy() {
        if (manager.getHistory().isEmpty()) return 0.0;
        double total = 0;
        for (StatsRecord r : manager.getHistory()) {
            total += r.getAccuracy();
        }
        return total / manager.getHistory().size();
    }

    public String getSpeedTrend() {
        List<StatsRecord> recent = manager.getRecentSessions(5);
        if (recent.size() < 2) return "Not enough data for trend";
        
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
        if (recent.size() < 2) return "Not enough data";
        
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
