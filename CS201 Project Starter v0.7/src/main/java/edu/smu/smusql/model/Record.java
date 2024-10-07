package edu.smu.smusql.model;

import java.util.List;

/**
 * This is One Record
 */
public class Record {
    /**
     * Example: (id, name, age, gpa, deans_list)
     * [String id, String name, int age, double gpa, boolean deans_list]
     */
    private final List<Object> values; // Holds the row data

    public Record(List<Object> values) {
        this.values = values;
    }

    public List<Object> getEntireRecord() {
        return values;
    }

    public Object getValueForSpecificColumn (int columnIndex) {
        return values.get(columnIndex);  // Retrieve value by index
    }

    public void setValueAtSpecificColumn (int columnIndex, Object newValue) {
        values.set(columnIndex, newValue);  // Modify value at specific index
    }

    @Override
    public String toString() {
        return values.toString();
    }
}