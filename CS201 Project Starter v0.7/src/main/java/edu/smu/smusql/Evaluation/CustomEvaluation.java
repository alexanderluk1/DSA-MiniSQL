package edu.smu.smusql.Evaluation;

import java.util.Random;
import static edu.smu.smusql.Main.dbEngine;

public class CustomEvaluation {

    // Define counters for each query type
    private static int insertCount = 0;
    private static int selectCount = 0;
    private static int updateCount = 0;
    private static int deleteCount = 0;
    private static int complexSelectCount = 0;
    private static int complexUpdateCount = 0;
    private static int complexDeleteCount = 0;

    public static void runEvaluation(int numberOfQueries) {
        System.out.println("Starting evaluation with " + numberOfQueries + " queries...");


        Random random = new Random();
        long startTime = System.nanoTime();

        createTables();
        prepopulateTables();

        for (int i = 0; i < numberOfQueries; i++) {
            executeRandomQuery(random);

            // Print progress every 10,000 queries
            if (i % 10_000 == 0 && i > 0) {
                System.out.println("Processed " + i + " queries...");
            }
        }

        long endTime = System.nanoTime();
        double elapsedTime = (endTime - startTime) / 1_000_000_000.0;

        // Print the summary of actions taken
        printSummary(numberOfQueries, elapsedTime);
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

    // Method to execute a random query (INSERT, SELECT, UPDATE, DELETE, Complex SELECT, Complex UPDATE, Complex DELETE)
    private static void executeRandomQuery(Random random) {
        int queryType = random.nextInt(7); // Choose between INSERT, SELECT, UPDATE, DELETE, Complex SELECT, Complex UPDATE, Complex DELETE

        switch (queryType) {
            case 0:  // INSERT query
                dbEngine.executeSQL(generateInsertQuery(random));
                insertCount++;
                break;
            case 1:  // SELECT query (simple)
                dbEngine.executeSQL(generateSimpleSelectQuery(random));
                selectCount++;
                break;
            case 2:  // UPDATE query
                dbEngine.executeSQL(generateUpdateQuery(random));
                updateCount++;
                break;
            case 3:  // DELETE query
                dbEngine.executeSQL(generateDeleteQuery(random));
                deleteCount++;
                break;
            case 4:  // Complex SELECT query with WHERE, AND, OR, >, <, LIKE
                dbEngine.executeSQL(generateComplexSelectQuery(random));
                complexSelectCount++;
                break;
            case 5:  // Complex UPDATE query with WHERE
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
        switch (tableChoice) {
            case 0:
                return String.format("INSERT INTO users VALUES (%d, 'User%d', %d, 'City%d')", random.nextInt(10000), random.nextInt(10000), random.nextInt(60) + 20, random.nextInt(10));
            case 1:
                return String.format("INSERT INTO products VALUES (%d, 'Product%d', %.2f, 'Category%d')", random.nextInt(1000), random.nextInt(1000), random.nextDouble() * 1000, random.nextInt(5));
            case 2:
                return String.format("INSERT INTO orders VALUES (%d, %d, %d, %d)", random.nextInt(10000), random.nextInt(10000), random.nextInt(1000), random.nextInt(50));
            case 3:
                return String.format("INSERT INTO student VALUES (%d, 'Student%d', %d, %.2f, %b)",
                        random.nextInt(10000),                // id
                        random.nextInt(10000),                // name with Student prefix
                        random.nextInt(60) + 16,              // age between 16 and 75
                        random.nextDouble() * 4.0,            // gpa between 0.0 and 4.0
                        random.nextBoolean());
        }
        return "";
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
                "INSERT INTO student VALUES (1, 'John', 30, 2.4, False)",
                "INSERT INTO student VALUES (2, 'Alice', 18, 3.6, True)",
                "INSERT INTO student VALUES (3, 'Bob', 19, 3.2, False)",
                "INSERT INTO student VALUES (4, 'Charlie', 21, 2.9, False)",
                "INSERT INTO student VALUES (5, 'Diana', 17, 3.9, True)",
                "INSERT INTO student VALUES (6, 'Evan', 20, 3.1, True)",
                "INSERT INTO student VALUES (7, 'Frank', 16, 3.4, False)"
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
                "INSERT INTO orders VALUES (5, 5, 5, 2)"  // Diana bought 2 Headphones
        };

        for (String insertCommand : insertCommands) {
            dbEngine.executeSQL(insertCommand);
        }

        System.out.println("  -> ORDERS table prepopulated.");
    }

    // ----------------- Generate Queries Below -----------------

    // Generate a simple SELECT query
    private static String generateSimpleSelectQuery(Random random) {
        String[] tables = {"users", "products", "orders"};
        return String.format("SELECT * FROM %s", tables[random.nextInt(tables.length)]);
    }

    // Generate a complex SELECT query
    private static String generateComplexSelectQuery(Random random) {
        // Define the available tables
        String[] tables = {"student", "users", "products", "orders"};

        // Randomly select a table
        String tableName = tables[random.nextInt(tables.length)];

        // Conditions specific to each table's schema
        String[] studentConditions = {"age > 20", "gpa > 3.0", "deans_list = true"};
        String[] userConditions = {"age > 30", "city = 'New York'", "age < 25"};
        String[] productConditions = {"price < 500", "category = 'Electronics'", "price > 100"};
        String[] orderConditions = {"quantity > 5", "user_id = 1", "product_id = 100", "quantity < 50"};

        // Logical operators
        String[] logicalOperators = {"AND", "OR"};

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
            query = String.format("SELECT * FROM %s WHERE %s %s %s", tableName, condition1, logicalOperator, condition2);
        } else { // Otherwise, use only one condition
            query = String.format("SELECT * FROM %s WHERE %s", tableName, condition1);
        }

        System.out.println("Generated Query: " + query); // Debugging output to see the generated query

        return query;
    }

    // Generate an UPDATE query
    private static String generateUpdateQuery(Random random) {
        String[] tables = {"student", "users", "products", "orders"};
        int tableIndex = random.nextInt(tables.length);
        String tableName = tables[tableIndex];

        // Fields to update based on the selected table
        String updateField;
        String idField = "id";
        switch (tableName) {
            case "student" -> updateField = "gpa";
            case "users" -> updateField = "age";
            case "products" -> updateField = "price";
            case "orders" -> updateField = "quantity";
            default -> updateField = "age";
        }

        // Generate random new value and id
        int newValue = random.nextInt(100) + 1;  // Generate random value for update
        int idValue = random.nextInt(10000) + 1; // Generate random id to update

        // Format the update SQL query
        String updateQuery = String.format("UPDATE %s SET %s = %d WHERE %s = %d", tableName, updateField, newValue, idField, idValue);

        // Log what is being updated to what
        System.out.printf("Updating table '%s': Setting '%s' to %d for record with %s = %d%n",
                tableName, updateField, newValue, idField, idValue);

        return updateQuery;
    }

    // Generate a DELETE query
    private static String generateDeleteQuery(Random random) {
        String[] tables = {"student", "users", "products", "orders"};
        int tableIndex = random.nextInt(tables.length);
        String tableName = tables[tableIndex];
        String idField = "id";  // Assuming all tables have an "id" field

        int idValue = random.nextInt(10000) + 1; // Generate random id to delete
        return String.format("DELETE FROM %s WHERE %s = %d", tableName, idField, idValue);
    }

    // Generate a complex DELETE query
    private static String generateComplexDeleteQuery(Random random) {
        String[] tables = {"student", "users", "products", "orders"};
        int tableIndex = random.nextInt(tables.length);
        String tableName = tables[tableIndex];

        // Fields and values vary depending on the table
        String conditionField1;
        String conditionField2;
        String operator1;
        String operator2;
        String conditionValue1;
        String conditionValue2;

        switch (tableName) {
            case "student":
                conditionField1 = "age";
                conditionField2 = "gpa";
                operator1 = random.nextBoolean() ? ">" : "<";
                operator2 = random.nextBoolean() ? ">" : "<";
                conditionValue1 = String.valueOf(random.nextInt(10) + 18); // Random age between 18-28
                conditionValue2 = String.valueOf(1.0 + (random.nextDouble() * 3.0)); // Random GPA between 1.0 and 4.0
                break;
            case "users":
                conditionField1 = "age";
                conditionField2 = "city";
                operator1 = random.nextBoolean() ? ">" : "<";
                operator2 = "=";
                conditionValue1 = String.valueOf(random.nextInt(60) + 20); // Random age between 20-80
                conditionValue2 = "'City" + random.nextInt(10) + "'"; // Random city
                break;
            case "products":
                conditionField1 = "price";
                conditionField2 = "category";
                operator1 = random.nextBoolean() ? ">" : "<";
                operator2 = "=";
                conditionValue1 = String.valueOf(random.nextInt(500) + 100); // Random price between 100-600
                conditionValue2 = "'Category" + random.nextInt(5) + "'"; // Random category
                break;
            case "orders":
                conditionField1 = "quantity";
                conditionField2 = "user_id";
                operator1 = random.nextBoolean() ? ">" : "<";
                operator2 = "=";
                conditionValue1 = String.valueOf(random.nextInt(50) + 1); // Random quantity between 1-50
                conditionValue2 = String.valueOf(random.nextInt(10000) + 1); // Random user_id
                break;
            default:
                conditionField1 = "id";
                conditionField2 = "id";
                operator1 = "=";
                operator2 = "=";
                conditionValue1 = "1";
                conditionValue2 = "1";
        }

        // Combine conditions with either AND or OR
        String logicalOperator = random.nextBoolean() ? "AND" : "OR";

        return String.format("DELETE FROM %s WHERE %s %s %s %s %s %s",
                tableName, conditionField1, operator1, conditionValue1,
                logicalOperator, conditionField2, operator2, conditionValue2);
    }

    // Generate a complex UPDATE query
    private static String generateComplexUpdateQuery(Random random) {
        String[] tables = {"users", "products"};
        int tableIndex = random.nextInt(tables.length);
        String tableName = tables[tableIndex];

        String updateField;
        String conditionField;
        String conditionOperator;
        String idField = "id";  // Assuming all tables have an "id" field
        String conditionValue;

        switch (tableName) {
            case "users":
                updateField = "age";
                conditionField = "city";
                conditionOperator = "=";
                conditionValue = "'City" + random.nextInt(10) + "'"; // Generate a random city
                break;
            case "products":
                updateField = "price";
                conditionField = "category";
                conditionOperator = "=";
                conditionValue = "'Category" + random.nextInt(5) + "'"; // Generate a random category
                break;
            default:
                updateField = "age";
                conditionField = "city";
                conditionOperator = "=";
                conditionValue = "'City1'";
        }

        int newValue = random.nextInt(100) + 1;  // Random value for the update field
        return String.format("UPDATE %s SET %s = %d WHERE %s %s %s", tableName, updateField, newValue, conditionField, conditionOperator, conditionValue);
    }

    private static void printSummary(int numberOfQueries, double elapsedTime) {
        System.out.println("\nEvaluation completed in: " + elapsedTime + " seconds.");
        System.out.println("Total queries executed: " + numberOfQueries);
        System.out.println("Insert queries executed: " + insertCount);
        System.out.println("Simple SELECT queries executed: " + selectCount);
        System.out.println("Update queries executed: " + updateCount);
        System.out.println("Delete queries executed: " + deleteCount);
        System.out.println("Complex SELECT queries executed: " + complexSelectCount);
        System.out.println("Complex UPDATE queries executed: " + complexUpdateCount);
        System.out.println("Complex DELETE queries executed: " + complexDeleteCount);
    }
}