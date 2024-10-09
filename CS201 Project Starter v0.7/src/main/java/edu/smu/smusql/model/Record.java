package edu.smu.smusql.model;

import java.util.List;

public class Record {
    private final List<Object> fields;  // This will hold the values for each column in a single record

    // Constructor that takes a list of field values for the record (row)
    public Record(List<Object> fields) {
        this.fields = fields;
    }

    // Get the value of a specific field (column) by index
    public Object getField(int columnIndex) {
        return fields.get(columnIndex);
    }

    // Update the value of a specific field (column) by index
    public void setField(int columnIndex, Object value) {
        fields.set(columnIndex, value);
    }

    // Get all field values in the record (row)
    public List<Object> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return "{" + fields + "}";
    }
}