package edu.smu.smusql.Evaluation;

import edu.smu.smusql.enums.QueryToExecute;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import static edu.smu.smusql.Main.dbEngine;
import static edu.smu.smusql.enums.QueryToExecute.*;

public class CustomEvaluation {

    // Define counters and time trackers for each query type
    private static int insertCount = 0, selectCount = 0, updateCount = 0, deleteCount = 0;
    private static int complexSelectCount = 0, complexUpdateCount = 0, complexDeleteCount = 0;
    private static Map<QueryToExecute, Double> queryTimeMap = new HashMap<>();
    private static int idCounter = 10;

    public static void runEvaluation(int numberOfQueries) {
        System.out.println("=====================================");
        System.out.println("   Starting Evaluation with " + numberOfQueries + " Queries");
        System.out.println("=====================================");

        Random random = new Random();
        long startTime = System.nanoTime();

        createTables();
        prepopulateTables();

        // Execute n queries equally for each type of query
        int baseExecutionPerQuery = numberOfQueries / QueryToExecute.values().length;
        int remainingQueries = numberOfQueries % QueryToExecute.values().length;

        executeEqually(baseExecutionPerQuery, random);

        // Distribute the remaining queries randomly
        List<QueryToExecute> queryTypes = new ArrayList<>(List.of(QueryToExecute.values()));
        for (int i = 0; i < remainingQueries; i++) {
            QueryToExecute randomQuery = queryTypes.get(random.nextInt(queryTypes.size()));
            executeSpecificQuery(randomQuery, random);
        }

        long endTime = System.nanoTime();
        double totalElapsedTime = (endTime - startTime) / 1_000_000_000.0;

        if (numberOfQueries == 1000) { // first run
            Summary.clearSummaryCSV();
        }

        // Print the summary of actions taken
        printSummary(numberOfQueries, totalElapsedTime);
        for (QueryToExecute queryType : QueryToExecute.values()) {
            Double executionTime = queryTimeMap.getOrDefault(queryType, 0.0);
            Summary.storeTiming(queryType, executionTime, numberOfQueries);
        }
    }

    // Method to create initial tables
    private static void createTables() {
        System.out.println("\n=====================");
        System.out.println("  Creating Tables");
        System.out.println("=====================");

        System.out.println("  -> Creating STUDENT table...");
        dbEngine.executeSQL("CREATE TABLE student (id, name, age, gpa, deans_list)");
        System.out.println("  -> Creating USERS table...");
        dbEngine.executeSQL("CREATE TABLE users (id, name, age, city)");
        System.out.println("  -> Creating PRODUCTS table...");
        dbEngine.executeSQL("CREATE TABLE products (id, name, price, category)");
        System.out.println("  -> Creating ORDERS table...");
        dbEngine.executeSQL("CREATE TABLE orders (id, user_id, product_id, quantity)");

        System.out.println("  All Tables Created");
        System.out.println("=====================");
    }

    private static void executeEqually(int numberOfExecutionPerQuery, Random random) {
        int executedQueries = 0; // Track the number of queries executed

        for (QueryToExecute query : QueryToExecute.values()) {
            for (int i = 0; i < numberOfExecutionPerQuery; i++) {
                executeSpecificQuery(query, random);
                executedQueries++;

                // Print every 1,000 queries executed
                if (executedQueries % 1000 == 0) {
                    System.out.println("Executed " + executedQueries + " queries so far...");
                }
            }
        }
    }

    private static void executeSpecificQuery(QueryToExecute query, Random random) {
        long queryStartTime = System.nanoTime(); // Start timer for individual query

        switch (query) {
            case SIMPLE_INSERT:
                dbEngine.executeSQL(generateInsertQuery(random));
                insertCount++;
                break;
            case SIMPLE_SELECT:
                dbEngine.executeSQL(generateSimpleSelectQuery(random));
                selectCount++;
                break;
            case SIMPLE_UPDATE:
                dbEngine.executeSQL(generateUpdateQuery(random));
                updateCount++;
                break;
            case SIMPLE_DELETE:
                dbEngine.executeSQL(generateDeleteQuery(random));
                deleteCount++;
                break;
            case COMPLEX_SELECT:
                dbEngine.executeSQL(generateComplexSelectQuery(random));
                complexSelectCount++;
                break;
            case COMPLEX_UPDATE:
                dbEngine.executeSQL(generateComplexUpdateQuery(random));
                complexUpdateCount++;
                break;
            case COMPLEX_DELETE:
                dbEngine.executeSQL(generateComplexDeleteQuery(random));
                complexDeleteCount++;
                break;
        }

        long queryEndTime = System.nanoTime(); // End timer for individual query
        double queryElapsedTime = (queryEndTime - queryStartTime) / 1_000_000_000.0;

        // Accumulate time for this query type
        queryTimeMap.put(query, queryTimeMap.getOrDefault(query, 0.0) + queryElapsedTime);
    }

    private static void randomExecution(int numberOfQueries, Random random) {
        for (int i = 0; i < numberOfQueries; i++) {
            executeRandomQuery(random);

            // Print every 1,000 queries executed
            if ((i + 1) % 1000 == 0) {
                System.out.println("Executed " + (i + 1) + " queries so far...");
            }
        }
    }

    // Method to execute a random query (INSERT, SELECT, UPDATE, DELETE, Complex
    // SELECT, Complex UPDATE, Complex DELETE)
    private static void executeRandomQuery(Random random) {
        int queryType = random.nextInt(7); // Choose between INSERT, SELECT, UPDATE, DELETE, Complex SELECT, Complex
                                           // UPDATE, Complex DELETE

        switch (queryType) {
            case 0: // INSERT query
                dbEngine.executeSQL(generateInsertQuery(random));
                insertCount++;
                break;
            case 1: // SELECT query (simple)
                dbEngine.executeSQL(generateSimpleSelectQuery(random));
                selectCount++;
                break;
            case 2: // UPDATE query
                dbEngine.executeSQL(generateUpdateQuery(random));
                updateCount++;
                break;
            case 3: // DELETE query
                dbEngine.executeSQL(generateDeleteQuery(random));
                deleteCount++;
                break;
            case 4: // Complex SELECT query with WHERE, AND, OR, >, <, LIKE
                dbEngine.executeSQL(generateComplexSelectQuery(random));
                complexSelectCount++;
                break;
            case 5: // Complex UPDATE query with WHERE
                dbEngine.executeSQL(generateComplexUpdateQuery(random));
                complexUpdateCount++;
                break;
            case 6: // Complex DELETE query with WHERE
                dbEngine.executeSQL(generateComplexDeleteQuery(random));
                complexDeleteCount++;
                break;
        }
    }

    // Generate an INSERT query
    private static String generateInsertQuery(Random random) {
        int tableChoice = random.nextInt(4);
        return switch (tableChoice) {
            case 0 -> String.format("INSERT INTO users VALUES (%d, 'User%d', %d, 'City%d')",
                    idCounter++,
                    random.nextInt(10000),
                    random.nextInt(60) + 20,
                    random.nextInt(10));
            case 1 -> String.format("INSERT INTO products VALUES (%d, 'Product%d', %.2f, 'Category%d')",
                    idCounter++,
                    random.nextInt(1000),
                    random.nextDouble() * 1000,
                    random.nextInt(5));
            case 2 -> String.format("INSERT INTO orders VALUES (%d, %d, %d, %d)",
                    idCounter++,
                    random.nextInt(10000),
                    random.nextInt(1000),
                    random.nextInt(50));
            case 3 -> String.format("INSERT INTO student VALUES (%d, 'Student%d', %d, %.2f, %b)",
                    idCounter++, // id
                    random.nextInt(10000), // name with Student prefix
                    random.nextInt(60) + 16, // age between 16 and 75
                    random.nextDouble() * 4.0, // gpa between 0.0 and 4.0
                    random.nextBoolean());
            default -> "";
        };
    }

    // Prepopulate tables with sample data
    private static void prepopulateTables() {

        System.out.println("\n=====================");
        System.out.println("  Prepopulating Tables");
        System.out.println("=====================");
        prepopulateStudentsTable();
        prepopulateUsersTable();
        prepopulateProductsTable();
        prepopulateOrdersTable();

        System.out.println("\n=====================");
        System.out.println("  Tables have been prepopulated");
        System.out.println("=====================");
    }

    // Prepopulate Users Table
    private static void prepopulateStudentsTable() {
        System.out.println("  -> Prepopulating STUDENT table...");

        String[] insertCommands = {
                "INSERT INTO student VALUES (1, 'John', 30, 2.4, false)",
                "INSERT INTO student VALUES (2, 'Alice', 18, 3.6, true)",
                "INSERT INTO student VALUES (3, 'Bob', 19, 3.2, false)",
                "INSERT INTO student VALUES (4, 'Charlie', 21, 2.9, false)",
                "INSERT INTO student VALUES (5, 'Diana', 17, 3.9, true)",
                "INSERT INTO student VALUES (6, 'Evan', 20, 3.1, true)",
                "INSERT INTO student VALUES (7, 'Frank', 16, 3.4, false)"
        };

        for (String insertCommand : insertCommands) {
            dbEngine.executeSQL(insertCommand);
        }

        System.out.println("  -> STUDENT table prepopulated.");
    }

    // Prepopulate Users Table
    private static void prepopulateUsersTable() {
        System.out.println("  -> Prepopulating USERS table...");

        String[] insertCommands = {
                "INSERT INTO users VALUES (1, 'John', 30, 'New York')",
                "INSERT INTO users VALUES (2, 'Alice', 18, 'Los Angeles')",
                "INSERT INTO users VALUES (3, 'Bob', 25, 'Chicago')",
                "INSERT INTO users VALUES (4, 'Charlie', 40, 'Houston')",
                "INSERT INTO users VALUES (5, 'Diana', 22, 'Miami')"
        };

        for (String insertCommand : insertCommands) {
            dbEngine.executeSQL(insertCommand);
        }

        System.out.println("  -> USERS table prepopulated.");
    }

    // Prepopulate Products Table
    private static void prepopulateProductsTable() {
        System.out.println("  -> Prepopulating PRODUCTS table...");

        String[] insertCommands = {
                "INSERT INTO products VALUES (1, 'Laptop', 999.99, 'Electronics')",
                "INSERT INTO products VALUES (2, 'Smartphone', 799.99, 'Electronics')",
                "INSERT INTO products VALUES (3, 'Table', 150.50, 'Furniture')",
                "INSERT INTO products VALUES (4, 'Chair', 85.75, 'Furniture')",
                "INSERT INTO products VALUES (5, 'Headphones', 199.99, 'Electronics')"
        };

        for (String insertCommand : insertCommands) {
            dbEngine.executeSQL(insertCommand);
        }

        System.out.println("  -> PRODUCTS table prepopulated.");
    }

    // Prepopulate Orders Table
    private static void prepopulateOrdersTable() {
        System.out.println("  -> Prepopulating ORDERS table...");

        String[] insertCommands = {
                "INSERT INTO orders VALUES (1, 1, 1, 2)", // John bought 2 Laptops
                "INSERT INTO orders VALUES (2, 2, 2, 1)", // Alice bought 1 Smartphone
                "INSERT INTO orders VALUES (3, 3, 3, 1)", // Bob bought 1 Table
                "INSERT INTO orders VALUES (4, 4, 4, 4)", // Charlie bought 4 Chairs
                "INSERT INTO orders VALUES (5, 5, 5, 2)" // Diana bought 2 Headphones
        };

        for (String insertCommand : insertCommands) {
            dbEngine.executeSQL(insertCommand);
        }

        System.out.println("  -> ORDERS table prepopulated.");
    }

    // ----------------- Generate Queries Below -----------------

    // Generate a simple SELECT query
    private static String generateSimpleSelectQuery(Random random) {
        String[] tables = { "users", "products", "orders", "student" };
        return String.format("SELECT * FROM %s", tables[random.nextInt(tables.length)]);
    }

    // Generate a complex SELECT query
    private static String generateComplexSelectQuery(Random random) {
        // Define the available tables
        String[] tables = { "student", "users", "products", "orders" };

        // Randomly select a table
        String tableName = tables[random.nextInt(tables.length)];

        // Conditions specific to each table's schema
        String[] studentConditions = { "age > 20", "gpa > 3.0", "deans_list = true" };
        String[] userConditions = { "age > 30", "city = 'New York'", "age < 25" };
        String[] productConditions = { "price < 500.0", "category = 'Electronics'", "price > 100.0" };
        String[] orderConditions = { "quantity > 5", "user_id = 1", "product_id = 100", "quantity < 50" };

        // Logical operators
        String[] logicalOperators = { "AND", "OR" };

        // Select conditions and operators based on the table
        String[] selectedConditions;
        switch (tableName) {
            case "student" -> selectedConditions = studentConditions;
            case "users" -> selectedConditions = userConditions;
            case "products" -> selectedConditions = productConditions;
            case "orders" -> selectedConditions = orderConditions;
            default -> throw new IllegalStateException("Unexpected value: " + tableName);
        }

        // Randomly pick 1 or 2 conditions
        String condition1 = selectedConditions[random.nextInt(selectedConditions.length)];
        String condition2 = selectedConditions[random.nextInt(selectedConditions.length)];

        // Randomly choose a logical operator
        String logicalOperator = logicalOperators[random.nextInt(logicalOperators.length)];

        // Combine conditions with a logical operator
        String query;
        if (random.nextBoolean()) { // 50% chance to combine two conditions
            query = String.format("SELECT * FROM %s WHERE %s %s %s", tableName, condition1, logicalOperator,
                    condition2);
        } else { // Otherwise, use only one condition
            query = String.format("SELECT * FROM %s WHERE %s", tableName, condition1);
        }

        System.out.println("Generated Query: " + query); // Debugging output to see the generated query

        return query;
    }

    // Generate an UPDATE query
    private static String generateUpdateQuery(Random random) {
        String[] tables = { "student", "users", "products", "orders" };
        int tableIndex = random.nextInt(tables.length);
        String tableName = tables[tableIndex];

        // Fields to update based on the selected table
        String updateField;
        String idField = "id";
        Object newValue; // Use Object to hold either int or double for flexibility

        switch (tableName) {
            case "student" -> {
                updateField = "gpa";
                newValue = Math.round((random.nextDouble() * 4.0) * 100.0) / 100.0; // GPA between 0.00 and 4.00,
                                                                                    // rounded to 2 decimal places
            }
            case "users" -> {
                updateField = "age";
                newValue = random.nextInt(60) + 20; // Age between 20 and 80
            }
            case "products" -> {
                updateField = "price";
                newValue = (random.nextDouble() * 999.0) + 1.0; // Price between 1.00 and 1000.00
            }
            case "orders" -> {
                updateField = "quantity";
                newValue = random.nextInt(100) + 1; // Quantity between 1 and 100
            }
            default -> {
                updateField = "age";
                newValue = random.nextInt(60) + 20;
            }
        }

        int idValue = random.nextInt(5) + 1; // Generate random id to update

        // Format the update SQL query, choosing decimal format based on field type
        String updateQuery = String.format("UPDATE %s SET %s = %s WHERE %s = %d",
                tableName, updateField, newValue instanceof Double ? String.format("%.2f", newValue) : newValue,
                idField, idValue);

        // Log what is being updated
        System.out.printf("Updating table '%s': Setting '%s' to %s for record with %s = %d%n",
                tableName, updateField, newValue instanceof Double ? String.format("%.2f", newValue) : newValue,
                idField, idValue);

        return updateQuery;
    }

    // Generate a DELETE query
    private static String generateDeleteQuery(Random random) {
        String[] tables = { "student", "users", "products", "orders" };
        int tableIndex = random.nextInt(tables.length);
        String tableName = tables[tableIndex];
        String idField = "id"; // Assuming all tables have an "id" field

        int idValue = random.nextInt(1000) + 1; // Generate random id to delete
        return String.format("DELETE FROM %s WHERE %s = %d", tableName, idField, idValue);
    }

    // Generate a complex DELETE query
    private static String generateComplexDeleteQuery(Random random) {
        String[] tables = { "student", "users", "products", "orders" };
        int tableIndex = random.nextInt(tables.length);
        String tableName = tables[tableIndex];

        List<String> conditions = new ArrayList<>();

        // Generate conditions based on the selected table schema
        switch (tableName) {
            case "student":
                conditions.add(generateCondition("age", random.nextInt(10) + 18, random.nextBoolean() ? ">" : "<"));
                conditions.add(generateCondition("gpa", String.format("%.2f", 1.0 + (random.nextDouble() * 3.0)),
                        random.nextBoolean() ? ">" : "<"));
                break;
            case "users":
                conditions.add(generateCondition("age", random.nextInt(60) + 20, random.nextBoolean() ? ">" : "<"));
                conditions.add(generateCondition("city", "'" + "City" + random.nextInt(10) + "'", "="));
                break;
            case "products":
                conditions.add(generateCondition("price", String.format("%.2f", 50.0 + (random.nextDouble() * 500.0)),
                        random.nextBoolean() ? ">" : "<"));
                conditions.add(generateCondition("category", "'" + "Category" + random.nextInt(5) + "'", "="));
                break;
            case "orders":
                conditions.add(generateCondition("quantity", random.nextInt(50) + 1, random.nextBoolean() ? ">" : "<"));
                conditions.add(generateCondition("user_id", random.nextInt(10000) + 1, "=")); // Generates user_id as an
                                                                                              // integer
                break;
        }

        // Join conditions with AND
        String joinCondition = String.join(" AND ", conditions);

        return String.format("DELETE FROM %s WHERE %s", tableName, joinCondition);
    }

    private static String generateCondition(String field, Object value, String operator) {
        // Only wrap in single quotes if value is a String and is not already quoted
        if (value instanceof String && !((String) value).startsWith("'") && field.equals("category")) {
            value = "'" + value + "'";
        }
        return String.format("%s %s %s", field, operator, value);
    }

    // Generate a complex UPDATE query
    private static String generateComplexUpdateQuery(Random random) {
        String[] tables = { "users", "products" };
        int tableIndex = random.nextInt(tables.length);
        String tableName = tables[tableIndex];

        String updateField;
        String conditionField;
        String conditionOperator;
        String conditionValue;
        Object newValue; // Use Object to handle both Integer and Double values

        switch (tableName) {
            case "users":
                updateField = "age";
                conditionField = "city";
                conditionOperator = "=";
                conditionValue = "'City" + random.nextInt(10) + "'"; // Generate a random city
                newValue = random.nextInt(60) + 20; // Age as an integer between 20 and 80
                break;
            case "products":
                updateField = "price";
                conditionField = "category";
                conditionOperator = "=";
                conditionValue = "'Category" + random.nextInt(5) + "'"; // Generate a random category
                newValue = (random.nextDouble() * 999.0) + 1.0; // Price as a double between 1.0 and 1000.0
                break;
            default:
                updateField = "age";
                conditionField = "city";
                conditionOperator = "=";
                conditionValue = "'City1'";
                newValue = random.nextInt(60) + 20; // Default to age as an integer
        }

        // Format the update SQL query, ensuring price uses %.2f for double values
        return String.format("UPDATE %s SET %s = %s WHERE %s %s %s",
                tableName,
                updateField,
                newValue instanceof Double ? String.format("%.2f", newValue) : newValue,
                conditionField,
                conditionOperator,
                conditionValue);
    }

    private static void printSummary(int numberOfQueries, double totalElapsedTime) {
        String hashingAlgo = "Cuckoo Hashing w 0.4 Load Factor";
        System.out.println("\n=====================================");
        System.out.println("         Evaluation Summary          ");
        System.out.println("=====================================");
        System.out.printf("Hashing Algorithm Used: %s%n", hashingAlgo);
        System.out.printf("Total Queries Executed: %d%n", numberOfQueries);
        System.out.printf("Total Time Taken: %.6f seconds%n", totalElapsedTime);

        System.out.println("\n--- Query Execution Counts and Times ---");
        System.out.printf("Insert Queries      : %d | Time Taken: %.6f seconds%n", insertCount,
                queryTimeMap.getOrDefault(SIMPLE_INSERT, 0.0));
        System.out.printf("Simple SELECT Queries: %d | Time Taken: %.6f seconds%n", selectCount,
                queryTimeMap.getOrDefault(SIMPLE_SELECT, 0.0));
        System.out.printf("Update Queries      : %d | Time Taken: %.6f seconds%n", updateCount,
                queryTimeMap.getOrDefault(SIMPLE_UPDATE, 0.0));
        System.out.printf("Delete Queries      : %d | Time Taken: %.6f seconds%n", deleteCount,
                queryTimeMap.getOrDefault(SIMPLE_DELETE, 0.0));
        System.out.printf("Complex SELECT Queries: %d | Time Taken: %.6f seconds%n", complexSelectCount,
                queryTimeMap.getOrDefault(COMPLEX_SELECT, 0.0));
        System.out.printf("Complex UPDATE Queries: %d | Time Taken: %.6f seconds%n", complexUpdateCount,
                queryTimeMap.getOrDefault(COMPLEX_UPDATE, 0.0));
        System.out.printf("Complex DELETE Queries: %d | Time Taken: %.6f seconds%n", complexDeleteCount,
                queryTimeMap.getOrDefault(COMPLEX_DELETE, 0.0));
        System.out.println("=====================================");
    }
}