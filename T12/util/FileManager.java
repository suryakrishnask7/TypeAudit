package T12.util;

import T12.stats.StatsManager;
import java.io.*;

/**
 * Utility class for persistent data storage and activity logging.
 * Handles serialization/deserialization of StatsManager to 'stats.dat' for
 * session persistence across application restarts.
 * Maintains a timestamped activity log in 'log.txt' for debugging and user
 * activity tracking.
 * Gracefully handles corrupted or missing data files by creating fresh
 * StatsManager instances.
 */
public class FileManager {
    private static final String DATA_FILE = "stats.dat";
    private static final String LOG_FILE = "log.txt";

    // Serialize entire StatsManager object to disk for persistence
    // Overwrites existing file; logs success or I/O errors to activity log
    public static void saveStatsManager(StatsManager manager) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(manager);
            logActivity("StatsManager saved successfully.");
        } catch (IOException e) {
            logActivity("Error saving StatsManager: " + e.getMessage());
        }
    }

    // Deserialize StatsManager from disk; creates new instance if file missing or
    // corrupted
    // Catches InvalidClassException for version mismatches; falls back to fresh
    // StatsManager on errors
    // Graceful degradation ensures application never crashes due to data loading
    // issues
    public static StatsManager loadStatsManager() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            logActivity("No previous stats found. Initializing new StatsManager.");
            return new StatsManager();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            StatsManager manager = (StatsManager) ois.readObject();
            logActivity("StatsManager loaded successfully.");
            return manager;
        } catch (InvalidClassException e) {
            logActivity("Stats data structure changed. Initializing fresh StatsManager. " + e.getMessage());
            return new StatsManager();
        } catch (Throwable e) { // Catch Throwable instead of Exception to handle Errors like
                                // NoClassDefFoundError
            logActivity("Error loading StatsManager or corrupted file: " + e.getMessage());
            return new StatsManager();
        }
    }

    // Append timestamped log entry for debugging, error tracking, and user activity
    // audit trail
    // Log entries help diagnose application behavior and reconstruct usage history
    public static void logActivity(String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(System.currentTimeMillis() + " - " + message);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}
