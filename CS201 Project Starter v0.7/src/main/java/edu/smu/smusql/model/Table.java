package edu.smu.smusql.model;

import edu.smu.smusql.ErrorChecks.TypeConverter;
import edu.smu.smusql.utils.PredicateUtils;
import edu.smu.smusql.utils.StringFormatter;
import edu.smu.smusql.*;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

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

        // Parse the condition(s), e.g. "gpa > 3.8 AND age < 20"
        // parsedCondition will include the logicalOperator at the BACK if more than 1 condition
        List<String> parsedCondition = Parser.parseSelectConditions(conditions);

        String logicalOperator = parsedCondition.size() > 1 ? parsedCondition.get(parsedCondition.size() - 1) : null;

        if (logicalOperator != null) {
            parsedCondition.remove(parsedCondition.size() - 1);
        }

        List<Predicate<Record>> predicates = new ArrayList<>();

        // Iterate over conditions and create predicates
        for (String condition : parsedCondition) {
            String[] words = condition.split(" ");
            String columnName = words[0];
            int columnIndex = getIndexOfColumnName(columnName);
            Predicate<Record> predicate = PredicateUtils.createPredicateForCondition(condition, columnIndex, records);
            predicates.add(predicate);
        }

        // Combine predicates with AND/OR logic if there are multiple conditions
        Predicate<Record> combinedPredicate = predicates.size() == 1
                ? predicates.get(0)
                : PredicateUtils.combinePredicates(predicates, logicalOperator);

        // Filter record using combined predicates
        for (Record record : records) {
            if (combinedPredicate.test(record)) recordsRetrieved.add(record);
        }

        return StringFormatter.formatStringForPrintout(columns, new ArrayList<>(recordsRetrieved));
    }

    public int getIndexOfColumnName(String columnName) {
        return columns.indexOf(columnName);
    }


    @Override
    public String toString() {
        return "Table{" +
                "tableName='" + tableName + '\'' +
                ", columns=" + columns +
                ", records=" + records +
                '}';
    }
}