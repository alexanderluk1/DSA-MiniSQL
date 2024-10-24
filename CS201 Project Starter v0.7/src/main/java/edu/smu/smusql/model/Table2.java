package edu.smu.smusql.model;

import edu.smu.smusql.ErrorChecks.TypeConverter;
import edu.smu.smusql.Parser;
import java.util.regex.Pattern;

import java.util.*;

public class Table2 {
    private String tableName;
    private List<String> columns;
    private BPlusTree<Integer, Map<String, Object>> bPlusTree;
    private int currentKey = 0;

    public Table2(String tableName, List<String> columns, int orderNumber) {
        this.tableName = tableName;
        this.columns = columns;
        this.bPlusTree = new BPlusTree<>(orderNumber);
    }

    public int getCurrentKey() {
        return this.currentKey;
    }

    // Insert a row into the table
    public boolean insertRecord(List<Object> row) {
        if (row.size() != columns.size()) {
            throw new IllegalArgumentException("Row size must match the number of columns.");
        }

        Map<String, Object> record = new HashMap<>();
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

    public List<Integer> selectRecords(String condition) {
        List<Integer> resultKeys = new ArrayList<>();
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
            if (evaluateCombinedConditions(record, Arrays.asList(conditions), operators)) {
                resultKeys.add(k);
            }
        }

        return resultKeys;
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

    // Evaluate a single condition
    private boolean evaluateSingleCondition(Map<String, Object> record, String condition) {
        String[] parts = condition.split(" ");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid condition format: " + condition);
        }
        String columnName = parts[0];
        String operator = parts[1];
        Object value = TypeConverter.parseValue(parts[2].replace("'", "")); // Remove quotes for string values

        Object recordValue = record.get(columnName);
        return compare(recordValue, operator, value);
    }

    private boolean compare(Object recordValue, String operator, Object value) {
        if (recordValue == null) {
            return false; // Handle null values appropriately
        }

        switch (operator) {
            case "=":
                return Objects.equals(recordValue, value);
            case "!=":
                return !Objects.equals(recordValue, value);
            case ">":
                return ((Comparable<Object>) recordValue).compareTo(value) > 0;
            case "<":
                return ((Comparable<Object>) recordValue).compareTo(value) < 0;
            case ">=":
                return ((Comparable<Object>) recordValue).compareTo(value) >= 0;
            case "<=":
                return ((Comparable<Object>) recordValue).compareTo(value) <= 0;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    // Update records based on a condition
    // Update updateRecords to handle complex conditions
    public void updateRecords(String condition, Map<String, Object> updatedValues) {
        List<Integer> keysToUpdate = selectRecords(condition);
        for (int key : keysToUpdate) {
            Map<String, Object> recordToUpdate = bPlusTree.search(key);
            for (String column : updatedValues.keySet()) {
                if (columns.contains(column)) {
                    recordToUpdate.put(column, updatedValues.get(column));
                }
            }

            bPlusTree.update(key, recordToUpdate); // Assuming BPlusTree has an update method
        }
    }


    // Delete records based on a condition
    // Update deleteRecords to handle complex conditions
    public void deleteRecords(String condition) {
        List<Integer> keysToDelete = selectRecords(condition);
        for (int key : keysToDelete) {
            bPlusTree.delete(key);
        }
    }

    public void displayTableInfo() {
        System.out.println("Table " + tableName);
        bPlusTree.traverse();
    }

    public int getNumberOfColumns() {
        return columns.size();
    }
}
