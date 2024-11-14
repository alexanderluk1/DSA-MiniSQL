package edu.smu.smusql.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.*;

public class Table {
    private String tableName;
    private String[] columns;  // Use array for columns
    private BPlusTree bPlusTree;
    private int currentKey = 0;

    public Table(String tableName, String[] columns, int orderNumber) {
        this.tableName = tableName;
        this.columns = columns;
        this.bPlusTree = new BPlusTree(orderNumber);
    }

    public int getCurrentKey() {
        return this.currentKey;
    }

    // Insert a row into the table
    public boolean insertRecord(Object[] row) {
        if (row.length != columns.length) {
            throw new IllegalArgumentException("Row size must match the number of columns.");
        }

        // Insert record with an auto-incrementing key
        bPlusTree.insert(++currentKey, row);
        return true;
    }

    // Search for a row by primary key
    public Object[] selectRecord(int key) {
        return (Object[]) bPlusTree.search(key);
    }

    // Search for multiple records based on a condition
    public Map<Integer, Object[]> selectRecords(String condition) {
        Map<Integer, Object[]> resultRecords = new HashMap<>();
        String[] conditions = condition.split("\\s+(AND|OR)\\s+");
        List<String> operators = new ArrayList<>();

        // Capture operators
        String[] parts = condition.split("\\s+");
        for (String part : parts) {
            if (part.equals("AND") || part.equals("OR")) {
                operators.add(part);
            }
        }

        for (int k = 1; k <= currentKey; k++) {
            Object[] record = bPlusTree.search(k);
            if (record != null) {
                if (evaluateCombinedConditions(record, conditions, operators)) {
                    resultRecords.put(k, record);
                }
            }
        }

        return resultRecords;
    }

    public List<Object[]> getRecords() {
        List<Object[]> records = new ArrayList<>();
        for (int k = 1; k <= currentKey; k++) {
            Object[] record = (Object[]) bPlusTree.search(k);
            if (record != null) {
                records.add(record);
            }
        }
        return records;
    }

    // Evaluate combined conditions based on AND/OR logic
    private boolean evaluateCombinedConditions(Object[] record, String[] conditions, List<String> operators) {
        boolean overallResult = true; // Start with true for AND evaluation
        boolean firstCondition = true;

        for (int i = 0; i < conditions.length; i++) {
            boolean result = evaluateSingleCondition(record, conditions[i]);

            if (firstCondition) {
                overallResult = result;
                firstCondition = false;
            } else {
                String operator = operators.get(i - 1); // Get the operator before this condition
                if (operator.equals("AND")) {
                    overallResult = overallResult && result;
                } else if (operator.equals("OR")) {
                    overallResult = overallResult || result;
                }
            }
        }
        return overallResult;
    }

    private static Object convertValue(String value, Class<?> targetType) {
        if (targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType == String.class) {
            return value;
        } else {
            throw new IllegalArgumentException("Unsupported target type: " + targetType);
        }
    }

    // Evaluate a single condition
    private boolean evaluateSingleCondition(Object[] record, String condition) {
        String regex = "(\\w+)\\s*(>=|<=|!=|=|>|<)\\s*(.+)";
        Matcher matcher = Pattern.compile(regex).matcher(condition);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid condition format: " + condition);
        }

        String columnName = matcher.group(1);
        String operator = matcher.group(2);
        String rawValue = matcher.group(3).replace("'", ""); // Remove quotes

        // Get the index of the column
        int columnIndex = Arrays.asList(columns).indexOf(columnName);
        if (columnIndex == -1) {
            throw new IllegalArgumentException("Column not found: " + columnName);
        }

        Object recordValue = record[columnIndex];
        Object value = convertValue(rawValue, recordValue.getClass()); // Custom type conversion

        return compare(recordValue, operator, value);
    }

    private boolean compare(Object recordValue, String operator, Object value) {
        if (recordValue == null || value == null) {
            return false; // Handle null values appropriately
        }

        // Ensure both values are of the same type
        if (!recordValue.getClass().equals(value.getClass())) {
            throw new IllegalArgumentException("Mismatched types: " + recordValue.getClass() + " and " + value.getClass());
        }

        if (recordValue instanceof Comparable) {
            Comparable<Object> comparableRecordValue = (Comparable<Object>) recordValue;

            // Debugging the operator and values

            switch (operator) {
                case "=":
                    return Objects.equals(recordValue, value);
                case "!=":
                    return !Objects.equals(recordValue, value);
                case ">":
                    return comparableRecordValue.compareTo(value) > 0;
                case "<":
                    return comparableRecordValue.compareTo(value) < 0;
                case ">=":
                    return comparableRecordValue.compareTo(value) >= 0;
                case "<=":
                    return comparableRecordValue.compareTo(value) <= 0;
                default:
                    throw new IllegalArgumentException("Unknown operator: " + operator);
            }
        } else {
            throw new IllegalArgumentException("recordValue is not comparable: " + recordValue.getClass());
        }
    }

    public int updateRecords(String condition, HashMap<String, Object> updatedValues) {
        int count = 0;

        // Retrieve records to update based on the condition
        Map<Integer, Object[]> recordsToUpdate = selectRecords(condition);

        // Iterate over each record to be updated
        for (Map.Entry<Integer, Object[]> recordEntry : recordsToUpdate.entrySet()) {
            Integer key = recordEntry.getKey(); // Assuming this is the key for BPlusTree
            Object[] record = recordEntry.getValue();

            // Iterate over the columns in the updated values
            for (Map.Entry<String, Object> entry : updatedValues.entrySet()) {
                String columnName = entry.getKey(); // Get column name from updatedValues
                Object newValue = entry.getValue(); // Get new value

                // Get the index of the column in the table
                int columnIndex = Arrays.asList(columns).indexOf(columnName);

                if (columnIndex != -1) {
                    // If the value has changed, update it
                    if (!Objects.equals(record[columnIndex], newValue)) {
                        record[columnIndex] = newValue; // Update record with new value
                        count++; // Increment count for each update
                    }
                } else {
                    System.out.println("Column not found: " + columnName);
                }
            }

            // Update the B+ tree with the modified record
            bPlusTree.update(key, record); // Assuming BPlusTree has an update method
        }

        return count;
    }


    // Delete records based on a condition
    public int deleteRecords(String condition) {
        Map<Integer, Object[]> recordsToDelete = selectRecords(condition);
        for (int key : recordsToDelete.keySet()) {
            bPlusTree.delete(key);
        }

        return recordsToDelete.keySet().size();
    }

    public void displayTableInfo() {
        System.out.println("Table " + tableName);
        bPlusTree.printTree();
    }

    public int getNumberOfColumns() {
        return columns.length; // Adjusted to use array length
    }
}
