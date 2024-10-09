package edu.smu.smusql.utils;

import edu.smu.smusql.model.Record;

import java.util.List;

public class StringFormatter {
    public StringFormatter() {};

    public static String formatStringForPrintout(List<String> headers, List<Record> records) {
        StringBuilder sb = new StringBuilder();

        // Format the Table Header separated by tabs
        for (String header : headers) {
            sb.append(header).append("\t");
        }
        sb.append("\n");
        
        // Format the records
        for (Record record : records) {
            List<Object> recordFields = record.getFields();

            for (Object field : recordFields) {
                sb.append(field.toString()).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
