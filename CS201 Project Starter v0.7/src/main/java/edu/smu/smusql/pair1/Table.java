package edu.smu.smusql.pair1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.smu.smusql.Parser;

public class Table {
    private String tableName;
    private HashTable records; // Stores the records - Can use HashMap???
    private HashMap<String, AVLTree<Object>> columns; // Column name and Tree
    private List<String> columnOrder; // maintain order of col

    public Table(String tableName) {
        this.tableName = tableName;
        this.records = new HashTable(1); 
        this.columns = new HashMap<>();
        this.columnOrder = new ArrayList<>();
    }

    public void addColumn(String columnName) {
        columns.put(columnName, new AVLTree<>());
        columnOrder.add(columnName);
    }

    public void insertRecord(List<Object> values) {
        Record newRecord = new Record(columnOrder, values); // Match Col to Value
        int id = (Integer) values.get(0);

        for (int i = 1; i < columnOrder.size(); i++) { // Add to AVL Tree
            columns.get(columnOrder.get(i)).insert(values.get(i), id); // Add to all Trees
        }
        
        records.put(newRecord); // Add to HashTable
    }

    public void deleteRecords(List<Integer> list) {
        for (Integer id : list) {
            records.remove(id);
        }
    }

    public void updateRecord(List<Integer> list) {
        // TO DO
    }

    public String getTableName() {
        return tableName;
    }

    public HashTable getRecords() {
        return records;
    }

    public String getAll() {
        StringBuilder sb = new StringBuilder();
        sb.append(printHeader(columnOrder)); // Get headers
    
        // Iterate through each bucket in the HashTable
        for (int i = 0; i < records.getSize(); i++) {
            BucketNode current = records.getStartOfBucket(i); // Get the start of the bucket
            while (current != null) { // Iterate through the linked list in the bucket
                Record record = current.getRecord(); // Get the record from the current node
                sb.append(String.format("| %-10d |", record.getId())); // Print the ID
                for (String colName : columnOrder) {
                    if (colName.equals("id")) {
                        continue; // Skip id column
                    }
                    sb.append(String.format(" %-20s |", record.getColumn(colName))); // Print other columns
                }
                sb.append("\n"); // New line for the next record
                current = current.getNext(); // Move to the next node
            }
        }
        return sb.toString();
    }

    public List<Integer> getWithCondition(List<String> command) {
        String conditions = command.get(1);
        // Parse the condition(s), e.g. "gpa > 3.8 AND age < 20"
        // parsedCondition will include the logicalOperator at the BACK if more than 1 condition
        List<String> parsedCondition = Parser.parseSelectConditions(conditions);

        String logicalOperator = parsedCondition.size() > 1 ? parsedCondition.get(parsedCondition.size() - 1) : null;

        if (logicalOperator != null) {
            parsedCondition.remove(parsedCondition.size() - 1);
        }

        List<Integer> listOfId = new ArrayList<>();
        List<Set<Integer>> resultSet = new ArrayList<>();
        // Iterate over conditions and create list of ID according to condition
        for (String condition : parsedCondition) {
            String[] words = condition.split(" ");
            String columnName = words[0];
            AVLTree<Object> tree = columns.get(columnName);
            // resultSet.add(tree.findWithCondition(condition));
        } 

        // Combine lists with AND/OR logic
        // for (Set<Integer> set : resultSet) {
        //     set.
        // }

        // for (Integer id : listOfId) {
        //     recordsRetrieved.add(records.get(id));
        // }

        return listOfId;
    }

    private String printHeader(List<String> columnNames) {
        StringBuilder sb = new StringBuilder();
        sb.append("| ID         |");
    
        // Print the dynamic column names based on the ordered column names
        for (String columnName : columnNames) {
            if (columnName.equals("id")) {
                continue; // Skip id column
            }
            sb.append(String.format(" %-20s |", columnName));
        }
        sb.append("\n|------------|");
        for (int i = 1; i < columnNames.size(); i++) {
            sb.append("----------------------|");
        }
        sb.append("\n");
    
        return sb.toString();
    }
    

    public String formatRecords(List<Integer> subset) {
        StringBuilder sb = new StringBuilder();
    
        sb.append(printHeader(columnOrder)); // Get headers
    
        for (Integer id : subset) { // Iterate through the subset
            Record record = records.get(id);
            sb.append(String.format("| %-10d |", record.getId()));
            for (String colName : columnOrder) {
                if (colName.equals("id")) {
                    continue; // Skip id column
                }
                sb.append(String.format(" %-20s |", record.getColumn(colName)));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
