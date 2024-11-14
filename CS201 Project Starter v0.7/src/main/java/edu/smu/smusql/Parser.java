package edu.smu.smusql;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    /**
     * Parses a CREATE TABLE command.
     * 
     * @param tokens The tokens of the command.
     * @return A list containing the table name and column definitions.
     */
    public static List<String> parseCreate(String[] tokens) {
        if (!tokens[1].equalsIgnoreCase("TABLE")) {
            throw new IllegalArgumentException("Invalid CREATE TABLE syntax");
        }

        List<String> parsedCommand = new ArrayList<>();
        parsedCommand.add(tokens[2]); // Table name

        String columnsDefinition = String.join(" ", Arrays.copyOfRange(tokens, 3, tokens.length));
        if (!columnsDefinition.startsWith("(") || !columnsDefinition.endsWith(")")) {
            throw new IllegalArgumentException("Invalid syntax for column definitions in CREATE TABLE");
        }

        String columnsPart = columnsDefinition.substring(1, columnsDefinition.length() - 1);
        parsedCommand.addAll(Arrays.asList(columnsPart.split("\\s*,\\s*")));

        return removeDataTypes(parsedCommand);
    }

    /**
     * Parses an INSERT command.
     * 
     * @param tokens The tokens of the command.
     * @return A list containing the table name and values.
     */
    public static List<Object> parseInsert(String[] tokens) {
        if (tokens.length < 5 || !tokens[1].equalsIgnoreCase("INTO") || !tokens[3].equalsIgnoreCase("VALUES")) {
            throw new IllegalArgumentException("Invalid INSERT statement.");
        }

        String tableName = tokens[2]; // Table name
        String valueList = queryBetweenParentheses(tokens, 4); // Extract values

        List<Object> parsedCommand = new ArrayList<>();
        parsedCommand.add(tableName);
        parsedCommand.addAll(Arrays.asList(valueList.split(",\\s*")));
        return parsedCommand;
    }

    /**
     * Parses a SELECT command with conditions.
     * 
     * @param query The SELECT command string.
     * @return A list containing the table name, conditions, and operators.
     */
    public static List<Object> parseSelectConditions(String query) {
        String[] tokens = query.split("\\s+");
        List<Object> parsedCommand = new ArrayList<>();

        parsedCommand.add(tokens[3]); // Table name
        if (query.contains("WHERE")) {
            int whereIndex = Arrays.asList(tokens).indexOf("WHERE");
            List<String> conditions = new ArrayList<>();

            for (int i = whereIndex + 1; i < tokens.length; i++) {
                conditions.add(tokens[i]);
            }
            parsedCommand.add(conditions);
        }
        
        return parsedCommand;
    }

    public static List<String> removeDataTypes(List<String> columnDefinitions) {
        List<String> columnsWithoutTypes = new ArrayList<>();

        // Regular expression to match column names
        String regex = "(\\w+)";
        Pattern pattern = Pattern.compile(regex);

        for (String definition : columnDefinitions) {
            Matcher matcher = pattern.matcher(definition);
            if (matcher.find()) {
                // Add only the column name (first part before the type)
                columnsWithoutTypes.add(matcher.group(1));
            }
        }

        return columnsWithoutTypes;
    }

    /**
     * Parses an UPDATE command.
     * 
     * @param command The UPDATE command string.
     * @return A list containing the table name, updated values, and conditions.
     */
    public static List<Object> parseUpdate(String command) {
        // Remove the "UPDATE" keyword and trim any extra whitespace
        command = command.replaceFirst("(?i)^UPDATE\\s+", "").trim();

        // Example command: "UPDATE student SET age = 25 WHERE id = 1"
        String[] parts = command.split(" SET | WHERE ");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid UPDATE command syntax.");
        }

        String tableName = parts[0].trim(); // Get table name
        String setClause = parts[1].trim(); // Get set clause
        String conditionClause = parts[2].trim(); // Get condition clause

        // Parse the SET clause into a map of column -> new value
        Map<String, Object> updatedValues = new HashMap<>();
        String[] updates = setClause.split(",");

        for (String update : updates) {
            String[] keyValue = update.split("=");
            String key = keyValue[0].trim();
            Object value = keyValue[1].trim().replace("'", ""); // Remove quotes
            updatedValues.put(key, value);
        }

        // Return a list with the table name, updated values map, and condition string
        return Arrays.asList(tableName, updatedValues, conditionClause);
    }

    /**
     * Parses a DELETE command.
     * 
     * @param query The DELETE command string.
     * @return A list containing the table name and conditions.
     */
    public static List<String> parseDelete(String query) {
        // Normalize spaces and check for proper syntax
        query = query.trim();
        if (!query.startsWith("DELETE FROM")) {
            throw new IllegalArgumentException("Invalid DELETE command syntax.");
        }
    
        // Split the query by the WHERE clause
        String[] tokens = query.split("WHERE");
        String tableName = tokens[0].trim().split(" ")[2]; // Extract the table name
    
        // Prepare the result list
        List<String> parsedDeleteCommand = new ArrayList<>();
        parsedDeleteCommand.add(tableName);
    
        // If there is a WHERE condition, add it to the list
        if (tokens.length > 1) {
            parsedDeleteCommand.add(tokens[1].trim());
        } else {
            parsedDeleteCommand.add(""); // Add empty string if no condition exists
        }

        return parsedDeleteCommand;
    }
    
    /**
     * Extracts the content between parentheses in the INSERT statement.
     * 
     * @param tokens The tokens of the command.
     * @param startIndex The starting index of the values.
     * @return A string containing the values between parentheses.
     */
    private static String queryBetweenParentheses(String[] tokens, int startIndex) {
        StringBuilder result = new StringBuilder();
        boolean openParen = false;

        for (int i = startIndex; i < tokens.length; i++) {
            if (tokens[i].contains("(")) {
                openParen = true;
                result.append(tokens[i].replace("(", "")).append(" ");
            } else if (tokens[i].contains(")")) {
                result.append(tokens[i].replace(")", ""));
                break;
            } else if (openParen) {
                result.append(tokens[i]).append(" ");
            }
        }
        return result.toString().trim();
    }
}
