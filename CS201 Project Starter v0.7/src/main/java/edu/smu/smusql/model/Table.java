package edu.smu.smusql.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
    private String tableName;
    private List<String> columns;
    private Map<String, BPlusTree<String, Integer>> columnIndexes; // B+ trees for each column
    private BPlusTree<Integer, Map<String, Object>> primaryKeyIndex; // B+ tree for primary key (ID)

    private int currentKey = 0;

    public Table(String tableName, List<String> columns, int orderNumber) {
        this.tableName = tableName;
        this.columns = columns;
        this.primaryKeyIndex = new BPlusTree<>(orderNumber);

        // Initialize a B+ tree for each column to store indexes for efficient searches
        columnIndexes = new HashMap<>();
        for (String column : columns) {
            columnIndexes.put(column, new BPlusTree<>(orderNumber));
        }
    }

    // Insert a row into the table
    public boolean insertRecord(List<Object> row) {
        if (row.size() != columns.size()) {
            throw new IllegalArgumentException("Row size must match the number of columns.");
        }

        currentKey++;
        Map<String, Object> record = new HashMap<>();
        for (int i = 0; i < columns.size(); i++) {
            record.put(columns.get(i), row.get(i));

            // Insert into the index tree of the respective column (store key as String)
            Object value = row.get(i);
            String keyString = value.toString(); // Convert key to String for storage
            if (value instanceof Comparable) {
                BPlusTree<String, Integer> columnIndex = columnIndexes.get(columns.get(i));
                columnIndex.insert(keyString, currentKey);
            }
        }

        // Insert the record into the primary key index
        primaryKeyIndex.insert(currentKey, record);
        return true;
    }

    // Search for a row by primary key
    public Map<String, Object> selectRecord(int key) {
        return primaryKeyIndex.search(key).get(0); // Convert key to String
    }

    // Search for rows based on conditions, leveraging column B+ trees for
    // efficiency
    public List<Integer> selectRecords(String condition) {
        List<Integer> resultKeys = new ArrayList<>();

        if (condition.trim().equals("id >= 0")) {
            // Get all keys from primary key index
            for (int i = 1; i <= currentKey; i++) {
                List<Map<String, Object>> record = primaryKeyIndex.search(i);
                if (!record.isEmpty()) {
                    resultKeys.add(i);
                }
            }
            return resultKeys;
        }

        // Capture conditions
        String[] conditions = condition.split("\\s+(AND|OR)\\s+");

        // Capture operators
        List<String> operators = new ArrayList<>();
        String[] parts = condition.split("\\s+");
        for (String part : parts) {
            if (part.equals("AND") || part.equals("OR")) {
                operators.add(part);
            }
        }

        // Process each condition and query the appropriate column index
        for (int i = 0; i < conditions.length; i++) {
            String cond = conditions[i];
            String columnName = getColumnFromCondition(cond);
            BPlusTree<String, Integer> columnIndex = columnIndexes.get(columnName);

            // Extract the operator and value
            String operator = getOperatorFromCondition(cond);
            String value = getValueFromCondition(cond);

            if (columnIndex != null) {
                List<Integer> columnResults = searchInColumnIndex(columnIndex, operator, value);
                if (operators.isEmpty() || i == 0) {
                    resultKeys.addAll(columnResults);
                } else {
                    // Combine results based on operators (AND/OR logic)
                    if (operators.contains("AND")) {
                        resultKeys.retainAll(columnResults); // Perform intersection for AND
                    } else if (operators.contains("OR")) {
                        resultKeys.addAll(columnResults); // Union for OR
                    }
                }
            }
        }

        return resultKeys;
    }

    // Parse the column name from the condition (e.g., "gpa > 3.8")
    private String getColumnFromCondition(String condition) {
        String regex = "(\\w+)\\s*(=|!=|>|<|>=|<=)\\s*(.+)";
        Matcher matcher = Pattern.compile(regex).matcher(condition);
        if (matcher.matches()) {
            return matcher.group(1); // Return column name
        }
        throw new IllegalArgumentException("Invalid condition format: " + condition);
    }

    // Parse the operator from the condition (e.g., ">", "<=")
    private String getOperatorFromCondition(String condition) {
        String regex = "(\\w+)\\s*(=|>=|<=|>|<)\\s*(.+)";
        Matcher matcher = Pattern.compile(regex).matcher(condition);
        if (matcher.matches()) {
            return matcher.group(2); // Return operator
        }
        throw new IllegalArgumentException("Invalid condition format: " + condition);
    }

    // Parse the value from the condition (e.g., "3.8", "'John'")
    private String getValueFromCondition(String condition) {
        String regex = "(\\w+)\\s*(=|!=|>=|<=|>|<)\\s*(.+)";
        Matcher matcher = Pattern.compile(regex).matcher(condition);
        if (matcher.matches()) {
            return matcher.group(3).replace("'", ""); // Remove quotes for string values
        }
        throw new IllegalArgumentException("Invalid condition format: " + condition);
    }

    // Search in the column index for the given condition (operator and value)
    private List<Integer> searchInColumnIndex(BPlusTree<String, Integer> columnIndex, String operator, String value) {
        // Initialize the list of matching keys
        List<Integer> matchingKeys = new ArrayList<>();

        // Depending on the operator, we will search the BPlusTree accordingly
        switch (operator) {
            case "=":
                matchingKeys = columnIndex.search(value);
                break;
            case ">":
                matchingKeys = columnIndex.searchRangeExclusive(value + "\0", String.valueOf('\uFFFF'));
                matchingKeys.removeIf(key -> key.equals(value)); // Remove the exact match
                break;
            case "<":
                matchingKeys = columnIndex.searchRangeExclusive("", value);
                matchingKeys.removeIf(key -> key.equals(value)); // Remove the exact match
                break;
            case ">=":
                matchingKeys = columnIndex.searchRangeInclusive(value, String.valueOf('\uFFFF'));
                break;
            case "<=":
                matchingKeys = columnIndex.searchRangeInclusive("", value);
                break;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
        return matchingKeys;
    }

    // Update records based on a condition and updated values
    public int updateRecords(String condition, Map<String, Object> updatedValues) {
        int count = 0;
        List<Integer> keysToUpdate = selectRecords(condition); // Assuming selectRecords gives us the keys to update

        for (int key : keysToUpdate) {
            // Fetch the current record using the primary key index
            Map<String, Object> recordToUpdate = primaryKeyIndex.search(key).get(0);

            for (String column : updatedValues.keySet()) {
                if (columns.contains(column) && !recordToUpdate.get(column).equals(updatedValues.get(column))) {
                    // Get the old and new value for the column
                    Object oldValue = recordToUpdate.get(column);
                    Object newValue = updatedValues.get(column);

                    // Update the value in the record
                    recordToUpdate.put(column, newValue);

                    // Update the column's B+ tree with the new value
                    BPlusTree<String, Integer> columnIndex = columnIndexes.get(column);
                    columnIndex.updateDuplicate(oldValue.toString(), newValue.toString(), key); // Insert new value

                    count++;
                }
            }

            // Update the record in the primary key index
            primaryKeyIndex.update(key, recordToUpdate);
        }

        return count;
    }

    // Delete records based on a condition
    public int deleteRecords(String condition) {
        List<Integer> keysToDelete = selectRecords(condition); // Assuming selectRecords gives us the keys to delete
        for (int key : keysToDelete) {
            // Fetch the record using the primary key index
            Map<String, Object> recordToDelete = primaryKeyIndex.search(key).get(0);

            if (recordToDelete != null) {
                // Delete from the primary key index
                primaryKeyIndex.delete(key);

                // Also remove the key from each column's index
                for (Map.Entry<String, Object> entry : recordToDelete.entrySet()) {
                    String column = entry.getKey();
                    String value = entry.getValue().toString(); // Convert value to String
                    BPlusTree<String, Integer> columnIndex = columnIndexes.get(column);
                    columnIndex.deleteDuplicate(value, key); // Remove from column index
                }
            }
        }

        return keysToDelete.size();
    }

}
