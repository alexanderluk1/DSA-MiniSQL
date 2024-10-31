package edu.smu.smusql;

import java.util.HashMap;
import java.util.List;

import edu.smu.smusql.pair1.CuckooTable;
import edu.smu.smusql.pair1.MHCTable;
import edu.smu.smusql.pair1.Table;

/**
 * This will contain all the DBs
 */
public class Database {
//    private final HashMap<String, Table> tables;
    private final HashMap<String, CuckooTable> tables;
//    private final HashMap<String, MHCTable> tables;

    public Database() {
        tables = new HashMap<>();
    }

    public void createTable(String tableName, List<String> columns) {
//        Table table = new Table();
        CuckooTable table = new CuckooTable();
//        MHCTable table = new MHCTable();

        for (String col : columns) {
            table.addColumn(col);
        }
        tables.put(tableName, table);
    }

//    public Table getTable(String tableName) {
//        return tables.get(tableName);
//    }

//    public CuckooTable getTable(String tableName) {
//        return tables.get(tableName);
//    }

    public CuckooTable getTable(String tableName) {
        return tables.get(tableName);
    }
}
