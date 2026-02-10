package org.json.utils.jsonUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;

/**
 * Utility class for all JSON file and data operations.
 */
public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    // --- File Operations ---
    public static void createJsonFile(String filePath, String jsonContent) throws JsonUtilsException {
        File file = new File(filePath);
        if (file.exists()) {
            throw new JsonUtilsException("File already exists: " + filePath);
        }
        writeJsonFile(filePath, jsonContent);
    }

    public static String readJsonFile(String filePath) throws JsonUtilsException {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            throw new JsonUtilsException("Failed to read file: " + filePath, e);
        }
    }

    public static void writeJsonFile(String filePath, String jsonContent) throws JsonUtilsException {
        try (FileWriter writer = new FileWriter(filePath, false)) {
            writer.write(jsonContent);
        } catch (IOException e) {
            throw new JsonUtilsException("Failed to write file: " + filePath, e);
        }
    }

    public static void updateJsonFile(String filePath, String jsonContent) throws JsonUtilsException {
        writeJsonFile(filePath, jsonContent);
    }

    // --- In-memory Data Operations ---
    public static JsonNode parseJsonFromFile(String filePath) throws JsonUtilsException {
        try {
            return mapper.readTree(new File(filePath));
        } catch (IOException e) {
            throw new JsonUtilsException("Failed to parse JSON from file: " + filePath, e);
        }
    }

    public static void objectToJsonFile(String filePath, Object obj) throws JsonUtilsException {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), obj);
        } catch (IOException e) {
            throw new JsonUtilsException("Failed to write object to JSON file: " + filePath, e);
        }
    }

    public static void addRecord(String filePath, JsonNode newRecord) throws JsonUtilsException {
        JsonNode root = parseJsonFromFile(filePath);
        if (!(root instanceof ArrayNode)) {
            throw new JsonUtilsException("Root is not a JSON array: " + filePath);
        }
        ArrayNode array = (ArrayNode) root;
        array.add(newRecord);
        objectToJsonFile(filePath, array);
    }

    // --- Query Operations ---
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

    // --- Validation & Formatting ---
    public static String prettyPrint(JsonNode node) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (Exception e) {
            return node.toString();
        }
    }

    public static boolean isValidJson(String jsonContent) {
        try {
            mapper.readTree(jsonContent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isEmptyOrMalformed(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists() || file.length() == 0) {
                return true;
            }
            String content = new String(Files.readAllBytes(file.toPath()));
            return !isValidJson(content);
        } catch (Exception e) {
            return true;
        }
    }

    // --- Exception as static inner class ---
    public static class JsonUtilsException extends Exception {
        public JsonUtilsException(String message, Throwable cause) { super(message, cause); }
        public JsonUtilsException(String message) { super(message); }
    }
}
