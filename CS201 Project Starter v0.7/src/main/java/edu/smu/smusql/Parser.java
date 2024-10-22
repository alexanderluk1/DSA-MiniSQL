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
        if (query.contains("WHERE")) return parseSelectWhere(query);
        return parseBasicSelect(query);
    }

    // helper method for parseSelect
    /* This helper method adds a 'basic' flag as the first element of the list 
     * eg. ["basic", "SELECT", "*", "FROM", "student"]
    */
    private static List<String> parseBasicSelect(String query) {
        List<String> parsedSelectCommand = new ArrayList<>();
        parsedSelectCommand.add("basic");
        Collections.addAll(parsedSelectCommand, query.split(" "));

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

        Collections.addAll(parsedSelectCommand, query.split("WHERE"));

        parsedSelectCommand.replaceAll(String::trim);
        return parsedSelectCommand;
    }


    // [ tableName | columnName | newValue | null OR conditionString ]
    public static List<String> updateParser( String query ) {
        List<String> parsedUpdate = new ArrayList<>();

        // Split by tokens 
        String[] tokens = query.trim().split("\\s+");

        String tableName = tokens[1];
        parsedUpdate.add(tableName); 

        String setColumn = tokens[3];
        String setNewValue = tokens[5];
        parsedUpdate.add(setColumn);
        parsedUpdate.add(setNewValue);

        // if query contains WHERE clause 
        if ( query.contains("WHERE") ) {
            String whereClauseConditions = query.split("WHERE")[1].trim();
            // parse the conditions 
            parsedUpdate.add(whereClauseConditions);
        } else {
            parsedUpdate.add(null); // to indicate that there is not WHERE clause 
        }

        return parsedUpdate;

    }

    /**
     * This method splits up all the condition by "AND" / "OR"
     * @param query - String of command -> "gpa > 3.8 AND age < 20"
     * @return - A List -> [gpa > 3.8, age < 20]
     */


    /* This method returns the individual conditions (eg. "gpa > 3.8")  as long as there is a WHERE clause */
    public static List<String> parseConditions(String query /* after WHERE clause */ ) {
        List<String> conditions = new ArrayList<>();

        String logicalOperator = null;

        // assumption query only has one AND | OR 
        if (query.contains("AND")) logicalOperator = "AND";
        else if (query.contains("OR")) logicalOperator = "OR";

        if (logicalOperator != null) {
            String[] conditionParts = query.split(logicalOperator);

            for (String part : conditionParts) {
                conditions.add(part.trim());
            }
            conditions.add(logicalOperator);
        }
        else {
            conditions.add(query.trim());
        }

        return conditions;
    }

    // --------------- DEFAULT PARSER FROM HERE ON DOWN ---------------

    public void parseInsert(String[] tokens) {
        String tableName = tokens[2]; // The name of the table to be inserted into.
        String valueList = queryBetweenParentheses(tokens, 4); // Get values list between parentheses
        List<String> values = Arrays.asList(valueList.split(",")); // These are the values in the row to be inserted.
    }

    public void parseDelete(String[] tokens) {
        String tableName = tokens[2]; // The name of the table to be deleted from.

        List<String[]> whereClauseConditions = new ArrayList<>(); // Array for storing conditions from the where clause.

        // Parse WHERE clause conditions
        if (tokens.length > 3 && tokens[3].toUpperCase().equals("WHERE")) {
            for (int i = 4; i < tokens.length; i++) {
                if (tokens[i].toUpperCase().equals("AND") || tokens[i].toUpperCase().equals("OR")) {
                    // Add AND/OR conditions
                    whereClauseConditions.add(new String[] {tokens[i].toUpperCase(), null, null, null});
                } else if (isOperator(tokens[i])) {
                    // Add condition with operator (column, operator, value)
                    String column = tokens[i - 1];
                    String operator = tokens[i];
                    String value = tokens[i + 1];
                    whereClauseConditions.add(new String[] {null, column, operator, value});
                    i += 1; // Skip the value since it has been processed
                }
            }
        }
    }

    public void parseUpdate(String[] tokens){
        String tableName = tokens[1]; // name of the table to be updated

        String setColumn = tokens[3]; // column to be updated
        String newValue = tokens[5]; // new value for above column

        // Initialize whereClauseConditions list
        List<String[]> whereClauseConditions = new ArrayList<>();

        // Parse WHERE clause conditions
        if (tokens.length > 6 && tokens[6].equalsIgnoreCase("WHERE")) {
            for (int i = 5; i < tokens.length; i++) {
                if (tokens[i].equalsIgnoreCase("AND") || tokens[i].equalsIgnoreCase("OR")) {
                    // Add AND/OR conditions
                    whereClauseConditions.add(new String[] {tokens[i].toUpperCase(), null, null, null});
                } else if (isOperator(tokens[i])) {
                    // Add condition with operator (column, operator, value)
                    String column = tokens[i - 1];
                    String operator = tokens[i];
                    String value = tokens[i + 1];
                    whereClauseConditions.add(new String[] {null, column, operator, value});
                    i += 1; // Skip the value since it has been processed
                }
            }
        }
    }

    // Helper method to extract content inside parentheses
    private String queryBetweenParentheses(String[] tokens, int startIndex) {
        StringBuilder result = new StringBuilder();
        for (int i = startIndex; i < tokens.length; i++) {
            result.append(tokens[i]).append(" ");
        }
        return result.toString().trim().replaceAll("\\(", "").replaceAll("\\)", "");
    }

    // Helper method to determine if a string is an operator
    private boolean isOperator(String token) {
        return token.equals("=") || token.equals(">") || token.equals("<") || token.equals(">=") || token.equals("<=");
    }
}