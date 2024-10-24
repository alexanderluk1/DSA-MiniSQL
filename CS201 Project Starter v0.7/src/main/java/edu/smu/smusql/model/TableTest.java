package edu.smu.smusql.model;

import edu.smu.smusql.Parser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.*;

public class TableTest {
    public static void main(String[] args) {
        // Create a new Table with SQL-like commands
        String createTableQuery = "CREATE TABLE student (id, name, age, gpa, deans_list)";
        List<String> columns = Parser.parseCreate(createTableQuery);
        Table2 table = new Table2("student", columns, 10);

        // Insert records
        String insertQuery1 = "INSERT INTO student VALUES (1, John, 30, 2.4, False)";
        String insertQuery2 = "INSERT INTO student VALUES (2, little_bobby_tables, 34, 1.4, True)";
        String insertQuery3 = "INSERT INTO student VALUES (3, Sam, 35, 1.4, False)";

        table.insertRecord(Parser.parseInsert(insertQuery1.split(" ")));
        table.insertRecord(Parser.parseInsert(insertQuery2.split(" ")));
        table.insertRecord(Parser.parseInsert(insertQuery3.split(" ")));

        // Select and print records
        System.out.println("Selecting all records:");
        for (int i = 1; i <= table.getCurrentKey(); i++) {
            Map<String, Object> record = table.selectRecord(i);
            System.out.println("Key " + i + ": " + record);
        }


        System.out.println("Selecting specific records:");

        String selectQuery = "Select * from student where gpa < 2.0 AND name = little_bobby_tables";

        String condition4 = "gpa < 2.0 AND name = little_bobby_tables";
        List<Integer> results = table.selectRecords(condition4);
        for (int i = 0; i < results.size(); i++) {
            Map<String, Object> record = table.selectRecord(results.get(i));
            System.out.println("Key " + i + ": " + record);
        }

        // Update records without knowing the key
        String updateQuery = "UPDATE student SET gpa = 3.5 WHERE gpa > 2.0 OR name = little_bobby_tables";
        Map<String, Object> updatedValues = new HashMap<>();
        updatedValues.put("gpa", 3.5);

        table.updateRecords("gpa > 2.0 OR name = little_bobby_tables", updatedValues);


        System.out.println("\nRecords after update:");
        for (int i = 1; i <= table.getCurrentKey(); i++) {
            Map<String, Object> record = table.selectRecord(i);
            System.out.println("Key " + i + ": " + record);
        }

        // Delete records without knowing the key
        String deleteQuery = "DELETE FROM student WHERE gpa < 2.0 AND name = Sam";
        table.deleteRecords("gpa < 1.0 OR name = Sam");

        System.out.println("\nRecords after deletion:");
        for (int i = 1; i <= table.getCurrentKey(); i++) {
            Map<String, Object> record = table.selectRecord(i);
            if (record != null) {
                System.out.println("Key " + i + ": " + record);
            }
        }


        System.out.println("\nDisplaying final table info:");
        table.displayTableInfo();
    }
}
