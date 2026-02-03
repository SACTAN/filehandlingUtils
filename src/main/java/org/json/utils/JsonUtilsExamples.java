package org.json.utils;

import com.example.jsonutils.exceptions.JsonUtilsException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class JsonUtilsExamples {
    public static void main(String[] args) {
        try {
            // Sample JSON array file path
            String filePath = "sample.json";
            ObjectMapper mapper = new ObjectMapper();

            // 1. Create a new JSON file
            ArrayNode array = mapper.createArrayNode();
            ObjectNode user1 = mapper.createObjectNode();
            user1.put("id", 1);
            user1.put("name", "Alice");
            user1.putObject("address").put("city", "London");
            array.add(user1);
            JsonFileUtils.createJsonFile(filePath, array.toString());

            // 2. Read JSON file
            String content = JsonFileUtils.readJsonFile(filePath);
            System.out.println("Read file: " + content);

            // 3. Parse JSON from file
            JsonNode root = JsonDataUtils.parseJsonFromFile(filePath);

            // 4. Add a new record
            ObjectNode user2 = mapper.createObjectNode();
            user2.put("id", 2);
            user2.put("name", "Bob");
            user2.putObject("address").put("city", "Paris");
            JsonDataUtils.addRecord(filePath, user2);

            // 5. Update a record by id
            JsonDataUtils.updateRecord(filePath, node -> node.has("id") && node.get("id").asInt() == 1,
                    mapper.createObjectNode().put("id", 1).put("name", "Alice Updated").putObject("address").put("city", "Berlin"));

            // 6. Delete a record by id
            JsonDataUtils.deleteRecord(filePath, node -> node.has("id") && node.get("id").asInt() == 2);

            // 7. Search by key and value
            JsonNode updatedRoot = JsonDataUtils.parseJsonFromFile(filePath);
            System.out.println("Search by name: " + JsonQueryUtils.searchByKeyValue(updatedRoot, "name", "Alice Updated"));

            // 8. Search by nested path
            System.out.println("Search by city: " + JsonQueryUtils.searchByNestedPath(updatedRoot, "address.city", "Berlin"));

            // 9. Pretty print
            System.out.println("Pretty print:\n" + JsonValidationUtils.prettyPrint(updatedRoot));

            // 10. Validate JSON
            System.out.println("Is valid JSON: " + JsonValidationUtils.isValidJson(content));

            // 11. Count records
            System.out.println("Count: " + JsonQueryUtils.countRecords(updatedRoot));

            // 12. Delete file
            JsonFileUtils.deleteJsonFile(filePath);

        } catch (JsonUtilsException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
