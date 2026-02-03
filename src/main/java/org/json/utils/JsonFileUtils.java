package org.json.utils;

import com.example.jsonutils.exceptions.JsonUtilsException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class for JSON file operations.
 */
public class JsonFileUtils {

    /**
     * Creates a new JSON file with the given content.
     * @param filePath Path to the new file
     * @param jsonContent JSON content as String
     * @throws JsonUtilsException if file exists or IO error occurs
     */
    public static void createJsonFile(String filePath, String jsonContent) throws JsonUtilsException {
        File file = new File(filePath);
        if (file.exists()) {
            throw new JsonUtilsException("File already exists: " + filePath);
        }
        writeJsonFile(filePath, jsonContent);
    }

    /**
     * Reads the content of a JSON file as a String.
     * @param filePath Path to the file
     * @return JSON content as String
     * @throws JsonUtilsException if file not found or IO error occurs
     */
    public static String readJsonFile(String filePath) throws JsonUtilsException {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            throw new JsonUtilsException("Failed to read file: " + filePath, e);
        }
    }

    /**
     * Writes JSON content to a file (overwrites if exists).
     * @param filePath Path to the file
     * @param jsonContent JSON content as String
     * @throws JsonUtilsException if IO error occurs
     */
    public static void writeJsonFile(String filePath, String jsonContent) throws JsonUtilsException {
        try (FileWriter writer = new FileWriter(filePath, false)) {
            writer.write(jsonContent);
        } catch (IOException e) {
            throw new JsonUtilsException("Failed to write file: " + filePath, e);
        }
    }

    /**
     * Updates an existing JSON file with new content.
     * @param filePath Path to the file
     * @param jsonContent New JSON content as String
     * @throws JsonUtilsException if file does not exist or IO error occurs
     */
    public static void updateJsonFile(String filePath, String jsonContent) throws JsonUtilsException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new JsonUtilsException("File does not exist: " + filePath);
        }
        writeJsonFile(filePath, jsonContent);
    }

    /**
     * Deletes a JSON file.
     * @param filePath Path to the file
     * @throws JsonUtilsException if file does not exist or cannot be deleted
     */
    public static void deleteJsonFile(String filePath) throws JsonUtilsException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new JsonUtilsException("File does not exist: " + filePath);
        }
        if (!file.delete()) {
            throw new JsonUtilsException("Failed to delete file: " + filePath);
        }
    }
}
