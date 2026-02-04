package org.json.utils.csvUtils;

/**
 * Custom exception for CSV utility operations.
 */
public class CsvException extends Exception {
    public CsvException(String message) { super(message); }
    public CsvException(String message, Throwable cause) { super(message, cause); }
}
