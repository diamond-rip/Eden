package rip.diamond.practice.util;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class WordUtil {
    
    public static String toCapital(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }

    public static String toCapitalEachWord(String word) {
        List<String> words = new ArrayList<>();
        for (String s : word.split(" ")) {
            words.add(toCapital(s));
        }
        return StringUtils.join(words, " ");
    }

    public static String formatWords(String word) {
        return toCapitalEachWord(word.replace("_", " "));
    }
    
}
