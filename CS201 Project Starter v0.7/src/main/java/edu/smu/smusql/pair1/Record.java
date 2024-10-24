package edu.smu.smusql.pair1;

import java.util.HashMap;
import java.util.List;

public class Record {

    private Integer id;
    private HashMap<String, Object> columns; // Column name and its value

    public Record(List<String> columnNames, List<Object> values) {
        this.id = (Integer) values.get(0); // Assume first co1 is id
        this.columns = new HashMap<>();

        for (int i = 1; i < columnNames.size(); i++) {
            setColumnValue(columnNames.get(i), values.get(i));
        }
    }

    public Integer getId() {
        return id;
    }

    public void setColumnValue(String columnName, Object value) {
        columns.put(columnName, value);
    }

    public Object getColumnValue(String columnName) {
        return columns.get(columnName);
    }

    public String toString(List<String> columnOrder) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("| %-10d |", id));
        for (int i = 1; i < columnOrder.size(); i++) {
            sb.append(String.format(" %-20s |", getColumnValue(columnOrder.get(i))));
        }
        sb.append("\n");
        return sb.toString();
    }
}
