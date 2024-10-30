package edu.smu.smusql.ErrorChecks;

import java.util.ArrayList;
import java.util.List;

public class TypeConverter {

    public TypeConverter() {}

    public static List<Object> convertParams(List<String> params) {
        List<Object> convertedParameters = new ArrayList<>();

        for (String param : params) {
            convertedParameters.add(parseValue(param));
        }

        return convertedParameters;
    }

    public static Object parseValue(String value) {
        // Step 1: Check if it's a boolean
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(value);
        }

        // Step 2: Try to parse as a number
        try {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            } else {
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            // Not an integer, move on to the next check
        }

        // Step 4: Treat as a string (remove any extra quotes around strings)
        return value.replace("\"", "").replace("'", "");
    }
}