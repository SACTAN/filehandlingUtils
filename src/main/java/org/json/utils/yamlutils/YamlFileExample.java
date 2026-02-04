package org.json.utils.yamlutils;

import java.util.*;

public class YamlFileExample {
    public static void main(String[] args) throws YamlFileException {
        String path = "test.yaml";
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", 1);
        data.put("name", "Alice");
        data.put("email", "alice@example.com");

        // Create
        YamlFileUtils.createYamlFile(path, data);

        // Read
        Object read = YamlFileUtils.readYamlFile(path);
        System.out.println("Read: " + read);

        // Update
        YamlFileUtils.updateYamlFile(path, obj -> {
            if (obj instanceof Map) {
                ((Map<String, Object>) obj).put("email", "alice@new.com");
            }
            return obj;
        });

        // Contains key
        System.out.println("Contains 'name': " + YamlFileUtils.containsKey(path, "name"));

        // Get value by key
        System.out.println("Value for 'email': " + YamlFileUtils.getValueByKey(path, "email").orElse("<none>"));

        // Pretty print
        System.out.println(YamlFileUtils.prettyPrint(path));

        // Validate
        System.out.println("Valid: " + YamlFileUtils.validateYaml(path));

        // Delete file
        YamlFileUtils.deleteYamlFile(path);
    }
}
