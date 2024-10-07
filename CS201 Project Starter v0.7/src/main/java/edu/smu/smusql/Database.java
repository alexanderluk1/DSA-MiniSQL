package edu.smu.smusql;

import edu.smu.smusql.model.Table;

import java.util.HashMap;
import java.util.List;

/**
 * This will contain all the Tables
 */
public class Database {
    private final HashMap<String, Table> tables;

    public Database() {
        tables = new HashMap<>();
    }

    public boolean createTable (String tableName, List<String> tableFields) {
        if (tables.containsKey(tableName)) return false;

        // Create a new table to add to HashMap
        Table newTable = new Table(tableName, tableFields);
        tables.put(tableName, newTable);

        return true;
    }

    public Table retrieveSpecificTable(String tableName) {
        return tables.get(tableName);
    }
}
