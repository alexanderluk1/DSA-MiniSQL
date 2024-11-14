package edu.smu.smusql.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.*;

public class Table {
    private String tableName;
    private List<String> columns;
    private BPlusTree bPlusTree;
    private int currentKey = 0;

    public Table(String tableName, List<String> columns, int orderNumber) {
        this.tableName = tableName;
        this.columns = columns;
        this.bPlusTree = new BPlusTree(orderNumber);
    }

    public int getCurrentKey() {
        return this.currentKey;
    }

    // Insert a row into the table
    public boolean insertRecord(List<Object> row) {
        if (row.size() != columns.size()) {
            throw new IllegalArgumentException("Row size must match the number of columns.");
        }

        HashMap<String, Object> record = new HashMap<>();
        for (int i = 0; i < columns.size(); i++) {
            record.put(columns.get(i), row.get(i));
        }

        // Insert record with an auto-incrementing key
        bPlusTree.insert(++currentKey, record);
        return true;
    }

    // Search for a row by primary key
    public Map<String, Object> selectRecord(int key) {
        return bPlusTree.search(key);
    }

    public Map<Integer, Map<String, Object>> selectRecords(String condition) {
        Map<Integer, Map<String, Object>> resultRecords = new HashMap<>();
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
            Map<String, Object> record = bPlusTree.search(k);
            if (record != null) {
                if (evaluateCombinedConditions(record, Arrays.asList(conditions), operators)) {
                    resultRecords.put(k, record);
                }
            }
        }

        return resultRecords;
    }

    public List<Map<String, Object>> getRecords() {
        List<Map<String, Object>> records = new ArrayList<>();
        for (int k = 1; k <= currentKey; k++) {
            Map<String, Object> record = bPlusTree.search(k);
            if (record != null) {
                records.add(record);
            }
        }
        return records;
    }


    // Evaluate combined conditions based on AND/OR logic
    private boolean evaluateCombinedConditions(Map<String, Object> record, List<String> conditions, List<String> operators) {
        boolean overallResult = true; // Start with true for AND evaluation
        boolean firstCondition = true;

        for (int i = 0; i < conditions.size(); i++) {
            boolean result = evaluateSingleCondition(record, conditions.get(i));

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
    private boolean evaluateSingleCondition(Map<String, Object> record, String condition) {
        String regex = "(\\w+)\\s*(>=|<=|!=|=|>|<)\\s*(.+)";
        Matcher matcher = Pattern.compile(regex).matcher(condition);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid condition format: " + condition);
        }

        String columnName = matcher.group(1);
        String operator = matcher.group(2);
        String rawValue = matcher.group(3).replace("'", ""); // Remove quotes

        Object recordValue = record.get(columnName);
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


    // Update records based on a condition
    // Update updateRecords to handle complex conditions
    // Return number of rows updated
    public int updateRecords(String condition, HashMap<String, Object> updatedValues) {
        int count = 0;
        // Retrieve records to update based on the condition
        Map<Integer, Map<String, Object>> recordsToUpdate = selectRecords(condition);

        // Iterate over each record to be updated
        for (Map.Entry<Integer, Map<String, Object>> recordEntry : recordsToUpdate.entrySet()) {
            Integer key = recordEntry.getKey(); // Assuming this is the key for BPlusTree
            Map<String, Object> record = recordEntry.getValue();

            // Update the record with new values if the column exists and has changed
            for (String column : updatedValues.keySet()) {
                if (columns.contains(column)) {
                    Object newValue = updatedValues.get(column);
                    Object currentValue = record.get(column);

                    // Use Objects.equals to avoid NullPointerException
                    if (!Objects.equals(currentValue, newValue)) {
                        record.put(column, newValue);
                        count++;
                    }
                }
            }

            // Update the B+ tree with the modified record
            bPlusTree.update(key, record); // Assuming BPlusTree has an update method
        }

        return count;
    }



    // Delete records based on a condition
    // Update deleteRecords to handle complex conditions
    public int deleteRecords(String condition) {
        Map<Integer, Map<String, Object>> recordsToDelete = selectRecords(condition);
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
        return columns.size();
    }
}
