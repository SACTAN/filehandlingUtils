package org.json.utils.xmlutils;

/**
 * Custom exception for XML file utility operations.
 */
public class XmlFileException extends Exception {
    public XmlFileException(String message) { super(message); }
    public XmlFileException(String message, Throwable cause) { super(message, cause); }
}
