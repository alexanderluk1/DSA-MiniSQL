# DSA-MiniSQL G1-T1 (Version A)

## Project Overview

**DSA-MiniSQL** is a command-line interface (CLI) application that mimics SQL-like operations to interact with an in-memory database. This project is built using Java and the Picocli library. The CLI allows users to perform basic database operations such as inserting, searching, and deleting records using SQL-like statements.

## Features

- Insert records using `INSERT INTO`.
- Search records using `SELECT * FROM`.
- Delete records using `DELETE FROM`.
- Interactive CLI for user input, similar to a SQL shell.
- In-memory database implementation for simplicity.

## Data Structure Overview

### 1. Database (Database Class)
- The Database class is a container that holds all tables in the database.
Internally, it uses a `HashMap<String, Table>` where:
- The key is the table name (String).
- The value is a Table object representing the table and its data.
- Methods in this class allow creating new tables, retrieving tables, and checking for the existence of a table.

### 2. Table (Table Class)
- Represents a SQL-like table with columns and rows.
- Key attributes:
  - `tableName` - Name of the table. 
  - `columns` - List of columns (field names) in the table. 
  - `records` - A list of Record objects, each representing a row.
- Provides functionality to add new records, retrieve all records, retrieve records with conditions, update rows, and delete rows based on conditions.

### 3. Record (Record Class)
- Represents a single row in a table. 
- Each Record holds a list of values corresponding to the columns in the table. 
- Provides access to fields within the row, allowing operations like setting or retrieving field values.

### 4. Engine (Engine Class)
- The core class for handling and executing SQL commands.
- Uses the Database class for managing tables and records, simulating SQL commands:
  - `CREATE` - Creates a new table. 
  - `INSERT` - Inserts a new record into a table. 
  - `SELECT` - Retrieves records from a table, optionally with conditions. 
  - `UPDATE` - Modifies records in a table based on conditions. 
  - `DELETE` - Deletes records in a table based on conditions.

### 5. Parser (Parser Class)
- Responsible for parsing SQL-like queries into individual components for further processing by the Engine.
- Key methods:
  - `parseCreate` - Parses CREATE TABLE commands.
  - `parseSelect`, parseUpdate, parseDelete - Parses other SQL commands and their clauses.
  - `parseConditions` - Extracts conditions from WHERE clauses to apply them to SELECT, UPDATE, and DELETE operations.
- Uses regex patterns to split complex conditions and logical operators (e.g., AND, OR) into valid SQL components.

### 6. Custom Evaluation Script
- Each query type (SIMPLE_INSERT, SIMPLE_SELECT, SIMPLE_UPDATE, SIMPLE_DELETE, COMPLEX_SELECT, COMPLEX_UPDATE, COMPLEX_DELETE) has a separate counter and timer stored in the queryTimeMap to measure execution performance.
- During each query’s execution, a start time (queryStartTime) and end time (queryEndTime) are recorded in nanoseconds.
- The difference between these times gives the execution duration in seconds, which is added to the cumulative time for the query type in queryTimeMap.
- After all queries have run, the total execution time for each query type is printed in the evaluation summary, alongside the count of executed queries, allowing for performance analysis on each type of query.


## Technologies Used

- **Java**: The core programming language.
- **Maven**: Build automation tool used for dependency management and project structure.
- **Picocli**: A powerful library for building command-line interfaces in Java.
- **JUnit**: For testing purposes (if applicable).

## Prerequisites

Before running this project, ensure you have the following installed:

- **Java 8+**
- **Maven**
