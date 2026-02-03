package com.example.jsonutils.exceptions;

/**
 * Custom exception for JSON utility errors.
 */
public class JsonUtilsException extends Exception {
    public JsonUtilsException(String message, Throwable cause) {
        super(message, cause);
    }
    public JsonUtilsException(String message) {
        super(message);
    }
}
