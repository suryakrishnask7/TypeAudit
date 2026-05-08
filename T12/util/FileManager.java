package T12.util;

import T12.stats.StatsManager;
import java.io.*;

/**
 * Utility class for persistence and logging.
 */
public class FileManager {
    private static final String DATA_FILE = "stats.dat";
    private static final String LOG_FILE = "log.txt";

    // Serialize entire StatsManager object to disk for persistence
    // Overwrites existing file; logs success or I/O errors to activity log
    public static void saveStatsManager(StatsManager manager) {
        // Open the stats file and wrap it in an object stream so the whole manager can be saved.
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            // Write the current totals and history to stats.dat.
            oos.writeObject(manager);
            // Record a success message so the log shows that saving worked.
            logActivity("StatsManager saved successfully.");
        } catch (IOException e) {
            // If saving fails, keep the app running and write the error to the log.
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
        // Point to the file where saved statistics should be stored.
        File file = new File(DATA_FILE);
        // If the stats file has never been created, start with a clean stats manager.
        if (!file.exists()) {
            logActivity("No previous stats found. Initializing new StatsManager.");
            return new StatsManager();
        }

        // Open the saved file and read the serialized StatsManager object back into memory.
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            StatsManager manager = (StatsManager) ois.readObject();
            // Log the successful load before giving the manager back to the app.
            logActivity("StatsManager loaded successfully.");
            return manager;
        } catch (InvalidClassException e) {
            // If the saved object version no longer matches the code, discard old data safely.
            logActivity("Stats data structure changed. Initializing fresh StatsManager. " + e.getMessage());
            return new StatsManager();
        } catch (Throwable e) { // Catch Throwable instead of Exception to handle Errors like
                                // NoClassDefFoundError
            // Any corrupted file or unexpected loading problem also falls back to a fresh manager.
            logActivity("Error loading StatsManager or corrupted file: " + e.getMessage());
            return new StatsManager();
        }
    }

    // Append timestamped log entry for debugging, error tracking, and user activity
    // audit trail
    // Log entries help diagnose application behavior and reconstruct usage history
    public static void logActivity(String message) {
        // Open the log in append mode so older messages are not erased.
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            // Store the current time with the message to make events easier to trace.
            writer.println(System.currentTimeMillis() + " - " + message);
        } catch (IOException e) {
            // If file logging fails, at least show the issue in the console.
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}
