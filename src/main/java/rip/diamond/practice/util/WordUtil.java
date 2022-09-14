package rip.diamond.practice.util;

public class WordUtil {
    
    public static String toCapital(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
    
}
