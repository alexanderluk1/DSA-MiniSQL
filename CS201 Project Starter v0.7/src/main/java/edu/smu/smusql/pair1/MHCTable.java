package edu.smu.smusql.pair1;

import edu.smu.smusql.Hashing.Algorithms.MultiplicativeHashChaining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MHCTable {
    private MultiplicativeHashChaining mhcHashTable; // Stores records using Multiplicative Hash Chaining
    private HashMap<String, AVLTree<Object>> columns; // Column name and Tree
    private List<String> columnOrder; // maintain order of col

    public MHCTable(double loadFactor) {
        this.columns = new HashMap<>();
        this.columnOrder = new ArrayList<>();
        this.mhcHashTable = new MultiplicativeHashChaining(loadFactor);
    }

    public void addColumn(String columnName) {
        columns.put(columnName, new AVLTree<>());
        columnOrder.add(columnName);
    }

    public void insertRecord(List<Object> values) {
        Record newRecord = new Record(columnOrder, values); // Match Col to Value

        mhcHashTable.put((Integer) values.get(0), newRecord); // Insert record into MHC hash table

        int id = (Integer) values.get(0);
        for (int i = 1; i < columnOrder.size(); i++) { // Add to AVL Tree
            columns.get(columnOrder.get(i)).insert(values.get(i), id); // Add to all Trees
        }
    }

    public void deleteRecords(List<Integer> list) {
        for (Integer id : list) {
            Record record = mhcHashTable.get(id);
            if (record != null) {
                // Remove from each column's AVL tree
                for (int i = 1; i < columnOrder.size(); i++) { // Skip the ID column
                    String columnName = columnOrder.get(i);
                    columns.get(columnName).remove(record.getColumnValue(columnName), id);
                }
                // Remove from MHC Hash Table
                mhcHashTable.delete(id);
            }
        }
    }

    public void updateRecord(List<Integer> ids, String columnName, Object newValue) {
        AVLTree<Object> tree = columns.get(columnName);
        for (Integer id : ids) {
            Record record = mhcHashTable.get(id);
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

        // Retrieve all records from MHC hash table
        List<Record> allRecords = getAllRecords();
        for (Record record : allRecords) {
            sb.append(record.toString(columnOrder));
        }
        return sb.toString();
    }

    public List<Record> getAllRecords() {
        List<Record> records = new ArrayList<>();
        LinkedList<Integer> allKeys = mhcHashTable.getAllKeys();

        for (Integer key : allKeys) {
            Record record = mhcHashTable.get(key);
            if (record != null) {
                records.add(record);
            }
        }
        return records;
    }

    public List<Integer> getForId(String operator, Object value) {
        List<Integer> result = new ArrayList<>();

        // Retrieve record directly from MHC hash table by ID
        Integer id = (Integer) value;
        Record record = mhcHashTable.get(id);

        if (record != null && "id".equals(operator)) {
            result.add(id); // Only add if the record exists
        }
        return result;
    }

    public List<Integer> getWithCondition(String colName, String operator, Object value) {
        List<Integer> result = new ArrayList<>();

        if ("id".equals(colName)) {
            // If the column is "id", retrieve directly from the MHC hash table
            Integer id = (Integer) value;
            Record record = mhcHashTable.get(id);
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

    public List<Integer> getBetween(String colName, Object lowerBound, Object upperBound) {
        List<Integer> result = new ArrayList<>();

        if ("id".equals(colName)) {
            // If the column is "id", retrieve directly from the cuckoo hash table
            for (int id = (Integer) lowerBound; id <= (Integer) upperBound; id++) {
                Record record = mhcHashTable.get(id);
                if (record != null) {
                    result.add(id);
                }
            }
        } 

        // For other columns, check in the corresponding AVL tree
        AVLTree<Object> tree = columns.get(colName);
        if (tree == null)
            return result; // Early exit if column does not exist

        // Apply condition using the AVL tree for greater than or less than operators
        result.addAll(tree.findBetween(lowerBound, upperBound));
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
            Record record = mhcHashTable.get(id); // Retrieve record from mhcHashTable
            if (record == null) {
                // Skip if the record is not found
                continue;
            }
            sb.append(record.toString(columnOrder)); // Format the record based on column order
        }
        return sb.toString();
    }

    public long getMemoryUsage() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }
}