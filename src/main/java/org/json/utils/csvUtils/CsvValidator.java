package org.json.utils.csvUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for validating CSV data.
 */
public class CsvValidator {
    /**
     * Validates that all records have the same columns as the header.
     * @param records List of records (maps)
     * @param header List of column names
     * @return true if all records have the same columns as header
     */
    public static boolean validateConsistentColumns(List<Map<String, String>> records, List<String> header) {
        Set<String> headerSet = Set.copyOf(header);
        for (Map<String, String> record : records) {
            if (!record.keySet().equals(headerSet)) {
                return false;
            }
        }
        return true;
    }
}
