package org.json.utils;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Utility class for querying and searching JSON data.
 */
public class JsonQueryUtils {

    /**
     * Searches records by key and value in a JSON array.
     * @param root Root JsonNode (should be ArrayNode)
     * @param key Key to search
     * @param value Value to match
     * @return List of matching JsonNodes
     */
    public static List<JsonNode> searchByKeyValue(JsonNode root, String key, String value) {
        List<JsonNode> result = new ArrayList<>();
        if (root.isArray()) {
            for (JsonNode node : root) {
                if (node.has(key) && value.equals(node.get(key).asText())) {
                    result.add(node);
                }
            }
        }
        return result;
    }

    /**
     * Searches using nested paths (e.g., "user.address.city").
     * @param root Root JsonNode (should be ArrayNode)
     * @param path Dot-separated path
     * @param value Value to match
     * @return List of matching JsonNodes
     */
    public static List<JsonNode> searchByNestedPath(JsonNode root, String path, String value) {
        List<JsonNode> result = new ArrayList<>();
        String[] keys = path.split("\\.");
        if (root.isArray()) {
            for (JsonNode node : root) {
                JsonNode current = node;
                for (String key : keys) {
                    if (current.has(key)) {
                        current = current.get(key);
                    } else {
                        current = null;
                        break;
                    }
                }
                if (current != null && value.equals(current.asText())) {
                    result.add(node);
                }
            }
        }
        return result;
    }

    /**
     * Gets a specific record by key and value.
     * @param root Root JsonNode (should be ArrayNode)
     * @param key Key to search
     * @param value Value to match
     * @return First matching JsonNode or null
     */
    public static JsonNode getRecordByKey(JsonNode root, String key, String value) {
        if (root.isArray()) {
            for (JsonNode node : root) {
                if (node.has(key) && value.equals(node.get(key).asText())) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * Filters records based on a condition.
     * @param root Root JsonNode (should be ArrayNode)
     * @param condition Predicate to match
     * @return List of matching JsonNodes
     */
    public static List<JsonNode> filterRecords(JsonNode root, Predicate<JsonNode> condition) {
        List<JsonNode> result = new ArrayList<>();
        if (root.isArray()) {
            for (JsonNode node : root) {
                if (condition.test(node)) {
                    result.add(node);
                }
            }
        }
        return result;
    }

    /**
     * Checks if a key exists in the JSON (at any level in array elements).
     * @param root Root JsonNode (should be ArrayNode)
     * @param key Key to check
     * @return true if key exists in any element
     */
    public static boolean keyExists(JsonNode root, String key) {
        if (root.isArray()) {
            for (JsonNode node : root) {
                if (node.has(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Counts records in a JSON array.
     * @param root Root JsonNode (should be ArrayNode)
     * @return Number of records
     */
    public static int countRecords(JsonNode root) {
        return root.isArray() ? root.size() : 0;
    }
}
