package edu.smu.smusql;

import java.util.*;
import edu.smu.smusql.model.Table2;

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

            Table2 table = db.getTable(tableName);
            List<Map<String, Object>> allRows = table.getRecords();

            // If there are conditions to evaluate
            if (parsedCommand.size() > 1) {
                List<String> conditions = (List<String>) parsedCommand.get(1);
                List<String> operators = (List<String>) parsedCommand.get(2);

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
            List<Object> parsedCommand = Parser.parseUpdate(String.join(" ", tokens));
            String tableName = (String) parsedCommand.get(0); // Cast to String
            Map<String, Object> updatedValues = (Map<String, Object>) parsedCommand.get(1); // Cast to Map
            String condition = (String) parsedCommand.get(2); // Cast to String

            if (!db.doesTableExist(tableName)) {
                return "ERROR: Table does not exist";
            }

            Table2 table = db.getTable(tableName);
            // Update records based on the specified condition
            table.updateRecords(condition, updatedValues);

            // Count the number of rows updated
            List<Integer> updatedKeys = table.selectRecords(condition);
            int rowsUpdated = updatedKeys.size();

            return rowsUpdated + " row(s) updated.";
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

            Table2 table = db.getTable(tableName); // Retrieve the table instance
            // Perform the delete operation based on the specified condition
            table.deleteRecords(condition);

            // Count the number of rows deleted by re-selecting with the same condition
            List<Integer> deletedKeys = table.selectRecords(condition);
            int rowsDeleted = deletedKeys.size();

            return rowsDeleted + " row(s) deleted."; // Print the number of rows deleted
        } catch (Exception e) {
            return "ERROR: " + e.getMessage(); // Handle exceptions
        }
    }
}
