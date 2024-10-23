package edu.smu.smusql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/*
 * This is a rudimentary parser you may want to use to parse smuSQL statements.
 * Use of this parser is optional.
 * You may decide to use your own parser.
 * Example usage of some code in this parser can be found in the sample implementation.
 * However, the sample implementation does not have a 'Parser' class.
 */
public class Parser {

    public Parser() {}

    /* This method removes the brackets in the SQL query */
    /* Then puts the column name in a List<String> */
    public static List<String> parseCreate(String query) {
        int startIndex = query.indexOf('(');
        int endIndex = query.indexOf(')');

        List<String> parsedCreateCommand = new ArrayList<>();

        // Extract the substring between the parentheses
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            String parameters = query.substring(startIndex + 1, endIndex);
            // Add all the parameters
            Collections.addAll(parsedCreateCommand, parameters.split("\\s*,\\s*"));
        }
        return parsedCreateCommand;
    }

    /**
     *      * SELECT * FROM student
     *      * SELECT * FROM student WHERE gpa > 3.8 AND age < 20
     *      * SELECT * FROM student WHERE gpa > 3.8 OR age < 20
     */
    public static List<String> parseSelect(String query) {
        String lowerCaseQuery = query.toLowerCase(); // Convert the query to lowercase for case-insensitive comparison
        if (lowerCaseQuery.contains("where")) {
            return parseSelectWhere(query);
        }
        return parseBasicSelect(query);
    }

    // helper method for parseSelect
    /* This helper method adds a 'basic' flag as the first element of the list 
     * eg. ["basic", "SELECT", "*", "FROM", "student"]
    */
    private static List<String> parseBasicSelect(String query) {
        List<String> parsedSelectCommand = new ArrayList<>();
        parsedSelectCommand.add("basic");

        // Convert query to lowercase for case-insensitive parsing
        String lowerCaseQuery = query.toLowerCase();

        // Split the lowercase query and trim any spaces
        Collections.addAll(parsedSelectCommand, lowerCaseQuery.split(" "));

        // Remove extra whitespaces
        parsedSelectCommand.replaceAll(String::trim);

        return parsedSelectCommand;
    }

    // helper method for parseSelect
    /*
     * This method helps to break down the query into 2 main parts 
     * (1) before the WHERE clause , and
     * (2) after the WHERE clause
     * 
     * eg. ["SELECT * FROM student", "gpa > 3.8"]
     */
    private static List<String> parseSelectWhere(String query) {
        List<String> parsedSelectCommand = new ArrayList<>();

        // Convert only the SQL keywords to lowercase, not the actual values
        String[] queryParts = query.split("(?i)where");  // Case-insensitive split for the WHERE clause

        parsedSelectCommand.add(queryParts[0].trim());  // Add everything before WHERE clause (case-insensitive part)

        if (queryParts.length > 1) {
            parsedSelectCommand.add(queryParts[1].trim());  // Add everything after WHERE clause (including actual values like 'John')
        }

        return parsedSelectCommand;
    }


    // [ tableName | columnName | newValue | null OR conditionString ]
    public static List<String> updateParser(String query) {
        List<String> parsedUpdate = new ArrayList<>();

        // Convert query to lowercase for case-insensitive parsing
        String lowerCaseQuery = query.toLowerCase();

        // Split by tokens (e.g., "UPDATE table SET column = value WHERE...")
        String[] tokens = lowerCaseQuery.trim().split("\\s+");

        // Extract table name and add to parsedUpdate
        String tableName = tokens[1];
        parsedUpdate.add(tableName);

        // Extract SET clause (column name and new value)
        String setColumn = tokens[3];
        String setNewValue = tokens[5];
        parsedUpdate.add(setColumn);
        parsedUpdate.add(setNewValue);

        // Check if the query contains a WHERE clause
        if (lowerCaseQuery.contains("where")) {
            String whereClauseConditions = lowerCaseQuery.split("where")[1].trim();
            parsedUpdate.add(whereClauseConditions);  // Add WHERE conditions
        } else {
            parsedUpdate.add(null);  // Indicate no WHERE clause
        }

        return parsedUpdate;
    }

    public static List<String> parseDelete(String query) {
        List<String> parsedDelete = new ArrayList<>();

        // Convert the query to lowercase for case-insensitive matching
        String lowerQuery = query.toLowerCase();

        // Split the original query to preserve table name casing
        String[] tokens = query.trim().split("\\s+");

        String tableName = tokens[2];  // The table name is preserved with original casing
        parsedDelete.add(tableName);

        // Find the WHERE clause (case insensitive)
        if (lowerQuery.contains("where")) {
            // Split the original query to get the WHERE clause while preserving the original casing
            String whereClauseConditions = query.split("(?i)WHERE")[1].trim();
            parsedDelete.add(whereClauseConditions);
        } else {
            parsedDelete.add(null);  // No WHERE clause
        }

        return parsedDelete;
    }

    /**
     * This method splits up all the condition by "AND" / "OR"
     * @param query - String of command -> "gpa > 3.8 AND age < 20"
     * @return - A List -> [gpa > 3.8, age < 20]
     */


    /* This method returns the individual conditions (eg. "gpa > 3.8")  as long as there is a WHERE clause */
    public static List<String> parseConditions(String query) {
        List<String> conditions = new ArrayList<>();

        // Handle logical operators in a case-insensitive way, but don't affect actual values
        String logicalOperator = null;
        if (query.toLowerCase().contains("and")) logicalOperator = "AND";
        else if (query.toLowerCase().contains("or")) logicalOperator = "OR";

        if (logicalOperator != null) {
            String[] conditionParts = query.split("(?i)" + logicalOperator);  // Case-insensitive split for AND/OR
            for (String part : conditionParts) {
                conditions.add(part.trim());  // Keep the original casing for the actual data
            }
            conditions.add(logicalOperator);  // Add the logical operator itself
        } else {
            conditions.add(query.trim());  // No logical operator, just add the full condition
        }

        return conditions;
    }
}