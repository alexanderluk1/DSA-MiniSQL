package edu.smu.smusql.pair1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.smu.smusql.Parser;

public class Table {
    private String tableName;
    private HashTable records; // Stores the records - Can use HashMap???
    private HashMap<String, AVLTree<Object>> columns; // Column name and Tree
    private List<String> columnOrder; // maintain order of col

    public Table(String tableName) {
        this.tableName = tableName;
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
        int id = (Integer) values.get(0);

        for (int i = 1; i < columnOrder.size(); i++) { // Add to AVL Tree
            columns.get(columnOrder.get(i)).insert(values.get(i), id); // Add to all Trees
        }
        
        records.put(newRecord); // Add to HashTable
    }

    public void deleteRecords(List<Integer> list) {
        for (Integer id : list) {
            System.out.println("Before: " + records.get(id));
            records.remove(id);
            System.out.println("After: " + records.get(id));
        }
    }

    public void updateRecord(List<Integer> list, String colName, String value) {
        for (Integer id : list) {
            
            Record record = records.get(id);
            System.out.println("Before: " + record);
            record.setColumn(colName, value);
            System.out.println("After: " + record);
        }
    }

    public String getTableName() {
        return tableName;
    }

    public HashTable getRecords() {
        return records;
    }

    public String getAll() {
        StringBuilder sb = new StringBuilder();
        sb.append(printHeader(columnOrder)); // Get headers
    
        // Iterate through each bucket in the HashTable
        for (int i = 0; i < records.getSize(); i++) {
            BucketNode current = records.getStartOfBucket(i); // Get the start of the bucket
            while (current != null) { // Iterate through the linked list in the bucket
                Record record = current.getRecord(); // Get the record from the current node
                sb.append(String.format("| %-10d |", record.getId())); // Print the ID
                for (String colName : columnOrder) {
                    if (colName.equals("id")) {
                        continue; // Skip id column
                    }
                    sb.append(String.format(" %-20s |", record.getColumn(colName))); // Print other columns
                }
                sb.append("\n"); // New line for the next record
                current = current.getNext(); // Move to the next node
            }
        }
        return sb.toString();
    }

    public List<Integer> getWithCondition(String colName, String operator, Object value) {
        AVLTree<Object> tree = columns.get(colName);

        List<Integer> result = new ArrayList<>();
        if (operator.contains("=")) {
            result.addAll(tree.get(value).getValues());
        }

        if (operator.contains(">")) {
            result.addAll(tree.findMore(value));
        }

        if (operator.contains("<")) {
            result.addAll(tree.findLess(value));
        }

        return result;
    }

    private String printHeader(List<String> columnNames) {
        StringBuilder sb = new StringBuilder();
        sb.append("| ID         |");
    
        // Print the dynamic column names based on the ordered column names
        for (String columnName : columnNames) {
            if (columnName.equals("id")) {
                continue; // Skip id column
            }
            sb.append(String.format(" %-20s |", columnName));
        }
        sb.append("\n|------------|");
        for (int i = 1; i < columnNames.size(); i++) {
            sb.append("----------------------|");
        }
        sb.append("\n");
    
        return sb.toString();
    }
    

    public String formatRecords(List<Integer> subset) {
        StringBuilder sb = new StringBuilder();
    
        sb.append(printHeader(columnOrder)); // Get headers
    
        for (Integer id : subset) { // Iterate through the subset
            Record record = records.get(id);
            sb.append(String.format("| %-10d |", record.getId()));
            for (String colName : columnOrder) {
                if (colName.equals("id")) {
                    continue; // Skip id column
                }
                sb.append(String.format(" %-20s |", record.getColumn(colName)));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
