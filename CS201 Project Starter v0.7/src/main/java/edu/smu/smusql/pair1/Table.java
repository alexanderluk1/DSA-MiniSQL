package edu.smu.smusql.pair1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Table {
    private HashTable records; // Stores the records - Can use HashMap???
    private HashMap<String, AVLTree<Object>> columns; // Column name and Tree
    private List<String> columnOrder; // maintain order of col

    public Table() {
        this.records = new HashTable(1);
        this.columns = new HashMap<>();
        this.columnOrder = new ArrayList<>();
    }

    public void addColumn(String columnName) {
        columns.put(columnName, new AVLTree<>());
        columnOrder.add(columnName);
    }

    public void insertRecord(List<Object> values) {
        Record newRecord = new Record(columnOrder, values); // Match Col to Value
        records.put(newRecord); // Add to HashTable

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

    public void updateRecord(List<Integer> list, String columnName, String value) {
        for (Integer id : list) {
            Record record = records.get(id);
            record.setColumnValue(columnName, value);
        }
    }

    public String getAll() {
        return printHeader() + records.getAllRecords(columnOrder);
    }

    public List<Integer> getWithCondition(String colName, String operator, Object value) {
        AVLTree<Object> tree = columns.get(colName);

        List<Integer> result = new ArrayList<>();
        if (operator.contains("=")) {
            if (tree.get(value) != null) {
                result.addAll(tree.get(value).getValues());
            }
        }
        if (operator.contains(">")) result.addAll(tree.findMore(value));
        if (operator.contains("<")) result.addAll(tree.findLess(value));
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
