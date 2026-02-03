package org.json.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for JSON validation and formatting.
 */
public class JsonValidationUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Pretty prints JSON content.
     * @param node JsonNode to pretty print
     * @return Pretty-printed JSON string
     */
    public static String prettyPrint(JsonNode node) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (Exception e) {
            return node.toString();
        }
    }

    /**
     * Validates JSON syntax.
     * @param jsonContent JSON string
     * @return true if valid, false otherwise
     */
    public static boolean isValidJson(String jsonContent) {
        try {
            mapper.readTree(jsonContent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if a file is empty or contains malformed JSON.
     * @param filePath Path to the file
     * @return true if empty or malformed, false otherwise
     */
    public static boolean isEmptyOrMalformed(String filePath) {
        try {
            java.io.File file = new java.io.File(filePath);
            if (!file.exists() || file.length() == 0) {
                return true;
            }
            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            return !isValidJson(content);
        } catch (Exception e) {
            return true;
        }
    }
}
