package org.json.utils.textutils;

import java.util.List;

/**
 * Utility for validating text file content.
 */
public class TextFileValidator {
    /**
     * Validates that all lines are non-empty.
     */
    public static boolean allLinesNonEmpty(List<String> lines) {
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
