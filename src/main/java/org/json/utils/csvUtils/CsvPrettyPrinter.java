package org.json.utils.csvUtils;

import java.util.List;
import java.util.Map;

/**
 * Utility for pretty-printing CSV data.
 */
public class CsvPrettyPrinter {
    /**
     * Returns a pretty-printed CSV string from records and header.
     * @param records List of records (maps)
     * @param header List of column names
     * @return Well-formatted CSV string
     */
    public static String prettyPrint(List<Map<String, String>> records, List<String> header) {
        StringBuilder sb = new StringBuilder();
        // Print header
        sb.append(String.join(", ", header)).append("\n");
        // Print rows
        for (Map<String, String> record : records) {
            for (int i = 0; i < header.size(); i++) {
                sb.append(record.getOrDefault(header.get(i), ""));
                if (i < header.size() - 1) sb.append(", ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
