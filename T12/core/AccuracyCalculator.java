package T12.core;

public class AccuracyCalculator {

    // Tracks overall correct characters
    public int getCorrectCharactersCount(String expected, String typed) {
        if (expected == null || typed == null) return 0;
        
        int correct = 0;
        int minLength = Math.min(expected.length(), typed.length());

        for (int i = 0; i < minLength; i++) {
            char expectedChar = expected.charAt(i);
            char typedChar = typed.charAt(i);

            // Case-insensitive comparison
            if (Character.toLowerCase(expectedChar) == Character.toLowerCase(typedChar)) {
                correct++;
            }
        }
        return correct;
    }

    // Computes incorrect characters
    public int getIncorrectCharactersCount(String expected, String typed) {
        if (expected == null || typed == null) return 0;
        int correct = getCorrectCharactersCount(expected, typed);
        return typed.length() - correct;
    }

    // Calculates real-time accuracy based on total characters typed
    public double calculateLiveAccuracy(String expected, String typed) {
        if (expected == null || expected.isEmpty()) return 0.0;
        if (typed == null || typed.isEmpty()) return 0.0;

        int totalTyped = typed.length();
        if (totalTyped == 0) return 0.0;
        
        int correct = getCorrectCharactersCount(expected, typed);
        return (double) correct / totalTyped * 100.0;
    }
    
    public double calculateFinalAccuracy(String expected, String typed) {
        if (expected == null || expected.isEmpty()) return 0.0;
        if (typed == null || typed.isEmpty()) return 0.0;
        
        int correct = getCorrectCharactersCount(expected, typed);
        return (double) correct / expected.length() * 100.0;
    }
}
