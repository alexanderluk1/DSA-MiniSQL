package edu.smu.smusql;



import java.util.*;

// @author ziyuanliu@smu.edu.sg

public class Main {
    /*
     *  Main method for accessing the command line interface of the database engine.
     *  MODIFICATION OF THIS FILE IS NOT RECOMMENDED!
     */
    static Engine dbEngine = new Engine();
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("smuSQL Starter Code version 0.5");
        System.out.println("Have fun, and good luck!");

        while (true) {
            System.out.print("smusql> ");
            String query = scanner.nextLine();
            if (query.equalsIgnoreCase("exit")) {
                break;
            } else if (query.equalsIgnoreCase("evaluate")) {

                          autoEvaluate();

            }

            System.out.println(dbEngine.executeSQL(query));
        }
        scanner.close();
    }


    /*
     *  Below is the code for auto-evaluating your work.
     *  DO NOT CHANGE ANYTHING BELOW THIS LINE!
     */

        public static void autoEvaluate() {
            // Record overall start time
            long overallStartTime = System.currentTimeMillis();

            // Set the number of queries to execute
            int numberOfQueries = 100000;

            // Create tables
            dbEngine.executeSQL("CREATE TABLE users (id INT, name VARCHAR(100), age INT, city VARCHAR(100))");
            dbEngine.executeSQL("CREATE TABLE products (id INT, name VARCHAR(100), price FLOAT, category VARCHAR(100))");
            dbEngine.executeSQL("CREATE TABLE orders (id INT, user_id INT, product_id INT, quantity INT)");

            // Random data generator
            Random random = new Random();

            // Prepopulate the tables in preparation for evaluation
            prepopulateTables(random);

            // Query execution counters and times
            long insertCount = 0, selectCount = 0, updateCount = 0, deleteCount = 0, complexSelectCount = 0, complexUpdateCount = 0;
            long insertTime = 0, selectTime = 0, updateTime = 0, deleteTime = 0, complexSelectTime = 0, complexUpdateTime = 0;

            // Loop to simulate millions of queries
            for (int i = 0; i < numberOfQueries; i++) {
                int queryType = random.nextInt(6);  // Randomly choose the type of query to execute
                long startTime, endTime;

                switch (queryType) {
                    case 0:  // INSERT query
                        startTime = System.currentTimeMillis();
                        insertRandomData(random);
                        endTime = System.currentTimeMillis();
                        insertCount++;
                        insertTime += (endTime - startTime);
                        break;
                    case 1:  // SELECT query (simple)
                        startTime = System.currentTimeMillis();
                        selectRandomData(random);
                        endTime = System.currentTimeMillis();
                        selectCount++;
                        selectTime += (endTime - startTime);
                        break;
                    case 2:  // UPDATE query
                        startTime = System.currentTimeMillis();
                        updateRandomData(random);
                        endTime = System.currentTimeMillis();
                        updateCount++;
                        updateTime += (endTime - startTime);
                        break;
                    case 3:  // DELETE query
                        startTime = System.currentTimeMillis();
                        deleteRandomData(random);
                        endTime = System.currentTimeMillis();
                        deleteCount++;
                        deleteTime += (endTime - startTime);
                        break;
                    case 4:  // Complex SELECT query with WHERE, AND, OR, >, <, LIKE
                        startTime = System.currentTimeMillis();
                        complexSelectQuery(random);
                        endTime = System.currentTimeMillis();
                        complexSelectCount++;
                        complexSelectTime += (endTime - startTime);
                        break;
                    case 5:  // Complex UPDATE query with WHERE
                        startTime = System.currentTimeMillis();
                        complexUpdateQuery(random);
                        endTime = System.currentTimeMillis();
                        complexUpdateCount++;
                        complexUpdateTime += (endTime - startTime);
                        break;
                }

                // Print progress every 10,000 queries
                if (i % 10000 == 0) {
                    System.out.println("Processed " + i + " queries...");
                }
            }

            // Record overall end time
            long overallEndTime = System.currentTimeMillis();

            // Calculate the total execution time
            long totalExecutionTime = overallEndTime - overallStartTime;

            // Print the overall summary
            System.out.println("Evaluation Summary");
            System.out.println("=====================================");
            System.out.println("Total Queries Executed: " + numberOfQueries);
            System.out.println("Total Time Taken: " + (totalExecutionTime / 1000.0) + " seconds");

            // Print the detailed query execution counts and times
            System.out.println("\n--- Query Execution Counts and Times ---");
            System.out.printf("Insert Queries      : %-5d | Time Taken: %-10.6f seconds%n", insertCount, insertTime / 1000.0);
            System.out.printf("Simple SELECT Queries: %-5d | Time Taken: %-10.6f seconds%n", selectCount, selectTime / 1000.0);
            System.out.printf("Update Queries      : %-5d | Time Taken: %-10.6f seconds%n", updateCount, updateTime / 1000.0);
            System.out.printf("Delete Queries      : %-5d | Time Taken: %-10.6f seconds%n", deleteCount, deleteTime / 1000.0);
            System.out.printf("Complex SELECT Queries: %-5d | Time Taken: %-10.6f seconds%n", complexSelectCount, complexSelectTime / 1000.0);
            System.out.printf("Complex UPDATE Queries: %-5d | Time Taken: %-10.6f seconds%n", complexUpdateCount, complexUpdateTime / 1000.0);
        }

        private static void prepopulateTables(Random random) {
            // Insert initial users
            for (int i = 0; i < 50; i++) {
                String name = "User" + i;
                int age = 20 + (i % 41); // Ages between 20 and 60
                String city = getRandomCity(random);
                String insertCommand = String.format("INSERT INTO users VALUES (%d, %s, %d, %s)", i, name, age, city);
                dbEngine.executeSQL(insertCommand);
            }
            // Insert initial products
            for (int i = 0; i < 50; i++) {
                String productName = "Product" + i;
                double price = 10 + (i % 990); // Prices between $10 and $1000
                String category = getRandomCategory(random);
                String insertCommand = String.format("INSERT INTO products VALUES (%d, %s, %.2f, %s)", i, productName, price, category);
                dbEngine.executeSQL(insertCommand);
            }
            // Insert initial orders
            for (int i = 0; i < 50; i++) {
                int user_id = random.nextInt(50);
                int product_id = random.nextInt(50);
                int quantity = random.nextInt(1, 100);
                String insertCommand = String.format("INSERT INTO orders VALUES (%d, %d, %d, %d)", i, user_id, product_id, quantity);
                dbEngine.executeSQL(insertCommand);
            }
        }

        private static void insertRandomData(Random random) {
            // Randomly insert data into users, products, or orders
            int table = random.nextInt(3);
            String insertCommand = "";
            int id = random.nextInt(1000);
            switch (table) {
                case 0: // Insert into users
                    String name = "User" + random.nextInt(1000);
                    int age = random.nextInt(20, 60);
                    String city = getRandomCity(random);
                    insertCommand = String.format("INSERT INTO users VALUES (%d, %s, %d, %s)",id, name, age, city);
                    break;
                case 1: // Insert into products
                    String productName = "Product" + random.nextInt(1000);
                    double price = 10 + (random.nextDouble() * 1000);
                    String category = getRandomCategory(random);
                    insertCommand = String.format("INSERT INTO products VALUES (%d, %s, %.2f, %s)", id,productName, price, category);
                    break;
                case 2: // Insert into orders
                    int userId = random.nextInt(50);
                    int productId = random.nextInt(50);
                    int quantity = random.nextInt(1, 100);
                    insertCommand = String.format("INSERT INTO orders VALUES (%d, %d, %d, %d)", id, userId, productId, quantity);
                    break;
            }
            dbEngine.executeSQL(insertCommand);
        }

        private static void selectRandomData(Random random) {
            // Simple SELECT query to retrieve data from users, products, or orders
            int table = random.nextInt(3);
            String selectCommand = "";
            switch (table) {
                case 0: // SELECT from users
                    selectCommand = "SELECT * FROM users WHERE id = " + random.nextInt(50);
                    break;
                case 1: // SELECT from products
                    selectCommand = "SELECT * FROM products WHERE id = " + random.nextInt(50);
                    break;
                case 2: // SELECT from orders
                    selectCommand = "SELECT * FROM orders WHERE id = " + random.nextInt(50);
                    break;
            }
            dbEngine.executeSQL(selectCommand);
        }

        private static void updateRandomData(Random random) {
            // Randomly update data in users, products, or orders
            int table = random.nextInt(3);
            String updateCommand = "";
            switch (table) {
                case 0: // Update users
                    int userId = random.nextInt(50);
                    String newCity = getRandomCity(random);
                    updateCommand = String.format("UPDATE users SET city = %s WHERE id = %d", newCity, userId);
                    break;
                case 1: // Update products
                    int productId = random.nextInt(50);
                    double newPrice = 10 + (random.nextDouble() * 1000);
                    updateCommand = String.format("UPDATE products SET price = %.2f WHERE id = %d", newPrice, productId);
                    break;
                case 2: // Update orders
                    int orderId = random.nextInt(50);
                    int newQuantity = random.nextInt(1, 100);
                    updateCommand = String.format("UPDATE orders SET quantity = %d WHERE id = %d", newQuantity, orderId);
                    break;
            }
            dbEngine.executeSQL(updateCommand);
        }

        private static void deleteRandomData(Random random) {
            // Randomly delete data from users, products, or orders
            int table = random.nextInt(3);
            String deleteCommand = "";
            switch (table) {
                case 0: // Delete from users
                    deleteCommand = "DELETE FROM users WHERE id = " + random.nextInt(50);
                    break;
                case 1: // Delete from products
                    deleteCommand = "DELETE FROM products WHERE id = " + random.nextInt(50);
                    break;
                case 2: // Delete from orders
                    deleteCommand = "DELETE FROM orders WHERE id = " + random.nextInt(50);
                    break;
            }
            dbEngine.executeSQL(deleteCommand);
        }

        private static void complexSelectQuery(Random random) {
            // Complex SELECT query with WHERE, AND, OR, LIKE
            String complexSelectCommand = "SELECT * FROM products WHERE price > " + random.nextInt(100, 1000) +
                    " AND category = " + getRandomCategory(random);
            dbEngine.executeSQL(complexSelectCommand);
        }

        private static void complexUpdateQuery(Random random) {
            // Complex UPDATE query with WHERE
            String complexUpdateCommand = "UPDATE users SET city = Updated City WHERE age > " + random.nextInt(30, 50);
            dbEngine.executeSQL(complexUpdateCommand);
        }

        // Helper method to return a random city
        private static String getRandomCity(Random random) {
            String[] cities = {"New York", "Los Angeles", "Chicago", "Boston", "Miami", "Seattle", "Austin", "Dallas", "Atlanta", "Denver"};
            return cities[random.nextInt(cities.length)];
        }

        // Helper method to return a random category for products
        private static String getRandomCategory(Random random) {
            String[] categories = {"Electronics", "Appliances", "Clothing", "Furniture", "Toys", "Sports", "Books", "Beauty", "Garden"};
            return categories[random.nextInt(categories.length)];
        }
    }


