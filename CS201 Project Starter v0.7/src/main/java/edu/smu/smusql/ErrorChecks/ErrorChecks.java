package edu.smu.smusql.ErrorChecks;

import edu.smu.smusql.model.Table;

import java.util.List;

public class ErrorChecks {

    public ErrorChecks() {}

    public static boolean doesColumnsMatch(List<String> parsedCommand, Table tableRetrieved) {
        return parsedCommand.size() == tableRetrieved.getNumberOfColumns();
    }
}
