package edu.smu.smusql.ErrorChecks;

import java.util.ArrayList;
import java.util.List;

public class TypeConverter {

    public TypeConverter() {}

    public static List<Object> convertParams(List<Object> params) {
        List<Object> convertedParameters = new ArrayList<>();

        for (Object param : params) {
            convertedParameters.add(parseValue((String) param));
        }

        return convertedParameters;
    }

    public static Object parseValue(String value) {
        // Step 1: Check if it's a boolean
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(value);
        }

        // Step 2: Check if it's an integer
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // Not an integer, move on to the next check
        }

        // Step 3: Check if it's a double
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            // Not a double, move on to treat as string
        }

        // Step 4: Treat as a string (remove any extra quotes around strings)
        return value.replace("\"", "").replace("'", "");
    }
}
