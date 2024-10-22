package edu.smu.smusql.pair1;

import java.util.HashMap;
import java.util.Map;

public class Record {
    
    private Integer id;
    private HashMap<String, Object> columns; // Column name and its value

    public Record(Integer id) {
        this.id = id;
        this.columns = new HashMap<>();
    }
    
    public Integer getId() {
        return id;
    }

    public void setColumn(String columnName, Object value){
        columns.put(columnName, value);
    }

    public Object getColumn(String columnName) {
        return columns.get(columnName);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("| %-10d |", id));

        // Add each column's value to the string
        for (Map.Entry<String, Object> entry : columns.entrySet()) {
            sb.append(String.format(" %-20s |", entry.getValue()));
        }

        return sb.toString();
    }

    public static void printHeader(Record record) {
        System.out.print("| ID         |");

        // Print the dynamic column names based on the record
        for (String columnName : record.columns.keySet()) {
            System.out.printf(" %-20s |", columnName);
        }
        
        System.out.println();
        System.out.println("|------------|---------------------|");
    }
}
