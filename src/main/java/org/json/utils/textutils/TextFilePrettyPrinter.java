package org.json.utils.textutils;

import java.util.List;

/**
 * Utility for pretty-printing text file content.
 */
public class TextFilePrettyPrinter {
    /**
     * Returns a pretty-printed string from lines (trims and aligns).
     */
    public static String prettyPrint(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line.trim()).append(System.lineSeparator());
        }
        return sb.toString();
    }
}
