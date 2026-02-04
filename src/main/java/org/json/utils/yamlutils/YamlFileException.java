package org.json.utils.yamlutils;

/**
 * Custom exception for YAML file utility operations.
 */
public class YamlFileException extends Exception {
    public YamlFileException(String message) { super(message); }
    public YamlFileException(String message, Throwable cause) { super(message, cause); }
}
