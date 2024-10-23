package edu.smu.smusql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import edu.smu.smusql.ErrorChecks.TypeConverter;
import edu.smu.smusql.pair1.Table;

public class Engine {

    Database db = new Database();

    public String executeSQL(String query) {
        String[] tokens = query.trim().split("\\s+");
        String command = tokens[0].toUpperCase();

        return switch (command) {
            case "CREATE" -> create(tokens[2], query);
            case "INSERT" -> insert(tokens[2], query);
            case "SELECT" -> select(tokens[3], query);
            case "UPDATE" -> update(tokens[1], query);
            case "DELETE" -> delete(tokens[2], query);
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
    public String create(String tableName, String query) {
        List<String> parsedCommand = Parser.parseCreate(query);
        db.createTable(tableName, parsedCommand);
        return "table created";
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
    public String insert(String tableName, String query) {
        List<String> parsedCommand = Parser.parseCreate(query);

        // Error Checks
        // if (!db.doesTableExist(tableName)) return Messages.TABLE_NOT_EXIST.getMessage();
        // if (!ErrorChecks.doesColumnsMatch(parsedCommand, db.getTable(tableName))) return Messages.COLUMN_MISMATCH.getMessage();

        // Convert Parameters to their corresponding type
        List<Object> convertedParameters = TypeConverter.convertParams(parsedCommand);

        // Get the table
        Table tableToAdd = db.getTable(tableName);

        // Add record to the table
        tableToAdd.insertRecord(convertedParameters);
        return "success";
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
    public String select(String tableName, String query) {
        List<String> parsedCommand = Parser.parseSelect(query);

        Table tableToSelectFrom = db.getTable(tableName);

        if (Objects.equals(parsedCommand.get(0), "basic")) {
            System.out.println(tableToSelectFrom.getAll());
            return tableToSelectFrom.getAll();
        }
        return tableToSelectFrom.formatRecords(
                    tableToSelectFrom.getWithCondition(parsedCommand));
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
    public String update(String tableName, String query) {
        // List<String> parsedCommand = Parser.parseSelect(query);
        // do you allow to set multiple columns at once or only 1 col
        Table table = db.getTable(tableName);
        List<String> parsedUpdate = Parser.updateParser(query);

        // column name to update
        String colName = parsedUpdate.get(1);
        String newValue = parsedUpdate.get(2);
        String whereClauseConditions = parsedUpdate.get(3);

        List<String> command = new ArrayList<>(Arrays.asList(colName, whereClauseConditions));
        List<Integer> recordIds = table.getWithCondition(command);

        table.updateRecord(recordIds, colName, newValue);
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
    public String delete(String tableName, String query) {
        List<String> parsedDelete = Parser.parseDelete(query);
        System.out.println(parsedDelete);
        String whereClauseConditions = parsedDelete.get(1);
        Table table = db.getTable(tableName);
        List<String> command = new ArrayList<>(Arrays.asList(whereClauseConditions));
        System.out.println(command);
        List<Integer> recordIds = table.getWithCondition(command);
        table.deleteRecords(recordIds);
        return "not implemented";
    }

    /**
     * This function will handle the command syntax checks for all cases:
     *  - CREATE, INSERT, SELECT, UPDATE, DELETE
     *
     * @param tokens - Tokens which holds the separated commands in a String array
     * @return a boolean if syntax is correct, false otherwise
     */
    public boolean checkCommandSyntax(String[] tokens) {
        return false;
    }
}
