package org.json.utils.csvUtils;

import java.util.*;

public class CsvExample {
    public static void main(String[] args) throws CsvException {
        String path = "test.csv";
        List<String> header = Arrays.asList("id", "name", "email");

        // Create
        CsvUtils.createCsvFile(path, header);

        // Add records
        Map<String, String> rec1 = Map.of("id", "1", "name", "Alice", "email", "alice@example.com");
        Map<String, String> rec2 = Map.of("id", "2", "name", "Bob", "email", "bob@example.com");
        CsvUtils.addRecord(path, rec1);
        CsvUtils.addRecord(path, rec2);

        // Read
        List<Map<String, String>> records = CsvUtils.readCsvFile(path);
        System.out.println("Records: " + records);

        // Update
        CsvUtils.updateRecord(path, r -> r.get("id").equals("2"), Map.of("email", "bob@new.com"));

        // Search
        List<Map<String, String>> found = CsvUtils.searchByColumn(path, "name", "Alice");
        System.out.println("Found: " + found);

        // Pretty print
        System.out.println(CsvUtils.prettyPrint(path));

        // Validate
        System.out.println("Valid: " + CsvUtils.validateCsv(path));

        // Delete record
        CsvUtils.deleteRecord(path, r -> r.get("id").equals("1"));

        // Delete file
        CsvUtils.deleteCsvFile(path);
    }
}
