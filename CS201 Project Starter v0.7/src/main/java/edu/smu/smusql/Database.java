package edu.smu.smusql;

import edu.smu.smusql.model.Table2;

import java.util.HashMap;
import java.util.List;
import java.util.*;

/**
 * This will contain all the Tables
 */
public class Database {
    private final HashMap<String, Table2> tables;

    public Database() {
        tables = new HashMap<>();
    }

    public void createTable (String tableName, List<String> tableFields) {
        // Create a new table to add to HashMap
        Table2 newTable = new Table2(tableName, tableFields, tableFields.toArray().length);
        tables.put(tableName, newTable);
    }

    public Table2 getTable(String tableName) {
        return tables.get(tableName);
    }

    public boolean doesTableExist(String tableName) {
        return tables.containsKey(tableName);
    }
}
