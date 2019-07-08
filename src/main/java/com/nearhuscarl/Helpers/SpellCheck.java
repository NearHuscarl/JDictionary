package com.nearhuscarl.Helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class SpellCheck {
    private HashSet<String> vocabulary;
    private final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    private boolean isValidWord(String word) {
        return vocabulary.contains(word);
    }

    public SpellCheck(List<String> wordList) {
        vocabulary = new HashSet<>();
        for (String word: wordList) {
            vocabulary.add(word.toLowerCase());
        }
    }

    private class Split {
        String a;
        String b;

        public Split(String a, String b) {
            this.a = a;
            this.b = b;
        }
    }

    /// Return an IEnumarable of words (valid or not) that are one edit away from the given word
    /// Example: 'strike' would return {'trike', 'tsrike', 'atrike', 'astrike', ...}
    public List<String> edits1(String word)
    {
        List<Split> splits = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            splits.add(new Split(word.substring(0, i), word.substring(i)));
        }

        List<String> variations = new ArrayList<>();

        for (Split s: splits) {
            // delete
            if (!s.b.equals("")) {
                variations.add(s.a + s.b.substring(1));
            }

            // transpose
            if (s.b.length() > 1) {
                variations.add(s.a + s.b.charAt(1) + s.b.charAt(0) + s.b.substring(2));
            }

            for (int i = 0; i < alphabet.length(); i++) {
                char c = alphabet.charAt(i);

                // replace
                if (!s.b.equals("")) {
                    variations.add(s.a + c + s.b.substring(1));
                }

                // insert
                variations.add(s.a + c + s.b);
            }
        }

        return ListUtil.distinct(variations);
    }

    /// Return an IEnumarable of words that are two edits away from the given word
    /// See Edits1(string)
    private List<String> edits2(String word)
    {
        List<String> edits1 = edits1(word);
        List<String> edits2 = new ArrayList<>();

        for (String e1: edits1) {
            edits2.addAll(edits1(e1));
        }

        return ListUtil.distinct(edits2);
    }

    /// return a list of valid words from another wordlist
    private List<String> known(List<String> words)
    {
        List<String> knownWords = new ArrayList<>();

        for (String word: words) {
            if (isValidWord(word)) {
                knownWords.add(word);
            }
        }
        return knownWords;
    }

    /// return a list of candidates for the wrong spelling word
    public List<String> candidates(String word)
    {
        List<String> candidates;

        if (isValidWord(word)) {
            return null;
        }

        candidates = known(edits1(word));
        if (candidates.size() > 0) {
            return candidates;
        }

        candidates = known(edits2(word));
        if (candidates.size() > 0) {
            return candidates;
        }

        return Arrays.asList(word);
    }
}
