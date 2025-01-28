// by Luminaw
// A utility class for parsing and writing CSV files.
// Provides functionality for handling headers, type conversion, and enhanced validation.

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * A utility class for parsing and writing CSV files.
 * Provides functionality for handling headers, type conversion, and enhanced validation.
 */
public class CSVParser {
    private final String delimiter;
    private final char quoteChar;

    /**
     * Constructs a CSVParser with default delimiter (",") and quote character ("").
     */
    public CSVParser() {
        this(",", '"');
    }

    /**
     * Constructs a CSVParser with specified delimiter and quote character.
     * @param delimiter The delimiter to use
     * @param quoteChar The quote character to use
     */
    public CSVParser(String delimiter, char quoteChar) {
        this.delimiter = delimiter;
        this.quoteChar = quoteChar;
    }

    /**
     * Parses a CSV file into a list of string arrays.
     * @param filePath Path to the CSV file
     * @return List of string arrays representing the CSV data
     * @throws CSVParserException If an error occurs during parsing
     */
    public List<String[]> parse(String filePath) throws CSVParserException {
        List<String[]> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Skip empty lines
                String[] row = parseLine(line);
                if (row.length == 0) continue; // Skip empty rows
                if (firstLine && isHeaderRow(row)) {
                    // Handle header if needed
                    records.add(row);
                    firstLine = false;
                } else {
                    try {
                        validateRow(row);
                        records.add(row);
                    } catch (InvalidDataException e) {
                        throw new CSVParserException("Invalid data in row: " + e.getMessage(), e);
                    }
                }
            }
            if (records.isEmpty()) {
                throw new CSVParserException("The file is empty or contains no valid data");
            }
        } catch (IOException e) {
            throw new CSVParserException("Error reading file: " + e.getMessage(), e);
        }
        return records;
    }

    /**
     * Parses a single CSV line into an array of strings.
     * @param line The CSV line to parse
     * @return Array of strings representing the parsed line
     */
    public String[] parseLine(String line) {
        if (line == null || line.isEmpty()) {
            return new String[0];
        }

        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == quoteChar) {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == quoteChar) {
                    sb.append(c);
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == delimiter.charAt(0) && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }

        tokens.add(sb.toString().trim());
        return tokens.toArray(new String[0]);
    }

    /**
     * Writes CSV data to a file.
     * @param data The data to write
     * @param filePath Path to the output file
     * @throws CSVParserException If an error occurs during writing
     */
    public void writeCSV(List<String[]> data, String filePath) throws CSVParserException {
        if (data == null || data.isEmpty()) {
            throw new CSVParserException("Data cannot be null or empty");
        }

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"))) {
            for (String[] row : data) {
                if (row == null || row.length == 0) {
                    throw new CSVParserException("Invalid row in data");
                }
                StringBuilder line = new StringBuilder();
                for (int i = 0; i < row.length; i++) {
                    if (row[i].contains(delimiter) || row[i].contains(String.valueOf(quoteChar))) {
                        line.append(quoteChar).append(row[i].replace(String.valueOf(quoteChar),
                                String.valueOf(quoteChar) + String.valueOf(quoteChar))).append(quoteChar);
                    } else {
                        line.append(row[i]);
                    }
                    if (i < row.length - 1) {
                        line.append(delimiter);
                    }
                }
                bw.write(line.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new CSVParserException("Error writing to file: " + e.getMessage(), e);
        }
    }

    /**
     * Converts a list of string arrays into a list of maps with header-based keys.
     * @param data The list of string arrays
     * @param headers The header row containing column names
     * @return List of maps where each map represents a row with column names as keys
     * @throws InvalidDataException If conversion fails
     */
    public List<Map<String, String>> convertToStringMap(List<String[]> data, String[] headers) throws InvalidDataException {
        if (data == null || data.isEmpty() || headers == null || headers.length == 0) {
            throw new InvalidDataException("Data or headers cannot be null or empty");
        }

        List<Map<String, String>> result = new ArrayList<>();
        for (String[] row : data) {
            if (row.length != headers.length) {
                throw new InvalidDataException("Row does not match header length");
            }
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                map.put(headers[i], row[i]);
            }
            result.add(map);
        }
        return result;
    }

    /**
     * Checks if a row is a header row based on its content.
     * @param row The row to check
     * @return True if the row appears to be a header row
     */
    protected boolean isHeaderRow(String[] row) {
        // Implement your header detection logic here
        // For example, check if all elements are alphabetic
        for (String field : row) {
            if (!field.matches("[a-zA-Z]+")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates a row against expected criteria.
     * @param row The row to validate
     * @throws InvalidDataException If validation fails
     */
    protected void validateRow(String[] row) throws InvalidDataException {
        if (row == null || row.length == 0) {
            throw new InvalidDataException("Empty row found");
        }
        // Add additional validation logic as needed
    }

    /**
     * Exception class for CSV parsing errors.
     */
    public static class CSVParserException extends Exception {
        public CSVParserException(String message) {
            super(message);
        }

        public CSVParserException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Exception class for data validation errors.
     */
    public static class InvalidDataException extends Exception {
        public InvalidDataException(String message) {
            super(message);
        }
    }
}