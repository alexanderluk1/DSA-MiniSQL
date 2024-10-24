package edu.smu.smusql.enums;

public enum Messages {
    TABLE_NOT_EXIST("Error: The table does not exist."),
    TABLE_ALREADY_EXIST("Error: The table already exists."),
    SUCCESS_TABLE_CREATED("Success: The table was created successfully."),
    COLUMN_MISMATCH("Error: Number of input values does not match number of columns."),
    ADD_ERROR("Error: Unable to add values to the Database."),
    SUCCESS_ADD_RECORD("Success: The record was successfully added."),
    SUCCESS_UPDATE_RECORD("Sucess: The record is successfully updated"),
    SUCCESS_DELETE_RECORD("Sucess: The record is successfully deleted");

    private final String message;

    // Constructor for the enum
    Messages(String message) {
        this.message = message;
    }

    // Method to get the message
    public String getMessage() {
        return message;
    }
}