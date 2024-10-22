package edu.smu.smusql;

import java.util.HashMap;
import java.util.List;

import edu.smu.smusql.pair1.Table;

/**
 * This will contain all the DBs
 */
public class Database {
    private final HashMap<String, Table> tables;

    public Database() {
        tables = new HashMap<>();
    }

    public void createTable(String tableName, List<String> columns) {
        Table table = new Table(tableName);
        for (String col : columns) {
            table.addColumn(col);
        }
        table.print();
        tables.put(tableName, table);
    }

    public Table getTable(String tableName) {
        return tables.get(tableName);
    }
}
