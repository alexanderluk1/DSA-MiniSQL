package edu.smu.smusql.model;

import edu.smu.smusql.ErrorChecks.TypeConverter;
import edu.smu.smusql.utils.StringFormatter;
import edu.smu.smusql.*;

import java.util.*;
import java.util.function.BiPredicate;

/**
 * 1. Use the column name to get the index of the List
 * 2. Use the index to amend / access specific column in the record
 * 3. To search / delete it will always be O(n)
 *
 * Therefore, Iterate through outer List in records, Then access that specific index to check
 */
public class Table {
    private final String tableName;
    private final List<String> columns;
    private final List<Record> records;  // List of rows, where each Record is a row

    public Table(String tableName, List<String> columns) {
        this.tableName = tableName;
        this.columns = columns;
        this.records = new ArrayList<>();
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumns() {
        return columns;
    }

    public boolean hasColumn(String field) {
        return columns.contains(field);
    }

    public List<Record> getRow() {
        return records;
    }

    public int getNumberOfColumns() {
        return columns.size();
    }

    // Add a new record (row) to the table
    public boolean addRecordToTable(List<Object> dataToAdd) {
        if (dataToAdd.size() != columns.size()) return false;
        Record newRecord = new Record(dataToAdd);
        records.add(newRecord);
        return true;
    }

    public String retrieveAllFromTable() {
        return StringFormatter.formatStringForPrintout(columns, records);
    }

    public String retrieveWithCondition(List<String> command) {
        Set<Record> recordsRetrieved = new HashSet<>();
        String conditions = command.get(1);

        // Parse the conditions, e.g., "WHERE gpa > 3.8 AND age < 20"
        List<String> parsedConditions = Parser.parseSelectConditions(conditions);
        System.out.println(parsedConditions);

        // Map of available operators to their corresponding comparison functions
        Map<String, BiPredicate<Object, Object>> operatorMap = new HashMap<>();
        operatorMap.put(">", (a, b) -> Double.parseDouble(a.toString()) > Double.parseDouble(b.toString()));
        operatorMap.put("<", (a, b) -> Double.parseDouble(a.toString()) < Double.parseDouble(b.toString()));
        operatorMap.put("=", Object::equals);

        // 1 Condition -> WHERE gpa > 3.8
        if (parsedConditions.size() == 1) {
            // Expect to get ['gpa', '>', '3.8']
            String[] words = parsedConditions.get(0).trim().split(" ");

            String columnName = words[0];
            int columnIndex = getIndexOfColumnName(columnName);
            String operator = words[1];
            Object value = TypeConverter.parseValue(words[2]);

            BiPredicate<Object, Object> comparison = operatorMap.get(operator);

            for (Record record : records) {
                Object fieldValue = record.getField(columnIndex);
                if (comparison.test(fieldValue, value)) recordsRetrieved.add(record);
            }
        }
        // 2 Conditions
        else {
            // WHERE gpa > 3.8 AND age < 20
            // WHERE gpa > 3.8 OR age < 20
            for (String condition : parsedConditions) {

            }
        }
        return StringFormatter.formatStringForPrintout(columns, new ArrayList<>(recordsRetrieved));
    }

    public int getIndexOfColumnName(String columnName) {
        return columns.indexOf(columnName);
    }

//    public void getValuesOfSpecificColumn(String columnName) {
//        for (int i = 0; i < records.size(); i++) {
//            Record row = records.get(i);
////            Object value = row.getField(columns.indexOf(columnName));
//        }
//    }

    @Override
    public String toString() {
        return "Table{" +
                "tableName='" + tableName + '\'' +
                ", columns=" + columns +
                ", records=" + records +
                '}';
    }
}