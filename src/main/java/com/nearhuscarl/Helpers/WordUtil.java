package com.nearhuscarl.Helpers;

import java.text.Normalizer;

public class WordUtil {
    // change letters like áéőűú to aeouu
    public static String normalizeUnicodeCharacters(String text) {
        return Normalizer
                .normalize(text, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    public static String normalize(String word) {
        return normalizeUnicodeCharacters(word)
                .trim()
                .toLowerCase()
                .replace(' ', '-') // Aaron Copland -> aaron-copland
                .replace("'", ""); // 'Fats' Waller -> fats waller
    }
}
