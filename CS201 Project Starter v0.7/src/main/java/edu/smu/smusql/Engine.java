package edu.smu.smusql;

import java.util.List;

public class Engine {

    // Reference to the database object
    Database db = new Database();

    public String executeSQL(String query) {
        String[] tokens = query.trim().split("\\s+");
        String command = tokens[0].toUpperCase();

        return switch (command) {
            case "CREATE" -> create(tokens[2], query);
            case "INSERT" -> insert(tokens);
            case "SELECT" -> select(tokens);
            case "UPDATE" -> update(tokens);
            case "DELETE" -> delete(tokens);
            default -> "ERROR: Unknown command";
        };
    }

    /**
     * CREATE TABLE student (id, name, age, gpa, deans_list)
     *
     * 1. Check for command Syntax error
     * 2. Create the table with the params
     * 3. Output success
     */
    public String create (String tableName, String query) {
        List<String> parsedCommand = Parser.parseCreate(query);
        if (!db.createTable(tableName, parsedCommand)) return String.format("Failed to Create Table, %s already exists in the database!", tableName);
        return String.format("Created Table %s in the Database", tableName);
    }

    /**
     * INSERT INTO student VALUES (1, John, 30, 2.4, False)
     *
     * 0. Check for command Syntax error
     * 1. Error Handling: Check if there is a table called XX
     * 2. Error Handling: Check if params from command match params from table
     * 3. Error Handling: Check if param type matches param types from table
     * 4. Add params to table
     * 5. Output success
     */
    public String insert(String[] tokens) {
        return "not implemented";
    }

    /**
     * SELECT * FROM student
     * SELECT * FROM student WHERE gpa > 3.8 AND age < 20
     * SELECT * FROM student WHERE gpa > 3.8 OR age < 20
     *
     * 0. Check for command Syntax error
     * 1. Check if Table exists
     * 2. Handle Base case
     * 3. Handle Filtering Condition(s)
     * 4. Retrieve row(s) from Data Structure
     * 5. Format and return to user
     */
    public String select(String[] tokens) {
        //TODO
        return "not implemented";
    }

    /**
     * UPDATE student SET age = 25 WHERE id = 1
     * UPDATE student SET deans_list = True WHERE gpa > 3.8 OR age = 201
     *
     * 0. Check for command Syntax error
     * 1. Check if table exists
     * 2. Check if column exists
     * 3. Find row(s) where it fulfills the Filtering condition(s)
     * 4. Update row(s)
     * 5. Return success message "Updated 3 rows"
     */
    public String update(String[] tokens) {
        //TODO
        return "not implemented";
    }

    /**
     * DELETE FROM student WHERE gpa < 2.0
     * DELETE FROM student WHERE gpa < 2.0 OR name = little_bobby_tables
     *
     * 0. Check for command Syntax error
     * (Must be in this format: DELETE FROM `table` WHERE `col` Comparison-Operator X)
     * - Note, If Comparison-Op is '=' Check for equality w/o case-sensitive
     *
     * 1. Check if table exists
     * 2. Handle filtering conditions
     * 3. Check if row(s) exist
     *  True - Delete row(s)
     *  False - Return message (No rows found)
     *
     * 4. Return success message
     */
    public String delete(String[] tokens) {

        return "not implemented";
    }
}
