package edu.smu.smusql;

import edu.smu.smusql.ErrorChecks.TypeConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.*;

public class Parser {

    public Parser() {}

    // Parse CREATE TABLE command
    public static List<String> parseCreate(String query) {
        int startIndex = query.indexOf('(');
        int endIndex = query.indexOf(')');

        List<String> parsedCreateCommand = new ArrayList<>();

        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            String parameters = query.substring(startIndex + 1, endIndex);
            Collections.addAll(parsedCreateCommand, parameters.split("\\s*,\\s*"));
        } else {
            throw new IllegalArgumentException("Invalid CREATE TABLE syntax");
        }

        return parsedCreateCommand;
    }

    // Parse INSERT command
    public static List<Object> parseInsert(String[] tokens) {
        if (tokens.length < 5 || !tokens[3].equalsIgnoreCase("VALUES")) {
            throw new IllegalArgumentException("Invalid INSERT statement.");
        }

        String valueList = queryBetweenParentheses(tokens, 4);
        List<String> values = Arrays.asList(valueList.split(","));
        List<Object> formattedValues = new ArrayList<>();

        for (String value : values) {
            formattedValues.add(TypeConverter.parseValue(value.trim()));
        }

        return formattedValues;
    }

    // Parse SELECT conditions
    public static List<String> parseSelectConditions(String query) {
        List<String> parsedConditions = new ArrayList<>();
        String logicalOperator = null;

        if (query.contains("AND")) {
            logicalOperator = "AND";
        } else if (query.contains("OR")) {
            logicalOperator = "OR";
        }

        if (logicalOperator != null) {
            String[] conditions = query.split(" " + logicalOperator + " ");
            for (String condition : conditions) {
                parsedConditions.add(condition.trim());
            }
            parsedConditions.add(logicalOperator);
        } else {
            parsedConditions.add(query.trim());
        }

        return parsedConditions;
    }

    // Parse UPDATE command
    public static List<String> parseUpdate(String query) {
        String[] tokens = query.split("SET");
        String tableName = tokens[0].trim().split(" ")[1];
        String[] setClause = tokens[1].trim().split("WHERE");
        String setValues = setClause[0].trim();
        List<String> parsedUpdateCommand = new ArrayList<>();
        parsedUpdateCommand.add(tableName);
        Collections.addAll(parsedUpdateCommand, setValues.split(",\\s*"));

        if (setClause.length > 1) {
            String conditions = setClause[1].trim();
            parsedUpdateCommand.add(conditions);
        }
        return parsedUpdateCommand;
    }

    // Parse DELETE command
    public static List<String> parseDelete(String query) {
        String[] tokens = query.split("WHERE");
        String tableName = tokens[0].trim().split(" ")[2];
        List<String> parsedDeleteCommand = new ArrayList<>();
        parsedDeleteCommand.add(tableName);

        if (tokens.length > 1) {
            String conditions = tokens[1].trim();
            parsedDeleteCommand.add(conditions);
        }
        return parsedDeleteCommand;
    }

    // Helper method to extract content inside parentheses
    private static String queryBetweenParentheses(String[] tokens, int startIndex) {
        StringBuilder result = new StringBuilder();
        boolean openParen = false;

        for (int i = startIndex; i < tokens.length; i++) {
            if (tokens[i].contains("(")) {
                openParen = true;
                result.append(tokens[i].replace("(", "")).append(" ");
            } else if (tokens[i].contains(")")) {
                result.append(tokens[i].replace(")", ""));
                break; // Stop after reaching the closing parenthesis
            } else if (openParen) {
                result.append(tokens[i]).append(" ");
            }
        }

        return result.toString().trim();
    }
}
