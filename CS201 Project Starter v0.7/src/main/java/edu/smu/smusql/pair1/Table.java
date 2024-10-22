package edu.smu.smusql.pair1;

import java.util.HashMap;
import java.util.List;

public class Table {
    private String tableName;
    private HashTable records; // Stores the records
    private HashMap<String, AVLTree> columns; // Column name and Tree

    public Table(String tableName) {
        this.tableName = tableName;
        this.records = new HashTable(1); 
        this.columns = new HashMap<>();
    }

    public void addColumn(String columnName) {
        columns.put(columnName, new AVLTree<>());
    }

    public void insertRecord(List<Object> values) {
        System.out.println("Inserting Record...");
        Record newRecord = new Record((Integer) values.get(0)); // Assuming the first value is the ID
        int index = 0; 

        for (String colName : columns.keySet()) { // Add to each AVL tree
            if (index == 0) { // Skip the ID column
                index++;
                continue;
            }

            Object value = values.get(index); // Get the corresponding value
            newRecord.setColumn(colName, value); // Set the column value in the record
            columns.get(colName).insert((Comparable) value, newRecord.getId()); // Insert into the AVL tree
            index++;
        }

        records.put(newRecord); // Add to HashTable
    }

    public void deleteRecord(Record record) {
        records.put(record);
    }

    public String getTableName() {
        return tableName;
    }

    public HashTable getRecords() {
        return records;
    }

    public void print() {
        System.out.println("Table: " + tableName);

        for (int i = 0; i < records.getSize(); i++) { // Iterate through each bucket
            ListNode current = records.getStartOfBucket(i);
            while (current != null) {
                System.out.println(current.getRecord()); // Print the record
                current = current.getNext(); // Move to the next node
            }
        }
    }
}
