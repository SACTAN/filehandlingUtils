package org.json.utils.textutils;

import java.util.*;

public class TextFileExample {
    public static void main(String[] args) throws TextFileException {
        String path = "test.txt";
        String content = "Hello World\nThis is a test file.\nAnother line.";

        // Create
        TextFileUtils.createTextFile(path, content);

        // Read
        String read = TextFileUtils.readTextFile(path);
        System.out.println("Read:\n" + read);

        // Append
        TextFileUtils.appendToTextFile(path, "\nAppended line.");

        // Replace line
        TextFileUtils.replaceLine(path, l -> l.contains("test"), "This is a replaced line.");

        // Delete lines containing 'Another'
        TextFileUtils.deleteLines(path, l -> l.contains("Another"));

        // Search
        List<String> found = TextFileUtils.searchLines(path, "Hello");
        System.out.println("Found: " + found);

        // Get line
        Optional<String> line = TextFileUtils.getLine(path, 0);
        System.out.println("First line: " + line.orElse("<none>"));

        // Count lines
        System.out.println("Line count: " + TextFileUtils.countLines(path));

        // Contains
        System.out.println("Contains 'World': " + TextFileUtils.contains(path, "World"));

        // Validate not empty
        System.out.println("Not empty: " + TextFileUtils.validateNotEmpty(path));

        // Pretty print
        System.out.println(TextFileUtils.prettyPrint(path));

        // Delete file
        TextFileUtils.deleteTextFile(path);
    }
}
