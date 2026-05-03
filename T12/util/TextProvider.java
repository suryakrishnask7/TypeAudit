package T12.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TextProvider {
    
    private static final String CUSTOM_TEXT_FILE = "custom_text.txt";
    private static final String[] PARAGRAPHS = {
        "The quick brown fox jumps over the lazy dog. This sentence contains every letter in the English alphabet, which makes it a perfect pangram.",
        "Java is a high-level, class-based, object-oriented programming language that is designed to have as few implementation dependencies as possible.",
        "To be or not to be, that is the question. Whether 'tis nobler in the mind to suffer the slings and arrows of outrageous fortune, or to take arms against a sea of troubles.",
        "Typing fast and accurately is a great skill. It requires muscle memory, focus, and a lot of practice over an extended period of time.",
        "System, Character, BigInteger, and ArrayList are important classes in Java. Mastery of these components can help build strong and reliable applications."
    };

    public static String getRandomText() {
        int index = (int) (Math.random() * PARAGRAPHS.length);
        return PARAGRAPHS[index];
    }
    
    public static void saveCustomText(String text) throws IOException {
        if (text == null || text.trim().length() < 20) {
            throw new IllegalArgumentException("Custom text must be at least 20 characters long.");
        }
        try (PrintWriter out = new PrintWriter(new FileWriter(CUSTOM_TEXT_FILE))) {
            out.print(text);
        }
    }
    
    public static String loadCustomText() {
        try {
            if (Files.exists(Paths.get(CUSTOM_TEXT_FILE))) {
                return new String(Files.readAllBytes(Paths.get(CUSTOM_TEXT_FILE)));
            }
        } catch (IOException e) {
            FileManager.logActivity("Error loading custom text: " + e.getMessage());
        }
        return "";
    }
}
