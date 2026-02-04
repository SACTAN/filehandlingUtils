package org.json.utils.csvUtils;

import java.util.*;

public class CsvExample {
    public static void main(String[] args) throws CsvException {
        String path = "test.csv";
        List<String> header = Arrays.asList("id", "name", "email");

        // Create
        CsvFileUtils.createCsvFile(path, header);

        // Add records
        Map<String, String> rec1 = Map.of("id", "1", "name", "Alice", "email", "alice@example.com");
        Map<String, String> rec2 = Map.of("id", "2", "name", "Bob", "email", "bob@example.com");
        CsvFileUtils.addRecord(path, rec1);
        CsvFileUtils.addRecord(path, rec2);

        // Read
        List<Map<String, String>> records = CsvFileUtils.readCsvFile(path);
        System.out.println("Records: " + records);

        // Update
        CsvFileUtils.updateRecord(path, r -> r.get("id").equals("2"), Map.of("email", "bob@new.com"));

        // Search
        List<Map<String, String>> found = CsvFileUtils.searchByColumn(path, "name", "Alice");
        System.out.println("Found: " + found);

        // Pretty print
        System.out.println(CsvFileUtils.prettyPrint(path));

        // Validate
        System.out.println("Valid: " + CsvFileUtils.validateCsv(path));

        // Delete record
        CsvFileUtils.deleteRecord(path, r -> r.get("id").equals("1"));

        // Delete file
        CsvFileUtils.deleteCsvFile(path);
    }
}
