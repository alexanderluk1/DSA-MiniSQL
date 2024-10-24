package edu.smu.smusql;

import edu.smu.smusql.ErrorChecks.ErrorChecks;
import edu.smu.smusql.ErrorChecks.TypeConverter;
import edu.smu.smusql.enums.Messages;
import edu.smu.smusql.model.Table2;
import edu.smu.smusql.Parser;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Engine {
    private Database db = new Database();

    public String executeSQL(String query) {
        String[] tokens = query.trim().split("\\s+", 2); // Split command and the rest of the query
        String command = tokens[0].toUpperCase();

        return switch (command) {
            case "CREATE" -> create(tokens[1]);
            case "INSERT" -> insert(tokens);
            case "SELECT" -> select(tokens[1]);
            case "UPDATE" -> update(tokens[1]);
            case "DELETE" -> delete(tokens[1]);
            default -> "ERROR: Unknown command";
        };
    }

    public String create(String query) {
        List<String> parsedCommand = Parser.parseCreate(query);
        String tableName = parsedCommand.get(0);

        if (db.doesTableExist(tableName)) return Messages.TABLE_ALREADY_EXIST.getMessage();
        db.createTable(tableName, parsedCommand.subList(1, parsedCommand.size()));
        return Messages.SUCCESS_TABLE_CREATED.getMessage();
    }

    public String insert(String[] query) {
        List<Object> parsedCommand = Parser.parseInsert(query);
        String tableName = (String) parsedCommand.get(0);

        if (!db.doesTableExist(tableName)) return Messages.TABLE_NOT_EXIST.getMessage();
        if (!ErrorChecks.doesColumnsMatch(parsedCommand.subList(1, parsedCommand.size()), db.getTable(tableName))) {
            return Messages.COLUMN_MISMATCH.getMessage();
        }

        List<Object> convertedParameters = TypeConverter.convertParams(parsedCommand.subList(1, parsedCommand.size()));
        Table2 tableToAdd = db.getTable(tableName);

        if (!tableToAdd.insertRecord(convertedParameters)) return Messages.ADD_ERROR.getMessage();
        return Messages.SUCCESS_ADD_RECORD.getMessage();
    }

    public String select(String query) {
        List<String> parsedCommand = Parser.parseSelectConditions(query);
        String tableName = parsedCommand.get(0);

        if (!db.doesTableExist(tableName)) return Messages.TABLE_NOT_EXIST.getMessage();

        Table2 tableToSelectFrom = db.getTable(tableName);

        if (parsedCommand.size() == 1 && parsedCommand.get(0).equals("*")) {
            tableToSelectFrom.displayTableInfo();
            return "Table Information Displayed";
        } else {
            // Assume conditions are present
            String conditions = String.join(" ", parsedCommand.subList(1, parsedCommand.size()));
//            List<Map<String, Object>> results = tableToSelectFrom.selectRecords(conditions);
//            return formatSelectResults(results);
            return "HI";
        }
    }

    public String update(String query) {
        List<String> parsedCommand = Parser.parseUpdate(query);
        String tableName = parsedCommand.get(0);

        if (!db.doesTableExist(tableName)) return Messages.TABLE_NOT_EXIST.getMessage();

        // Extract key and updated values
        int keyToUpdate = Integer.parseInt(parsedCommand.get(1)); // Assuming the key is in the second position
        List<Object> updatedValues = TypeConverter.convertParams(Collections.singletonList(parsedCommand.subList(2, parsedCommand.size())));

        Table2 tableToUpdate = db.getTable(tableName);
//        if (!tableToUpdate.updateRecord(String.valueOf(keyToUpdate), updatedValues)) {
//            return Messages.ADD_ERROR.getMessage();
//        }
        return Messages.SUCCESS_UPDATE_RECORD.getMessage();
    }

    public String delete(String query) {
        List<String> parsedCommand = Parser.parseDelete(query);
        String tableName = parsedCommand.get(0);

        if (!db.doesTableExist(tableName)) return Messages.TABLE_NOT_EXIST.getMessage();

        int keyToDelete = Integer.parseInt(parsedCommand.get(1)); // Assuming the key is specified
        Table2 tableToDeleteFrom = db.getTable(tableName);
//        tableToDeleteFrom.deleteRecord(keyToDelete);
        return Messages.SUCCESS_DELETE_RECORD.getMessage();
    }

    private String formatSelectResults(List<Map<String, Object>> results) {
        StringBuilder formattedResults = new StringBuilder();
        for (Map<String, Object> record : results) {
            formattedResults.append(record.toString()).append("\n");
        }
        return formattedResults.toString();
    }
}
