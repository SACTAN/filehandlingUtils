package org.json.utils.csvUtils;

import java.util.Map;

/**
 * Represents a generic CSV record as a map of column names to values.
 */
public class CsvRecord {
    private final Map<String, String> fields;

    public CsvRecord(Map<String, String> fields) {
        this.fields = fields;
    }

    public String get(String column) {
        return fields.get(column);
    }

    public void set(String column, String value) {
        fields.put(column, value);
    }

    public Map<String, String> getFields() {
        return fields;
    }
}
