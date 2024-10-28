package edu.smu.smusql.pair1;

import edu.smu.smusql.Hashing.Algorithms.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Table {
    private HashMap<Integer, Record> records; // Stores the records - Key = ID of record
    private CuckooHashTable cuckooHashTable; // Stores records using Cuckoo Hashing
    private HashMap<String, AVLTree<Object>> columns; // Column name and Tree
    private List<String> columnOrder; // maintain order of col

    public Table() {
        this.records = new HashMap<>();
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

        records.put(newRecord.getId(), newRecord); // Add to HashTable

        int id = (Integer) values.get(0);
        for (int i = 1; i < columnOrder.size(); i++) { // Add to AVL Tree
            columns.get(columnOrder.get(i)).insert(values.get(i), id); // Add to all Trees
        }
    }

    public void insertRecordCuckoo(List<Object> values) {
        Record newRecord = new Record(columnOrder, values); // Match Col to Value

        cuckooHashTable.insert(newRecord);

        int id = (Integer) values.get(0);
        for (int i = 1; i < columnOrder.size(); i++) { // Add to AVL Tree
            columns.get(columnOrder.get(i)).insert(values.get(i), id); // Add to all Trees
        }
    }

    public void deleteRecords(List<Integer> list) {
        for (Integer id : list) {
            Record record = records.get(id);
            for (int i = 1; i < columnOrder.size(); i++) { // Remove from AVL Tree
                String columnName = columnOrder.get(i);
                columns.get(columnName).remove(record.getColumnValue(columnName), id); // Remove from all Trees
            }
            records.remove(id); // Remove from hashTable
        }
    }

    public void deleteRecordsCuckoo(List<Integer> list) {
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

    public void updateRecord(List<Integer> list, String columnName, Object value) {
        AVLTree<Object> tree = columns.get(columnName);
        for (Integer id : list) {
            Record record = records.get(id);
            if (record == null) return;
            tree.remove(record.getColumnValue(columnName), id);
            tree.insert(value, record.getId());
            record.setColumnValue(columnName, value);
        }
    }

    public void updateRecordCuckoo(List<Integer> ids, String columnName, Object newValue) {
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
        for (Record record : records.values()) {
            sb.append(record.toString(columnOrder));
        }
        return sb.toString();
    }

    private List<Integer> getForId(String operator, Object value) {
        List<Integer> result = new ArrayList<>();
        if (records.get(value) != null) result.add((Integer) value); // id is unique, no need for AVL tree lookup
        return result;
    }

    public List<Integer> getWithCondition(String colName, String operator, Object value) {
        List<Integer> result = new ArrayList<>();
        if (colName.equals("id")) {
            return getForId(operator, value); // id is unique, no need for AVL tree lookup
        }

        AVLTree<Object> tree = columns.get(colName);
        if (operator.contains("=")) {
            AVLNode<Object> found = tree.get(value);
            if (found != null) result.addAll(found.getValues());
        }
        if (operator.contains(">")) result.addAll(tree.findMore(value)); // greater than
        if (operator.contains("<")) result.addAll(tree.findLess(value)); // less than
        return result;
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
        for (Integer id : subset) { // Iterate through the subset
            sb.append(records.get(id).toString(columnOrder));
        }
        return sb.toString();
    }
}
