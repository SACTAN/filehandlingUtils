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
 * Utility class for performing file and data operations on CSV files.
 * Thread-safe for file operations.
 */
public class CsvFileUtils {
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Creates a new CSV file with the given header.
     * @param path File path
     * @param header List of column names
     * @throws CsvException if file exists or IO error
     */
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

    /**
     * Reads a CSV file into a list of records (maps).
     * @param path File path
     * @return List of records
     * @throws CsvException if file not found or malformed
     */
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

    /**
     * Writes records to a CSV file (overwrites).
     * @param path File path
     * @param header List of column names
     * @param records List of records
     * @throws CsvException on IO error
     */
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

    /**
     * Updates an existing CSV file by applying a transformation to the records.
     * @param path File path
     * @param updater Function to update records
     * @throws CsvException on IO error
     */
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

    /**
     * Deletes a CSV file.
     * @param path File path
     * @throws CsvException on IO error
     */
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

    /**
     * Parses CSV into a list of CsvRecord objects.
     */
    public static List<CsvRecord> parseCsv(String path) throws CsvException {
        List<Map<String, String>> records = readCsvFile(path);
        return records.stream().map(CsvRecord::new).collect(Collectors.toList());
    }

    /**
     * Converts a list of CsvRecord objects to CSV and writes to file.
     */
    public static void persistCsv(String path, List<String> header, List<CsvRecord> records) throws CsvException {
        List<Map<String, String>> maps = records.stream().map(CsvRecord::getFields).collect(Collectors.toList());
        writeCsvFile(path, header, maps);
    }

    /**
     * Adds a new record to the CSV file.
     */
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

    /**
     * Updates a record by key/condition.
     */
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

    /**
     * Deletes a record by key/condition.
     */
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

    /**
     * Replaces entire CSV content.
     */
    public static void replaceContent(String path, List<String> header, List<Map<String, String>> newRecords) throws CsvException {
        writeCsvFile(path, header, newRecords);
    }

    /**
     * Searches records by column and value.
     */
    public static List<Map<String, String>> searchByColumn(String path, String column, String value) throws CsvException {
        return readCsvFile(path).stream()
                .filter(r -> value.equals(r.get(column)))
                .collect(Collectors.toList());
    }

    /**
     * Gets a specific record by key.
     */
    public static Optional<Map<String, String>> getRecordByKey(String path, String keyColumn, String keyValue) throws CsvException {
        return readCsvFile(path).stream()
                .filter(r -> keyValue.equals(r.get(keyColumn)))
                .findFirst();
    }

    /**
     * Filters records based on a condition.
     */
    public static List<Map<String, String>> filterRecords(String path, Predicate<Map<String, String>> condition) throws CsvException {
        return readCsvFile(path).stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    /**
     * Checks if a column exists.
     */
    public static boolean columnExists(String path, String column) throws CsvException {
        List<String> header = getHeader(path);
        return header.contains(column);
    }

    /**
     * Counts records.
     */
    public static long countRecords(String path) throws CsvException {
        return readCsvFile(path).size();
    }

    /**
     * Pretty prints the CSV file.
     */
    public static String prettyPrint(String path) throws CsvException {
        List<Map<String, String>> records = readCsvFile(path);
        List<String> header = getHeader(path);
        return CsvPrettyPrinter.prettyPrint(records, header);
    }

    /**
     * Validates CSV syntax (consistent columns).
     */
    public static boolean validateCsv(String path) throws CsvException {
        List<Map<String, String>> records = readCsvFile(path);
        List<String> header = getHeader(path);
        return CsvValidator.validateConsistentColumns(records, header);
    }

    /**
     * Gets the header (column names) from a CSV file.
     */
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
}
