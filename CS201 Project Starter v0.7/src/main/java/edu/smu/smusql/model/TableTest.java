package edu.smu.smusql.model;

import edu.smu.smusql.Engine;

import java.util.*;


public class TableTest {
    public static void main(String[] args) {
        Engine engine = new Engine();

        // Test: Create table - Success
        String createTableQuery = "CREATE TABLE student (id, name, age, gpa )";
        engine.executeSQL(createTableQuery);

        // Test: Insert records - Success
        String insertRecordQuery1 = "INSERT INTO student VALUES (1, John, 30, 2.4)";
        String insertRecordQuery2 = "INSERT INTO student VALUES (2, little_bobby_tables, 34, 1.4)";
        String insertRecordQuery3 = "INSERT INTO student VALUES (3, Sam, 35, 1.4)";
        engine.executeSQL(insertRecordQuery1);
        engine.executeSQL(insertRecordQuery2);
        engine.executeSQL(insertRecordQuery3);

        // Select and print records
        System.out.println("Selecting all records:");
        String selectQuery = "SELECT * FROM student";
        System.out.println(engine.executeSQL(selectQuery));

        // Test: Update records - Success
        String updateQuery = "UPDATE student SET gpa = 3.5 WHERE gpa <= 1.5 AND name = little_bobby_tables";
        engine.executeSQL(updateQuery);

        // Test: Delete record - Success
        String deleteQuery = "DELETE FROM student WHERE gpa = 1.4 OR name = Sam";
        engine.executeSQL(deleteQuery);

        // Test: Select and print remaining records
        System.out.println("\nRecords after deletion:");
        System.out.println(engine.executeSQL(selectQuery));

    }
}
