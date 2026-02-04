package org.json.utils.textutils;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Utility class for performing file and data operations on text files.
 * Thread-safe for file operations.
 */
public class TextFileUtils {
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Creates a new text file with the given content.
     */
    public static void createTextFile(String path, String content) throws TextFileException {
        lock.writeLock().lock();
        try {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath)) {
                throw new TextFileException("File already exists: " + path);
            }
            Files.writeString(filePath, content == null ? "" : content);
        } catch (IOException e) {
            throw new TextFileException("Failed to create text file", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Reads the entire content of a text file.
     */
    public static String readTextFile(String path) throws TextFileException {
        lock.readLock().lock();
        try {
            return Files.readString(Paths.get(path));
        } catch (IOException e) {
            throw new TextFileException("Failed to read text file", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Writes content to a text file (overwrites).
     */
    public static void writeTextFile(String path, String content) throws TextFileException {
        lock.writeLock().lock();
        try {
            Files.writeString(Paths.get(path), content);
        } catch (IOException e) {
            throw new TextFileException("Failed to write text file", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Appends content to a text file.
     */
    public static void appendToTextFile(String path, String content) throws TextFileException {
        lock.writeLock().lock();
        try {
            Files.writeString(Paths.get(path), content, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new TextFileException("Failed to append to text file", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Deletes a text file.
     */
    public static void deleteTextFile(String path) throws TextFileException {
        lock.writeLock().lock();
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            throw new TextFileException("Failed to delete text file", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Replaces a line matching a predicate with new content.
     */
    public static void replaceLine(String path, Predicate<String> condition, String newLine) throws TextFileException {
        lock.writeLock().lock();
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            List<String> updated = lines.stream().map(line -> condition.test(line) ? newLine : line).collect(Collectors.toList());
            Files.write(Paths.get(path), updated);
        } catch (IOException e) {
            throw new TextFileException("Failed to replace line in text file", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Deletes lines matching a predicate.
     */
    public static void deleteLines(String path, Predicate<String> condition) throws TextFileException {
        lock.writeLock().lock();
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            List<String> updated = lines.stream().filter(line -> !condition.test(line)).collect(Collectors.toList());
            Files.write(Paths.get(path), updated);
        } catch (IOException e) {
            throw new TextFileException("Failed to delete lines in text file", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Searches for lines containing a keyword.
     */
    public static List<String> searchLines(String path, String keyword) throws TextFileException {
        lock.readLock().lock();
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            return lines.stream().filter(line -> line.contains(keyword)).collect(Collectors.toList());
        } catch (IOException e) {
            throw new TextFileException("Failed to search lines in text file", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Gets a specific line by number (0-based).
     */
    public static Optional<String> getLine(String path, int lineNumber) throws TextFileException {
        lock.readLock().lock();
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            if (lineNumber < 0 || lineNumber >= lines.size()) return Optional.empty();
            return Optional.of(lines.get(lineNumber));
        } catch (IOException e) {
            throw new TextFileException("Failed to get line from text file", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Counts the number of lines in the file.
     */
    public static long countLines(String path) throws TextFileException {
        lock.readLock().lock();
        try {
            return Files.lines(Paths.get(path)).count();
        } catch (IOException e) {
            throw new TextFileException("Failed to count lines in text file", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Checks if the file contains a specific string.
     */
    public static boolean contains(String path, String search) throws TextFileException {
        lock.readLock().lock();
        try {
            return Files.readString(Paths.get(path)).contains(search);
        } catch (IOException e) {
            throw new TextFileException("Failed to check content in text file", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Validates if the file is not empty.
     */
    public static boolean validateNotEmpty(String path) throws TextFileException {
        lock.readLock().lock();
        try {
            return Files.size(Paths.get(path)) > 0;
        } catch (IOException e) {
            throw new TextFileException("Failed to validate text file", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Pretty prints the file content (trims and aligns lines).
     */
    public static String prettyPrint(String path) throws TextFileException {
        lock.readLock().lock();
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line.trim()).append(System.lineSeparator());
            }
            return sb.toString();
        } catch (IOException e) {
            throw new TextFileException("Failed to pretty print text file", e);
        } finally {
            lock.readLock().unlock();
        }
    }
}
