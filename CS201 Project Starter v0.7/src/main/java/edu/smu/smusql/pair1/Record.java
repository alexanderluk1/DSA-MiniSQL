package edu.smu.smusql.pair1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Record {
    
    private Integer id;
    private HashMap<String, Object> columns; // Column name and its value

    public Record(List<String> colNames, List<Object> values) {
        this.id = (Integer) values.get(0); // Assume first co1 is id
        this.columns = new HashMap<>();

        for (int i = 1; i < colNames.size(); i++) {
            setColumn(colNames.get(i), values.get(i));
        }
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
}
