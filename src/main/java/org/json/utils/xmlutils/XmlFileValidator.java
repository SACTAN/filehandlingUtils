package com.example.xmlutils;

import org.w3c.dom.Element;

/**
 * Utility for validating XML file content.
 */
public class XmlFileValidator {
    /**
     * Validates that an element has a non-empty tag name.
     */
    public static boolean hasNonEmptyTagName(Element element) {
        return element != null && element.getTagName() != null && !element.getTagName().trim().isEmpty();
    }
}
