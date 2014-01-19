package org.aitek.controller.utils;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 12/25/13
 * Time: 10:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringUtils {

    public static final String[] articles = new String[] {"L'", "Il ", "Lo ", "La ", "I ", "Gli ", "Le ", "Un ", "Uno ", "Una ", "The ", "A ", "l'", "il ", "lo ", "la ", "i ", "gli ", "le ", "un ", "uno ", "una ", "the ", "a "};

    public static String removeWords(String value, String words[]) {

        String newValue = new String(value);

        for (String word: words) {
            if (newValue.startsWith(word)) {
                newValue = newValue.replaceFirst(word, "");
            }
        }

        return newValue;
    }
}
