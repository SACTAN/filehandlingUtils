package org.json.utils.textutils;

/**
 * Custom exception for text file utility operations.
 */
public class TextFileException extends Exception {
    public TextFileException(String message) { super(message); }
    public TextFileException(String message, Throwable cause) { super(message, cause); }
}
