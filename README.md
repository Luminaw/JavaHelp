# Java Help

A collection of utility functions and classes designed to assist with common tasks in Java programming, particularly for laboratory exercises.

## Table of Contents
- [Java Help](#java-help)
  - [Table of Contents](#table-of-contents)
  - [Features](#features)
  - [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
  - [API Usage](#api-usage)
    - [CSVParser](#csvparser)
  - [Technical Details](#technical-details)
    - [Error Handling](#error-handling)
    - [Performance](#performance)
    - [Best Practices](#best-practices)
  - [Known Limitations](#known-limitations)
  - [Future Enhancements](#future-enhancements)
  - [Contributing](#contributing)
  - [License](#license)

## Features

- CSV Parsing: Robust CSV file parsing with proper handling of quoted strings and escaped characters
- Data Validation: Basic data validation utilities
- Error Handling: Comprehensive error handling and meaningful error messages
- New: Enhanced performance optimizations

## Getting Started

### Prerequisites
- Java 8 or higher
- Maven 3.6.0 or higher (for dependency management)
- Git for version control

## API Usage

### CSVParser

```java
import com.java.help.CSVParser;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Example usage demonstrating full functionality
try {
    // Parse the CSV file
    List<String[]> parsedData = new CSVParser().parse("data.csv");
    
    // Convert to string maps using headers
    String[] headers = parsedData.get(0);
    List<Map<String, String>> dataMaps = new CSVParser().convertToStringMap(parsedData, headers);
    
    // Example: Filter and modify data
    List<Map<String, String>> filteredData = dataMaps.stream()
        .filter(map -> map.get("age") != null && Integer.parseInt(map.get("age")) > 18)
        .peek(map -> map.put("status", "active"))
        .collect(Collectors.toList());
    
    // Convert back to string arrays for writing
    List<String[]> modifiedData = filteredData.stream()
        .map(map -> headers.stream()
            .map(header -> map.getOrDefault(header, ""))
            .toArray(String[]::new))
        .collect(Collectors.toList());
    
    // Write the modified data back to a new CSV file
    new CSVParser().writeCSV(modifiedData, "modified_data.csv");
} catch (CSVParserException e) {
    System.err.println("Error parsing or writing CSV: " + e.getMessage());
} catch (InvalidDataException e) {
    System.err.println("Data validation error: " + e.getMessage());
}
```

## Technical Details

### Error Handling
- Custom exceptions for specific error cases
- Detailed error messages with context
- Proper resource management using try-with-resources

### Performance
- Efficient parsing using buffered streams
- Lazy evaluation of data rows
- Minimal memory footprint
- New: Multi-threaded processing for large datasets

### Best Practices
- Follows Java Naming Conventions
- Proper Javadoc comments
- Unit tests for all public methods
- Maven project structure
- Integration tests for edge cases

## Known Limitations
- Currently supports only basic CSV features
- No support for different encodings
- Limited to simple data validation rules
- No support for nested data structures

## Future Enhancements
- [ ] Support for different CSV formats and encodings
- [ ] Additional data validation rules
- [ ] Integration with other data formats (JSON, XML)
- [ ] Support for nested data structures
- [ ] Enhanced error recovery mechanisms

## Contributing
1. Fork the repository
2. Create a feature branch
3. Commit changes
4. Push to the branch
5. Create a Pull Request

## License
Distributed under the MIT License. See LICENSE for more information.
