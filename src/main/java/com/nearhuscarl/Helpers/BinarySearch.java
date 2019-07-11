package com.nearhuscarl.Helpers;

import java.util.ArrayList;

public class BinarySearch {
    public static int prefix(String element, ArrayList<String> sequence) {
        return prefix(element, sequence, true);
    }

    private static int prefix(String element, ArrayList<String> sequence, boolean recursive) {
        int minPos = 0;
        int maxPos = sequence.size() - 1;
        int prefixPos = -1;

        while (true) {
            if (maxPos < minPos) {
                if (prefixPos != -1)
                    return prefixPos;

                if (recursive) {
                    for(int i: new Range(0, element.length() + 1))
                    {
                        int pos = prefix(element.substring(0, i), sequence, false);

                        if (pos != -1)
                            prefixPos = pos;
                        else
                            return prefixPos;

                        //return -1;
                    }
                }

                return -1;
            }

            int curPos = (int) ((minPos + maxPos) / 2);
            String currentWord = sequence.get(curPos).toLowerCase();

            if (currentWord.startsWith(element))
                prefixPos = curPos;

            if (currentWord.compareTo(element) < 0)
                minPos = curPos + 1;

            else if (currentWord.compareTo(element) > 0)
                maxPos = curPos - 1;

            else // sequence.get(curPos) == element
                return curPos;
        }
    }
}
