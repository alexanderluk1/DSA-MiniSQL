package edu.smu.smusql.model;
import java.util.*;

public class Table2 {
    String tableName;
    List<String> columns;
    BPlusTree<Integer, Map<String, Object>> bPlusTree; // Assuming Integer as the primary key for simplicity.

    public Table2(String tableName, List<String> columns,int orderNumber) {
        this.tableName = tableName;
        this.columns = columns;
        this.bPlusTree = new BPlusTree<>(orderNumber);
    }

    // Insert a row into the table
    public void insertRecord(int key, Map<String, Object> row) {
        bPlusTree.insert(key, row);
    }

    // Search for a row by primary key
    public Map<String, Object> selectRecord(int key) {
        return bPlusTree.search(key);
    }

    // Update a row by primary key
    public void updateRecord(int key, Map<String, Object> updatedRow) {
        bPlusTree.update(key, updatedRow);
    }

    // Delete a row by primary key
    public void deleteRecord(int key) {
        bPlusTree.delete(key);
    }


    public void displayTableInfo(){
        System.out.println("Table "+tableName);
        bPlusTree.traverse();
    }

//    // Select records based on range or conditions
//    public List<Map<String, Object>> selectRange(int minKey, int maxKey) {
//        return bPlusTree.searchRange(minKey, maxKey);
//    }
}
