package edu.smu.smusql.Evaluation;

import java.util.Random;
import static edu.smu.smusql.Main.dbEngine;

public class CustomEvaluation {
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
        System.out.println("Evaluation completed in: " + elapsedTime + " seconds.");
    }

    // Method to create initial tables
    private static void createTables() {
        dbEngine.executeSQL("CREATE TABLE student (id, name, age, gpa, deans_list)");
        dbEngine.executeSQL("CREATE TABLE users (id, name, age, city)");
        dbEngine.executeSQL("CREATE TABLE products (id, name, price, category)");
        dbEngine.executeSQL("CREATE TABLE orders (id, user_id, product_id, quantity)");
        System.out.println("--------- All tables created ---------");
    }

    // Method to execute a random query (INSERT, SELECT, UPDATE, DELETE)
    private static void executeRandomQuery(Random random) {
        int queryType = random.nextInt(3); // Choose between INSERT, SELECT, or Complex SELECT

        switch (queryType) {
            case 0: // INSERT
                dbEngine.executeSQL(generateInsertQuery(random));
                break;
            case 1: // Simple SELECT
                dbEngine.executeSQL(generateSimpleSelectQuery(random));
                break;
            case 2: // Complex SELECT
                dbEngine.executeSQL(generateComplexSelectQuery(random));
                break;
        }
    }

    // Generate an INSERT query
    private static String generateInsertQuery(Random random) {
        int tableChoice = random.nextInt(3);
        switch (tableChoice) {
            case 0:
                return String.format("INSERT INTO users VALUES (%d, 'User%d', %d, 'City%d')", random.nextInt(10000), random.nextInt(10000), random.nextInt(60) + 20, random.nextInt(10));
            case 1:
                return String.format("INSERT INTO products VALUES (%d, 'Product%d', %.2f, 'Category%d')", random.nextInt(1000), random.nextInt(1000), random.nextDouble() * 1000, random.nextInt(5));
            case 2:
                return String.format("INSERT INTO orders VALUES (%d, %d, %d, %d)", random.nextInt(10000), random.nextInt(10000), random.nextInt(1000), random.nextInt(50));
        }
        return "";
    }

    // Generate a simple SELECT query
    private static String generateSimpleSelectQuery(Random random) {
        String[] tables = {"users", "products", "orders"};
        return String.format("SELECT * FROM %s", tables[random.nextInt(tables.length)]);
    }

    // Generate a complex SELECT query
    private static String generateComplexSelectQuery(Random random) {
        String[] conditions = {"age > 30", "price < 500", "quantity > 5"};
        return String.format("SELECT * FROM users WHERE %s", conditions[random.nextInt(conditions.length)]);
    }

    // Prepopulate tables with sample data
    private static void prepopulateTables() {
        deterministicPrepopulateTable();
    }

    public static void deterministicPrepopulateTable() {
        System.out.println("Prepopulating Tables...");
        prepopulateStudentsTable();
        prepopulateUsersTable();
        prepopulateProductsTable();
        prepopulateOrdersTable();

        System.out.println("Tables have been prepopulated.");
    }

    // Prepopulate Users Table
    private static void prepopulateStudentsTable() {
        System.out.println("Prepopulating STUDENT table...");

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

        System.out.println("User records have been prepopulated.");
    }

    // Prepopulate Users Table
    private static void prepopulateUsersTable() {
        System.out.println("Prepopulating USERS table...");

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

        System.out.println("User records have been prepopulated.");
    }

    // Prepopulate Products Table
    private static void prepopulateProductsTable() {
        System.out.println("Prepopulating PRODUCTS table...");

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

        System.out.println("Product records have been prepopulated.");
    }

    // Prepopulate Orders Table
    private static void prepopulateOrdersTable() {
        System.out.println("Prepopulating orders table...");

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

        System.out.println("Order records have been prepopulated.");
    }
}