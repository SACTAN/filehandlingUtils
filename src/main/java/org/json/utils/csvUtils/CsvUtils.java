package org.json.utils.csvUtils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Utility class for performing all CSV file and data operations.
 * Thread-safe for file operations.
 */
public class CsvUtils {
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    // --- File Operations ---
    public static void createCsvFile(String path, List<String> header) throws CsvException {
        lock.writeLock().lock();
        try {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath)) {
                throw new CsvException("File already exists: " + path);
            }
            try (CSVWriter writer = new CSVWriter(new FileWriter(path))) {
                writer.writeNext(header.toArray(new String[0]));
            }
        } catch (IOException e) {
            throw new CsvException("Failed to create CSV file", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static List<Map<String, String>> readCsvFile(String path) throws CsvException {
        lock.readLock().lock();
        try (CSVReader reader = new CSVReader(new FileReader(path))) {
            List<String[]> allRows = reader.readAll();
            if (allRows.isEmpty()) return Collections.emptyList();
            String[] header = allRows.get(0);
            List<Map<String, String>> records = new ArrayList<>();
            for (int i = 1; i < allRows.size(); i++) {
                String[] row = allRows.get(i);
                Map<String, String> record = new LinkedHashMap<>();
                for (int j = 0; j < header.length; j++) {
                    record.put(header[j], j < row.length ? row[j] : "");
                }
                records.add(record);
            }
            return records;
        } catch (IOException | com.opencsv.exceptions.CsvException e) {
            throw new CsvException("Failed to read CSV file", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void writeCsvFile(String path, List<String> header, List<Map<String, String>> records) throws CsvException {
        lock.writeLock().lock();
        try (CSVWriter writer = new CSVWriter(new FileWriter(path))) {
            writer.writeNext(header.toArray(new String[0]));
            for (Map<String, String> record : records) {
                String[] row = header.stream().map(col -> record.getOrDefault(col, "")).toArray(String[]::new);
                writer.writeNext(row);
            }
        } catch (IOException e) {
            throw new CsvException("Failed to write CSV file", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void updateCsvFile(String path, java.util.function.Function<List<Map<String, String>>, List<Map<String, String>>> updater) throws CsvException {
        lock.writeLock().lock();
        try {
            List<Map<String, String>> records = readCsvFile(path);
            List<String> header = getHeader(path);
            List<Map<String, String>> updated = updater.apply(records);
            writeCsvFile(path, header, updated);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void deleteCsvFile(String path) throws CsvException {
        lock.writeLock().lock();
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            throw new CsvException("Failed to delete CSV file", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // --- Record Operations ---
    public static List<CsvRecord> parseCsv(String path) throws CsvException {
        List<Map<String, String>> records = readCsvFile(path);
        return records.stream().map(CsvRecord::new).collect(Collectors.toList());
    }

    public static void persistCsv(String path, List<String> header, List<CsvRecord> records) throws CsvException {
        List<Map<String, String>> maps = records.stream().map(CsvRecord::getFields).collect(Collectors.toList());
        writeCsvFile(path, header, maps);
    }

    public static void addRecord(String path, Map<String, String> newRecord) throws CsvException {
        lock.writeLock().lock();
        try {
            List<Map<String, String>> records = readCsvFile(path);
            List<String> header = getHeader(path);
            records.add(newRecord);
            writeCsvFile(path, header, records);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void updateRecord(String path, Predicate<Map<String, String>> condition, Map<String, String> updatedFields) throws CsvException {
        lock.writeLock().lock();
        try {
            List<Map<String, String>> records = readCsvFile(path);
            for (Map<String, String> record : records) {
                if (condition.test(record)) {
                    record.putAll(updatedFields);
                }
            }
            List<String> header = getHeader(path);
            writeCsvFile(path, header, records);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void deleteRecord(String path, Predicate<Map<String, String>> condition) throws CsvException {
        lock.writeLock().lock();
        try {
            List<Map<String, String>> records = readCsvFile(path);
            List<Map<String, String>> filtered = records.stream().filter(r -> !condition.test(r)).collect(Collectors.toList());
            List<String> header = getHeader(path);
            writeCsvFile(path, header, filtered);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void replaceContent(String path, List<String> header, List<Map<String, String>> newRecords) throws CsvException {
        writeCsvFile(path, header, newRecords);
    }

    public static List<Map<String, String>> searchByColumn(String path, String column, String value) throws CsvException {
        return readCsvFile(path).stream()
                .filter(r -> value.equals(r.get(column)))
                .collect(Collectors.toList());
    }

    public static Optional<Map<String, String>> getRecordByKey(String path, String keyColumn, String keyValue) throws CsvException {
        return readCsvFile(path).stream()
                .filter(r -> keyValue.equals(r.get(keyColumn)))
                .findFirst();
    }

    public static List<Map<String, String>> filterRecords(String path, Predicate<Map<String, String>> condition) throws CsvException {
        return readCsvFile(path).stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    public static boolean columnExists(String path, String column) throws CsvException {
        List<String> header = getHeader(path);
        return header.contains(column);
    }

    public static long countRecords(String path) throws CsvException {
        return readCsvFile(path).size();
    }

    // --- Pretty Print ---
    public static String prettyPrint(String path) throws CsvException {
        List<Map<String, String>> records = readCsvFile(path);
        List<String> header = getHeader(path);
        return prettyPrint(records, header);
    }

    public static String prettyPrint(List<Map<String, String>> records, List<String> header) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(", ", header)).append("\n");
        for (Map<String, String> record : records) {
            for (int i = 0; i < header.size(); i++) {
                sb.append(record.getOrDefault(header.get(i), ""));
                if (i < header.size() - 1) sb.append(", ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // --- Validation ---
    public static boolean validateCsv(String path) throws CsvException {
        List<Map<String, String>> records = readCsvFile(path);
        List<String> header = getHeader(path);
        return validateConsistentColumns(records, header);
    }

    public static boolean validateConsistentColumns(List<Map<String, String>> records, List<String> header) {
        Set<String> headerSet = Set.copyOf(header);
        for (Map<String, String> record : records) {
            if (!record.keySet().equals(headerSet)) {
                return false;
            }
        }
        return true;
    }

    // --- Header ---
    public static List<String> getHeader(String path) throws CsvException {
        lock.readLock().lock();
        try (CSVReader reader = new CSVReader(new FileReader(path))) {
            String[] header = reader.readNext();
            if (header == null) throw new CsvException("Empty CSV file: " + path);
            return Arrays.asList(header);
        } catch (IOException | com.opencsv.exceptions.CsvException e) {
            throw new CsvException("Failed to read CSV header", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    // --- CsvRecord as static inner class ---
    public static class CsvRecord {
        private final Map<String, String> fields;
        public CsvRecord(Map<String, String> fields) {
            this.fields = fields;
        }
        public String get(String column) {
            return fields.get(column);
        }
        public void set(String column, String value) {
            fields.put(column, value);
        }
        public Map<String, String> getFields() {
            return fields;
        }
    }

    // --- CsvException as static inner class ---
    public static class CsvException extends Exception {
        public CsvException(String message) { super(message); }
        public CsvException(String message, Throwable cause) { super(message, cause); }
    }
}
