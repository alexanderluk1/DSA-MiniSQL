# DSA-MiniSQL: A SQL-Like Command-Line Interface

## Project Overview

**DSA-MiniSQL** is a command-line interface (CLI) application that mimics SQL-like operations to interact with an in-memory database. This project is built using Java and the Picocli library. The CLI allows users to perform basic database operations such as inserting, searching, and deleting records using SQL-like statements.

## Key Features

- **Insert records** using `INSERT INTO`.
- **Search records** using `SELECT * FROM`.
- **Delete records** using `DELETE FROM`.
- **Interactive CLI** for user input, similar to a SQL shell.
- **In-memory database implementation** for simplicity.
- **Efficient data handling** using a B+ Tree per table approach.

## Data Structure: B+ Tree Per Table

The DSA-MiniSQL uses a **B+ Tree** data structure to handle records in memory for each table. Each table has its own B+ Tree that organizes data efficiently, enabling fast searches, updates, and deletions.

### Table Class

- **Stores table metadata**: Name, field list, and field count.
- **Manages a B+ tree**: Handles data storage and retrieval for the table.
- **Operations**: The `UPDATE`, `DELETE`, and `SELECT` operations involve iterating through nodes in the B+ Tree and checking whether the node values match the given condition.

### B+ Node

- **Index Nodes**: Store a list of keys that map one-to-one to record IDs.
- **Leaf Nodes**: Store values in a `TreeMap`, where:
  - **Key**: Record ID
  - **Value**: Array of field values corresponding to the record.
  
### Operations Involving B+ Tree:
- **INSERT**: Data is inserted into the appropriate position in the B+ Tree.
- **SELECT**: The tree is traversed to find records matching the specified condition.
- **UPDATE**: Nodes in the tree are updated if their values match the specified condition.
- **DELETE**: Matching nodes are deleted from the tree, ensuring the B+ Tree remains balanced.

## Technologies Used

- **Java**: The core programming language.
- **Maven**: Build automation tool used for dependency management and project structure.
- **Picocli**: A powerful library for building command-line interfaces in Java.

## Prerequisites

Before running this project, ensure you have the following installed:

- **Java 8+**
- **Maven**

## Installation

1. Navigate to the project directory:
    ```bash
    cd DSA-MiniSQL
    ```
3. Build the project using Maven:
    ```bash
    mvn clean install
    ```

## Usage

To run the CLI:

```bash
java -jar DSA-MiniSQL.jar
