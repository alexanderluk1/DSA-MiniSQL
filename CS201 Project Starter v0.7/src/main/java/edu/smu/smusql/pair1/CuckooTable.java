package edu.smu.smusql.pair1;

import edu.smu.smusql.Hashing.Algorithms.CuckooHashTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CuckooTable {
    private CuckooHashTable cuckooHashTable; // Stores records using Cuckoo Hashing
    private HashMap<String, AVLTree<Object>> columns; // Column name and Tree
    private List<String> columnOrder; // maintain order of col

    public CuckooTable() {
        this.columns = new HashMap<>();
        this.columnOrder = new ArrayList<>();
        this.cuckooHashTable = new CuckooHashTable();
    }

    public void addColumn(String columnName) {
        columns.put(columnName, new AVLTree<>());
        columnOrder.add(columnName);
    }

    public void insertRecord(List<Object> values) {
        Record newRecord = new Record(columnOrder, values); // Match Col to Value

        cuckooHashTable.insert(newRecord);

        int id = (Integer) values.get(0);
        for (int i = 1; i < columnOrder.size(); i++) { // Add to AVL Tree
            columns.get(columnOrder.get(i)).insert(values.get(i), id); // Add to all Trees
        }
    }

    public void deleteRecords(List<Integer> list) {
        for (Integer id : list) {
            Record record = cuckooHashTable.get(id);
            if (record != null) {
                // Remove from each column's AVL tree
                for (int i = 1; i < columnOrder.size(); i++) { // Skip the ID column
                    String columnName = columnOrder.get(i);
                    columns.get(columnName).remove(record.getColumnValue(columnName), id);
                }
                // Remove from Cuckoo Hash Table
                cuckooHashTable.delete(id);
            }
        }
    }

    public void updateRecord(List<Integer> ids, String columnName, Object newValue) {
        AVLTree<Object> tree = columns.get(columnName);
        for (Integer id : ids) {
            Record record = cuckooHashTable.get(id);
            if (record == null) continue;

            // Update AVL tree for the column
            tree.remove(record.getColumnValue(columnName), id);
            tree.insert(newValue, id);

            // Update the record
            record.setColumnValue(columnName, newValue);
        }
    }

    public String getAll() {
        StringBuilder sb = new StringBuilder();
        sb.append(printHeader());

        // Retrieve all records from CuckooHashTable
        List<Record> allRecords = cuckooHashTable.getAllRecords(); // Assuming getAllRecords is implemented in CuckooHashTable
        for (Record record : allRecords) {
            sb.append(record.toString(columnOrder));
        }
        return sb.toString();
    }

    public List<Integer> getForId(String operator, Object value) {
        List<Integer> result = new ArrayList<>();

        // Retrieve record directly from Cuckoo HashTable by ID
        Integer id = (Integer) value;
        Record record = cuckooHashTable.get(id);

        if (record != null && "id".equals(operator)) {
            result.add(id); // Only add if the record exists
        }
        return result;
    }

    public List<Integer> getWithCondition(String colName, String operator, Object value) {
        List<Integer> result = new ArrayList<>();

        if ("id".equals(colName)) {
            // If the column is "id", retrieve directly from the cuckoo hash table
            Integer id = (Integer) value;
            Record record = cuckooHashTable.get(id);
            if (record != null && satisfiesCondition(record.getColumnValue(colName), operator, value)) {
                result.add(id);
            }
            return result;
        }

        // For other columns, check in the corresponding AVL tree
        AVLTree<Object> tree = columns.get(colName);
        if (tree == null) return result; // Early exit if column does not exist

        // Apply condition using the AVL tree for greater than or less than operators
        if (">".equals(operator)) {
            result.addAll(tree.findMore(value));
        } else if ("<".equals(operator)) {
            result.addAll(tree.findLess(value));
        } else if ("=".equals(operator)) {
            AVLNode<Object> node = tree.get(value);
            if (node != null) {
                result.addAll(node.getValues());
            }
        }
        return result;
    }

    // Helper method to evaluate condition
    private boolean satisfiesCondition(Object columnValue, String operator, Object value) {
        if (columnValue instanceof Comparable && value instanceof Comparable) {
            Comparable<Object> compColumnValue = (Comparable<Object>) columnValue;
            Comparable<Object> compValue = (Comparable<Object>) value;

            return switch (operator) {
                case ">" -> compColumnValue.compareTo(compValue) > 0;
                case "<" -> compColumnValue.compareTo(compValue) < 0;
                case "=" -> compColumnValue.compareTo(compValue) == 0;
                default -> false;
            };
        }
        return false;
    }

    private String printHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("| id         |");

        // Print the dynamic column names based on the ordered column names
        for (int i = 1; i < columnOrder.size(); i++) { // skip id
            sb.append(String.format(" %-20s |", columnOrder.get(i)));
        }
        sb.append("\n|------------|");
        for (int i = 1; i < columnOrder.size(); i++) {
            sb.append("----------------------|");
        }
        sb.append("\n");

        return sb.toString();
    }

    public String formatRecords(List<Integer> subset) {
        StringBuilder sb = new StringBuilder();
        sb.append(printHeader()); // Get headers

        for (Integer id : subset) {
            Record record = cuckooHashTable.get(id); // Retrieve record from cuckooHashTable
            if (record == null) {
                // Skip if the record is not found
                continue;
            }
            sb.append(record.toString(columnOrder)); // Format the record based on column order
        }
        return sb.toString();
    }
}
