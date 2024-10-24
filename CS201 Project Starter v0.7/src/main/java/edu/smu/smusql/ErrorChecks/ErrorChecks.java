package edu.smu.smusql.ErrorChecks;

import edu.smu.smusql.model.Table2;

import java.util.List;

public class ErrorChecks {

    public ErrorChecks() {}

    public static boolean doesColumnsMatch(List<Object> parsedCommand, Table2 tableRetrieved) {
        return parsedCommand.size() == tableRetrieved.getNumberOfColumns();
    }
}
