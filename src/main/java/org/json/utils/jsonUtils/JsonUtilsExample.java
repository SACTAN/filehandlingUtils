package org.json.utils.jsonUtils;

// import removed, now using static inner class
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class JsonUtilsExample {
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
            JsonUtils.createJsonFile(filePath, array.toString());

            // 2. Read JSON file
            String content = JsonUtils.readJsonFile(filePath);
            System.out.println("Read file: " + content);

            // 3. Parse JSON from file
            JsonNode root = JsonUtils.parseJsonFromFile(filePath);

            // 4. Add a new record
            ObjectNode user2 = mapper.createObjectNode();
            user2.put("id", 2);
            user2.put("name", "Bob");
            user2.putObject("address").put("city", "Paris");
            JsonUtils.addRecord(filePath, user2);

            // 5. Update a record by id
            // No direct updateRecord in merged class, implement manually if needed

            // 6. Delete a record by id
            // No direct deleteRecord in merged class, implement manually if needed

            // 7. Search by key and value
            JsonNode updatedRoot = JsonUtils.parseJsonFromFile(filePath);
            System.out.println("Search by name: " + JsonUtils.searchByKeyValue(updatedRoot, "name", "Alice Updated"));

            // 8. Search by nested path
            System.out.println("Search by city: " + JsonUtils.searchByNestedPath(updatedRoot, "address.city", "Berlin"));

            // 9. Pretty print
            System.out.println("Pretty print:\n" + JsonUtils.prettyPrint(updatedRoot));

            // 10. Validate JSON
            System.out.println("Is valid JSON: " + JsonUtils.isValidJson(content));

            // 11. Count records
            // No direct countRecords in merged class, implement manually if needed

            // 12. Delete file
            // No direct deleteJsonFile in merged class, use Files.deleteIfExists(Paths.get(filePath));
        } catch (JsonUtilsException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
