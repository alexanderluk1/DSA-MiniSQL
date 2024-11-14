package edu.smu.smusql;

import java.util.*;
import edu.smu.smusql.model.Table;

public class Engine {
    private Database db = new Database();

    /**
     * Executes an SQL query by parsing and dispatching it to the appropriate method.
     *
     * @param query The SQL query to execute.
     * @return Result message from the executed command.
     */
    public String executeSQL(String query) {
        String[] tokens = query.trim().split("\\s+");
        String command = tokens[0].toUpperCase();

        return switch (command) {
            case "CREATE" -> create(tokens);
            case "INSERT" -> insert(tokens);
            case "SELECT" -> select(tokens);
            case "UPDATE" -> update(tokens);
            case "DELETE" -> delete(tokens);
            default -> "ERROR: Unknown command";
        };
    }

    /**
     * Creates a new table based on the parsed command.
     *
     * @param tokens The tokens from the CREATE statement.
     * @return Result message indicating success or error.
     */
    private String create(String[] tokens) {
        try {
            List<String> parsedCommand = Parser.parseCreate(tokens);
            String tableName = parsedCommand.get(0);

            if (db.doesTableExist(tableName)) {
                return "ERROR: Table already exists";
            }
            db.createTable(tableName, parsedCommand.subList(1, parsedCommand.size()));
            return "Table created successfully";
        } catch (Exception e) {

            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * Inserts a record into a specified table.
     *
     * @param tokens The tokens from the INSERT statement.
     * @return Result message indicating success or error.
     */
    private String insert(String[] tokens) {
        try {
            List<Object> parsedCommand = Parser.parseInsert(tokens);
            String tableName = (String) parsedCommand.get(0);

            if (!db.doesTableExist(tableName)) {
                return "ERROR: Table does not exist";
            }
            if (!db.getTable(tableName).insertRecord(parsedCommand.subList(1, parsedCommand.size()))) {
                return "ERROR: Failed to insert record";
            }
            return "Record inserted successfully";
        } catch (Exception e) {


            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * Selects records from a specified table based on conditions.
     *
     * @param tokens The tokens from the SELECT statement.
     * @return Formatted string of selected records or error message.
     */
    private String select(String[] tokens) {
        try {
            List<Object> parsedCommand = Parser.parseSelectConditions(String.join(" ", tokens));
            String tableName = (String) parsedCommand.get(0);

            if (!db.doesTableExist(tableName)) {
                return "ERROR: Table does not exist";
            }

            Table table = db.getTable(tableName);
            List<Map<String, Object>> allRows = table.getRecords();

            // If there are conditions to evaluate
            if (parsedCommand.size() > 1) {
                List<String> conditions = (List<String>) parsedCommand.get(1);

                // Join the conditions for use in the Table2 class
                String combinedCondition = String.join(" ", conditions);
                List<Integer> selectedKeys = table.selectRecords(combinedCondition); // Use existing method
                List<Map<String, Object>> filteredRows = selectedKeys.stream()
                        .map(table::selectRecord)
                        .toList();

                return formatRows(filteredRows);
            } else {
                return formatRows(allRows);
            }
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * Formats the rows of selected records into a string.
     *
     * @param rows The list of rows to format.
     * @return Formatted string representation of the rows.
     */
    private String formatRows(List<Map<String, Object>> rows) {
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> row : rows) {
            sb.append(row.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Updates records in a specified table based on a condition.
     *
     * @param tokens The tokens from the UPDATE statement.
     * @return Result message indicating success or error.
     */
    private String update(String[] tokens) {
        try {
            // Parse the update query using Parser class
            List<Object> parsedCommand = Parser.parseUpdate(String.join(" ", tokens));

            // Extract the table name, update values, and condition from parsed command
            String tableName = (String)parsedCommand.get(0);
            HashMap<String,Object> input = (HashMap<String, Object>) parsedCommand.get(1);


            // Extract the condition for the update
            String condition = (String) parsedCommand.get(2);

            // Check if the table exists
            if (!db.doesTableExist(tableName)) {
                return "ERROR: Table does not exist";
            }

            // Get the table and perform the update
            Table table = db.getTable(tableName);
            int rowsUpdated = table.updateRecords(condition,input);
            // Return the number of rows updated
            return rowsUpdated +" row(s) updated.";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * Deletes records from a specified table based on a condition.
     *
     * @param tokens The tokens from the DELETE statement.
     * @return Result message indicating success or error.
     */
    private String delete(String[] tokens) {
        try {
            List<String> parsedCommand = Parser.parseDelete(String.join(" ", tokens));
            String tableName = parsedCommand.get(0); // Get the table name
            String condition = parsedCommand.get(1); // Get the condition

            if (!db.doesTableExist(tableName)) {
                return "ERROR: Table does not exist"; // Check if the table exists
            }

            Table table = db.getTable(tableName); // Retrieve the table instance
            // Perform the delete operation based on the specified condition
            int rowsDeleted = table.deleteRecords(condition);

            return rowsDeleted + " row(s) deleted."; // Print the number of rows deleted
        } catch (Exception e) {
            return "ERROR: " + e.getMessage(); // Handle exceptions
        }
    }

    // TEST
    private Map<String, String[]> tables = new HashMap<>(); // Store table structure

    public boolean doesTableExist(String tableName) {
        return tables.containsKey(tableName);
    }

    public static void main(String[] args) {
        System.out.println("Running tests...");

        Engine engine = new Engine();
//
//        {city='Dallas', name='User14450', id=14450, age=40}
//        {city='Boston', name='User12533', id=12533, age=75}
//        {city='New York', name='User14318', id=14318, age=29}
        // Test: Create table - Success
        String createTableQuery = "CREATE TABLE users (id INT, city VARCHAR(50), name VARCHAR(50), age INT)";
        String createTableResult = engine.executeSQL(createTableQuery);
        assert createTableResult.equals("Table created successfully") : "Test failed: Create Table";

        // Verify table creation
        assert engine.doesTableExist("users") : "Test failed: Table 'users' does not exist after creation";

        // Test: Insert record - Success
        String insertRecordQuery = "INSERT INTO users VALUES (1, 'Dallas', 'User1', age=40)";
        String insertRecordResult = engine.executeSQL(insertRecordQuery);
        assert insertRecordResult.equals("Record inserted successfully") : "Test failed: Insert Record";

        // Test: Select records - Success
        String selectQuery = "SELECT * FROM users";
        String selectResult = engine.executeSQL(selectQuery);
        assert selectResult.contains("John Doe") : "Test failed: Select Records";

        // Test: Update record - Success
        String updateQuery = "UPDATE users SET name = 'Jane Doe' WHERE id = 1";
        String updateResult = engine.executeSQL(updateQuery);
        assert updateResult.equals("1 row(s) updated.") : "Test failed: Update Record";

        // Test: Delete record - Success
        String deleteQuery = "DELETE FROM users WHERE id = 1";
        String deleteResult = engine.executeSQL(deleteQuery);
        assert deleteResult.equals("1 row(s) deleted.") : "Test failed: Delete Record";

        // Test: Insert record - Table does not exist
        String nonExistentInsertQuery = "INSERT INTO nonexistent_table VALUES (1, 'John Doe')";
        String nonExistentInsertResult = engine.executeSQL(nonExistentInsertQuery);
        assert nonExistentInsertResult.equals("ERROR: Table does not exist") : "Test failed: Insert into Non-Existent Table";

        // Test: Unknown command
        String unknownCommandQuery = "DROP TABLE users";
        String unknownCommandResult = engine.executeSQL(unknownCommandQuery);
        assert unknownCommandResult.equals("ERROR: Unknown command") : "Test failed: Unknown Command";

        System.out.println("All tests passed.");
    }
}
