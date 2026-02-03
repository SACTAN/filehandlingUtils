package org.json.utils;

import com.example.jsonutils.exceptions.JsonUtilsException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * Utility class for in-memory JSON data operations.
 */
public class JsonDataUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Parses JSON from file into a JsonNode.
     * @param filePath Path to the JSON file
     * @return JsonNode representing the JSON content
     * @throws JsonUtilsException if parsing fails
     */
    public static JsonNode parseJsonFromFile(String filePath) throws JsonUtilsException {
        try {
            return mapper.readTree(new File(filePath));
        } catch (IOException e) {
            throw new JsonUtilsException("Failed to parse JSON from file: " + filePath, e);
        }
    }

    /**
     * Converts an object to JSON and writes to file.
     * @param filePath Path to the file
     * @param obj Object to serialize
     * @throws JsonUtilsException if serialization fails
     */
    public static void objectToJsonFile(String filePath, Object obj) throws JsonUtilsException {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), obj);
        } catch (IOException e) {
            throw new JsonUtilsException("Failed to write object to JSON file: " + filePath, e);
        }
    }

    /**
     * Adds a new record to a JSON array in file.
     * @param filePath Path to the file
     * @param newRecord JsonNode to add
     * @throws JsonUtilsException if operation fails
     */
    public static void addRecord(String filePath, JsonNode newRecord) throws JsonUtilsException {
        JsonNode root = parseJsonFromFile(filePath);
        if (!(root instanceof ArrayNode)) {
            throw new JsonUtilsException("Root is not a JSON array: " + filePath);
        }
        ArrayNode array = (ArrayNode) root;
        array.add(newRecord);
        objectToJsonFile(filePath, array);
    }

    /**
     * Updates a record by condition.
     * @param filePath Path to the file
     * @param condition Predicate to match record
     * @param updatedRecord New record to replace
     * @throws JsonUtilsException if operation fails
     */
    public static void updateRecord(String filePath, Predicate<JsonNode> condition, JsonNode updatedRecord) throws JsonUtilsException {
        JsonNode root = parseJsonFromFile(filePath);
        if (!(root instanceof ArrayNode)) {
            throw new JsonUtilsException("Root is not a JSON array: " + filePath);
        }
        ArrayNode array = (ArrayNode) root;
        boolean updated = false;
        for (int i = 0; i < array.size(); i++) {
            if (condition.test(array.get(i))) {
                array.set(i, updatedRecord);
                updated = true;
            }
        }
        if (!updated) {
            throw new JsonUtilsException("No matching record found to update.");
        }
        objectToJsonFile(filePath, array);
    }

    /**
     * Deletes a record by condition.
     * @param filePath Path to the file
     * @param condition Predicate to match record
     * @throws JsonUtilsException if operation fails
     */
    public static void deleteRecord(String filePath, Predicate<JsonNode> condition) throws JsonUtilsException {
        JsonNode root = parseJsonFromFile(filePath);
        if (!(root instanceof ArrayNode)) {
            throw new JsonUtilsException("Root is not a JSON array: " + filePath);
        }
        ArrayNode array = (ArrayNode) root;
        boolean deleted = false;
        for (Iterator<JsonNode> it = array.iterator(); it.hasNext(); ) {
            JsonNode node = it.next();
            if (condition.test(node)) {
                it.remove();
                deleted = true;
            }
        }
        if (!deleted) {
            throw new JsonUtilsException("No matching record found to delete.");
        }
        objectToJsonFile(filePath, array);
    }

    /**
     * Replaces entire JSON content in file.
     * @param filePath Path to the file
     * @param newContent New JsonNode content
     * @throws JsonUtilsException if operation fails
     */
    public static void replaceJsonContent(String filePath, JsonNode newContent) throws JsonUtilsException {
        objectToJsonFile(filePath, newContent);
    }
}
