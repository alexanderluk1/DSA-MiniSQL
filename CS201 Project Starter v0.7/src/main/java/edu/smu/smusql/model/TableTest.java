package edu.smu.smusql.model;

import java.util.ArrayList;
import java.util.*;

public class TableTest {
    public static void main(String[] args) {
        // Create a new Table with a B+ Tree of order 4
        ArrayList<String> cols = new ArrayList<>(Arrays.asList("name", "age"));
        Table2 table = new Table2("Student",cols,6);

        // Insert records into the table
        Map<String, Object> record = new HashMap<>();
        record.put("name", "Bella");
        record.put("age", 12);
        Map<String, Object> record2 = new HashMap<>();
        record2.put("name", "Ben");
        record2.put("age", 14);
        Map<String, Object> record3 = new HashMap<>();
        record3.put("name", "James");
        record3.put("age", 16);
        table.insertRecord(1, record);
        table.insertRecord(2, record2);
        table.insertRecord(3, record3);
//        table.insertRecord(4, "Record 4");

        // Select and print records
        System.out.println("Selecting records:");
        System.out.println("Key 1: " + table.selectRecord(1)); // Output: Record 1
        System.out.println("Key 5: " + table.selectRecord(5)); // Output: null (Record doesn't exist)

        // Update a record
        System.out.println("\nUpdating record with key 1:");
        Map<String, Object> updatedRecord = new HashMap<>();
        updatedRecord.put("name", "Ben");
        updatedRecord.put("age", 12);
        table.updateRecord(1, updatedRecord);
        System.out.println("Key 1: " + table.selectRecord(1)); // Output: Updated Record 2

        // Delete a record
        System.out.println("\nDeleting record with key 1:");
        table.deleteRecord(1);
        System.out.println("Key 1: " + table.selectRecord(1)); // Output: null (Record was deleted)

        System.out.println("\nDisplaying DB");
        table.displayTableInfo();

    }
}
