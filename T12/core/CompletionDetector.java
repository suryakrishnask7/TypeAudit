package T12.core;

public class CompletionDetector {
    public static boolean isComplete(String expected, String typed) {
        if (expected == null || typed == null) return false;
        // Trim whitespace for practical comparison
        return expected.trim().equalsIgnoreCase(typed.trim());
    }
}
