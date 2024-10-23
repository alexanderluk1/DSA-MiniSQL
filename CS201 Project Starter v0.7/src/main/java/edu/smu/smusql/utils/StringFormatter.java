package edu.smu.smusql.utils;

import edu.smu.smusql.model.Record;

import java.util.ArrayList;
import java.util.List;

public class StringFormatter {
    public StringFormatter() {}

    public static String formatStringForPrintout(List<String> headers, List<Record> records) {
        StringBuilder sb = new StringBuilder();

        // Determine the maximum width for each column based on headers and record values
        List<Integer> columnWidths = new ArrayList<>();

        // Initialize columnWidths with the header lengths
        for (String header : headers) {
            columnWidths.add(header.length());
        }

        // Calculate the maximum width for each column by comparing with record values
        for (Record record : records) {
            List<Object> fields = record.getFields();
            for (int i = 0; i < fields.size(); i++) {
                int fieldLength = fields.get(i).toString().length();
                if (fieldLength > columnWidths.get(i)) {
                    columnWidths.set(i, fieldLength);
                }
            }
        }

        // Format the Table Header, using padded spacing
        for (int i = 0; i < headers.size(); i++) {
            sb.append(String.format("%-" + columnWidths.get(i) + "s", headers.get(i))).append("   ");
        }
        sb.append("\n");

        // Format the records, using the same padded spacing for each column
        for (Record record : records) {
            List<Object> recordFields = record.getFields();
            for (int i = 0; i < recordFields.size(); i++) {
                sb.append(String.format("%-" + columnWidths.get(i) + "s", recordFields.get(i).toString())).append("   ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}