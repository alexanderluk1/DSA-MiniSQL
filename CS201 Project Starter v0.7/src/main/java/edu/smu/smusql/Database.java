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

    public void createTable (String tableName, List<String> tableFields) {
        // Create a new table to add to HashMap
        Table newTable = new Table(tableName, tableFields);
        tables.put(tableName, newTable);
    }

    public Table getTable(String tableName) {
        return tables.get(tableName);
    }

    public boolean doesTableExist(String tableName) {
        return tables.containsKey(tableName);
    }

    public void showAllTables() {
        for (String tableName : tables.keySet()) {
            System.out.print(tableName + " ");
        }
    }
}
