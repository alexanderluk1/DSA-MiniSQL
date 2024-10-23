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

    public List<Integer> retrieveIds() {
        List<Integer> ids = new ArrayList<>();

        // Get the index of the 'id' column (assuming it's always present)
        int idIndex = getIndexOfColumnName("id");

        if (idIndex == -1) {
            System.out.println("Table does not contain an 'id' column.");
            return ids;
        }

        // Iterate through each record and collect the 'id' field
        for (Record record : records) {
            Object idValue = record.getField(idIndex);
            if (idValue instanceof Integer) {
                ids.add((Integer) idValue); // Assuming the 'id' is always an Integer
            } else {
                System.out.println("Invalid id value found: " + idValue);
            }
        }

        return ids;
    }

    public String retrieveAllFromTable() {
        return StringFormatter.formatStringForPrintout(columns, records);
    }

    public String retrieveWithCondition(List<String> command) { // eg. of command : ["SELECT * FROM student", "gpa > 3.8"]
        Set<Record> recordsRetrieved = new HashSet<>();
        String conditions = command.get(1); // get the command after the WHERE clause 

        // Parse the condition(s), e.g. "gpa > 3.8 AND age < 20"
        // parsedCondition will include the logicalOperator at the BACK if more than 1 condition
        List<String> parsedCondition = Parser.parseConditions(conditions);

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

    public int updateRows(String col, String newValue, String whereClauseConditions) {
        int updatedRowCount = 0;

        int colIndex = getIndexOfColumnName(col);
        List<String> parsedConditions = Parser.parseConditions(whereClauseConditions);

        String logicalOperator = parsedConditions.size() > 1 ? parsedConditions.get(parsedConditions.size() - 1) : null;
        if (logicalOperator != null) {
            parsedConditions.remove(parsedConditions.size() - 1); // Remove the logical operator from conditions
        }

        List<Predicate<Record>> predicates = new ArrayList<>();

        // Iterate over parsed conditions and create predicates for each condition
        for (String condition : parsedConditions) {
            String[] conditionParts = condition.split(" ");
            String conditionColumn = conditionParts[0];  // Column name in condition
            int conditionColIndex = getIndexOfColumnName(conditionColumn); // Index of column in condition

            // Create the predicate for this condition
            Predicate<Record> predicate = PredicateUtils.createPredicateForCondition(condition, conditionColIndex, records);
            predicates.add(predicate);
        }

        // Combine the predicates (if multiple) using AND/OR logic
        Predicate<Record> combinedPredicate = predicates.size() == 1
                ? predicates.get(0)
                : PredicateUtils.combinePredicates(predicates, logicalOperator);

        // Loop through the records, apply the combined predicate, and update the rows that match
        for (Record record : records) {
            if (combinedPredicate.test(record)) {
                // Update the field at colIndex with newValue
                record.setField(colIndex, newValue);
                updatedRowCount++;
            }
        }

        return updatedRowCount;  // Return the number of updated rows
    }

    public int deleteRows(String whereClauseConditions) {
        int deletedRowCount = 0;
    
        // Parse the conditions
        List<String> parsedConditions = Parser.parseConditions(whereClauseConditions);
    
        String logicalOperator = parsedConditions.size() > 1 ? parsedConditions.get(parsedConditions.size() - 1) : null;
        if (logicalOperator != null) {
            parsedConditions.remove(parsedConditions.size() - 1); // Remove the logical operator from conditions
        }
    
        List<Predicate<Record>> predicates = new ArrayList<>();
    
        // Create predicates for each condition
        for (String condition : parsedConditions) {
            String[] conditionParts = condition.split(" ");
            String conditionColumn = conditionParts[0];  // Column name in the condition
            int conditionColIndex = getIndexOfColumnName(conditionColumn); // Index of column in condition
    
            // Create the predicate for this condition
            Predicate<Record> predicate = PredicateUtils.createPredicateForCondition(condition, conditionColIndex, records);
            predicates.add(predicate);
        }
    
        // Combine the predicates (if multiple) using AND/OR logic
        Predicate<Record> combinedPredicate = predicates.size() == 1
                ? predicates.get(0)
                : PredicateUtils.combinePredicates(predicates, logicalOperator);
    
        // Loop through the records, apply the combined predicate, and delete the rows that match
        Iterator<Record> iterator = records.iterator();
        while (iterator.hasNext()) {
            Record record = iterator.next();
            if (combinedPredicate.test(record)) {
                iterator.remove();  // Remove the matching record
                deletedRowCount++;
            }
        }
    
        return deletedRowCount;  // Return the number of deleted rows
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