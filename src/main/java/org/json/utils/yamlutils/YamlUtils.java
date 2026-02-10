package org.json.utils.yamlutils;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.function.Predicate;

/**
 * Utility class for all YAML file and data operations.
 * Thread-safe for file operations.
 * Requires SnakeYAML dependency.
 */
public class YamlUtils {
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Yaml yaml;
    static {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        yaml = new Yaml(options);
    }

    // --- File Operations ---
    public static void createYamlFile(String path, Object data) throws YamlFileException {
        lock.writeLock().lock();
        try {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath)) {
                throw new YamlFileException("File already exists: " + path);
            }
            try (Writer writer = Files.newBufferedWriter(filePath)) {
                yaml.dump(data, writer);
            }
        } catch (IOException e) {
            throw new YamlFileException("Failed to create YAML file", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static Object readYamlFile(String path) throws YamlFileException {
        lock.readLock().lock();
        try (Reader reader = Files.newBufferedReader(Paths.get(path))) {
            return yaml.load(reader);
        } catch (IOException e) {
            throw new YamlFileException("Failed to read YAML file", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void writeYamlFile(String path, Object data) throws YamlFileException {
        lock.writeLock().lock();
        try (Writer writer = Files.newBufferedWriter(Paths.get(path))) {
            yaml.dump(data, writer);
        } catch (IOException e) {
            throw new YamlFileException("Failed to write YAML file", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void deleteYamlFile(String path) throws YamlFileException {
        lock.writeLock().lock();
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            throw new YamlFileException("Failed to delete YAML file", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void updateYamlFile(String path, java.util.function.Function<Object, Object> updater) throws YamlFileException {
        lock.writeLock().lock();
        try {
            Object data = readYamlFile(path);
            Object updated = updater.apply(data);
            writeYamlFile(path, updated);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // --- Query & Element Operations ---
    public static boolean containsKey(String path, String key) throws YamlFileException {
        Object data = readYamlFile(path);
        if (data instanceof Map) {
            return ((Map<?, ?>) data).containsKey(key);
        }
        return false;
    }

    public static Optional<Object> getValueByKey(String path, String key) throws YamlFileException {
        Object data = readYamlFile(path);
        if (data instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) data;
            return Optional.ofNullable(map.get(key));
        }
        return Optional.empty();
    }

    public static List<Object> searchInList(String path, Predicate<Object> predicate) throws YamlFileException {
        Object data = readYamlFile(path);
        if (data instanceof List) {
            List<?> list = (List<?>) data;
            List<Object> result = new ArrayList<>();
            for (Object item : list) {
                if (predicate.test(item)) {
                    result.add(item);
                }
            }
            return result;
        }
        return Collections.emptyList();
    }

    // --- Pretty Print ---
    public static String prettyPrint(String path) throws YamlFileException {
        Object data = readYamlFile(path);
        return prettyPrint(data);
    }
    public static String prettyPrint(Object data) {
        StringWriter sw = new StringWriter();
        yaml.dump(data, sw);
        return sw.toString();
    }

    // --- Validation ---
    public static boolean validateYaml(String path) throws YamlFileException {
        Object data = readYamlFile(path);
        return data != null;
    }
    public static boolean allKeysNonEmpty(Map<?, ?> map) {
        for (Object key : map.keySet()) {
            if (!(key instanceof String) || ((String) key).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    // --- Exception as static inner class ---
    public static class YamlFileException extends Exception {
        public YamlFileException(String message) { super(message); }
        public YamlFileException(String message, Throwable cause) { super(message, cause); }
    }
}
