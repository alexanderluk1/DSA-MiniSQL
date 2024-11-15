package edu.smu.smusql;

import edu.smu.smusql.model.Table;
import java.util.*;
import java.util.regex.*;

public class Engine {
    private Database database;

    public Engine() {
        this.database = new Database();
    }

    public String executeSQL(String query) {
        String[] tokens = query.trim().split("\\s+", 2);
        if (tokens.length == 0) return "ERROR: Empty query";
        
        String command = tokens[0].toUpperCase();
        String rest = tokens.length > 1 ? tokens[1] : "";

        switch (command) {
            case "CREATE":
                return create(rest);
            case "INSERT":
                return insert(rest);
            case "SELECT":
                return select(rest);
            case "UPDATE":
                return update(rest);
            case "DELETE":
                return delete(rest);
            default:
                return "ERROR: Unknown command: " + command;
        }
    }

    public String create(String query) {
        // CREATE TABLE table_name (field1, field2, ...)
        Pattern pattern = Pattern.compile("TABLE\\s+(\\w+)\\s*\\((.+)\\)");
        Matcher matcher = pattern.matcher(query);
        
        if (!matcher.find()) {
            return "ERROR: Invalid CREATE syntax";
        }

        String tableName = matcher.group(1);
        String[] fields = matcher.group(2).split(",\\s*");
        List<String> fieldList = Arrays.asList(fields);

        try {
            database.createTable(tableName, fieldList);
            return "Table " + tableName + " created successfully";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public String insert(String query) {
        // INSERT INTO table_name VALUES (value1, value2, ...)
        Pattern pattern = Pattern.compile("INTO\\s+(\\w+)\\s+VALUES\\s*\\((.+)\\)");
        Matcher matcher = pattern.matcher(query);
        
        if (!matcher.find()) {
            return "ERROR: Invalid INSERT syntax";
        }

        String tableName = matcher.group(1);
        String[] valueStrings = matcher.group(2).split(",\\s*");
        
        Table table = database.getTable(tableName);
        if (table == null) {
            return "ERROR: Table " + tableName + " does not exist";
        }

        List<Object> values = new ArrayList<>();
        for (String value : valueStrings) {
            // Remove quotes from string values
            value = value.trim().replaceAll("^'|'$", "");
            
            // Convert value to appropriate type
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                values.add(Boolean.parseBoolean(value));
            } else {
                try {
                    // Try parsing as number
                    if (value.contains(".")) {
                        values.add(Double.parseDouble(value));
                    } else {
                        values.add(Integer.parseInt(value));
                    }
                } catch (NumberFormatException e) {
                    // If parsing fails, treat as string
                    values.add(value);
                }
            }
        }

        try {
            table.insertRecord(values);
            return "1 row inserted successfully";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public String select(String query) {
        // SELECT * FROM table_name [WHERE condition]
        Pattern pattern = Pattern.compile("\\*\\s+FROM\\s+(\\w+)(?:\\s+WHERE\\s+(.+))?");
        Matcher matcher = pattern.matcher(query);
        
        if (!matcher.find()) {
            return "ERROR: Invalid SELECT syntax";
        }

        String tableName = matcher.group(1);
        String condition = matcher.group(2); // May be null if no WHERE clause

        Table table = database.getTable(tableName);
        if (table == null) {
            return "ERROR: Table " + tableName + " does not exist";
        }

        try {
            List<Integer> keys;
            if (condition == null) {
                // If no condition, get all records
                keys = table.selectRecords("id >= 0");
            } else {
                keys = table.selectRecords(condition);
            }

            StringBuilder result = new StringBuilder();
            for (Integer key : keys) {
                Map<String, Object> record = table.selectRecord(key);
                result.append(record.toString()).append("\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public String update(String query) {
        // UPDATE table_name SET field = value WHERE condition
        Pattern pattern = Pattern.compile("(\\w+)\\s+SET\\s+(.+?)\\s+WHERE\\s+(.+)");
        Matcher matcher = pattern.matcher(query);
        
        if (!matcher.find()) {
            return "ERROR: Invalid UPDATE syntax";
        }

        String tableName = matcher.group(1);
        String setClause = matcher.group(2);
        String condition = matcher.group(3);

        Table table = database.getTable(tableName);
        if (table == null) {
            return "ERROR: Table " + tableName + " does not exist";
        }

        // Parse SET clause
        String[] setParts = setClause.split("=");
        if (setParts.length != 2) {
            return "ERROR: Invalid SET clause";
        }

        String field = setParts[0].trim();
        String valueStr = setParts[1].trim();
        
        // Convert value to appropriate type
        Object value;
        if (valueStr.equalsIgnoreCase("true") || valueStr.equalsIgnoreCase("false")) {
            value = Boolean.parseBoolean(valueStr);
        } else {
            try {
                if (valueStr.contains(".")) {
                    value = Double.parseDouble(valueStr);
                } else {
                    value = Integer.parseInt(valueStr);
                }
            } catch (NumberFormatException e) {
                value = valueStr.replaceAll("^'|'$", ""); // Remove quotes if present
            }
        }

        Map<String, Object> updateValues = new HashMap<>();
        updateValues.put(field, value);

        try {
            int updatedRows = table.updateRecords(condition, updateValues);
            return updatedRows + " row(s) updated successfully";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public String delete(String query) {
        // DELETE FROM table_name WHERE condition
        Pattern pattern = Pattern.compile("FROM\\s+(\\w+)\\s+WHERE\\s+(.+)");
        Matcher matcher = pattern.matcher(query);
        
        if (!matcher.find()) {
            return "ERROR: Invalid DELETE syntax";
        }

        String tableName = matcher.group(1);
        String condition = matcher.group(2);

        Table table = database.getTable(tableName);
        if (table == null) {
            return "ERROR: Table " + tableName + " does not exist";
        }

        try {
            int deletedRows = table.deleteRecords(condition);
            return deletedRows + " row(s) deleted successfully";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}