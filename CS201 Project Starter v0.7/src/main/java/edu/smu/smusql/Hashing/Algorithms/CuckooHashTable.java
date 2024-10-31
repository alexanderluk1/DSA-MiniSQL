package edu.smu.smusql.Hashing.Algorithms;

import edu.smu.smusql.pair1.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
    Approach: Cuckoo Hashing with two hash functions and rehashing.
    Use Case: Suitable for handling large datasets with efficient constant time complexity for lookups, inserts, and deletes.
 */
public class CuckooHashTable {
    private static final double LOAD_FACTOR = 0.4;
    private static final int INITIAL_CAPACITY = 16;
    private static final int MAX_REHASH_ATTEMPTS = 32;

    private Record[] table1;
    private Record[] table2;
    private int size;
    private int capacity;
    private final Random random;

    public CuckooHashTable() {
        this.capacity = INITIAL_CAPACITY;
        this.table1 = new Record[capacity];
        this.table2 = new Record[capacity];
        this.size = 0;
        this.random = new Random();
    }

    // Hash function using multiply-shift hashing for the first table
    private int hash1(int key) {
        int hashConstant1 = 0x7f4a7c15; // Prime constant for multiplicative hashing
        return (int) ((key * hashConstant1) >>> (32 - log2(capacity))) % capacity;
    }

    // Hash function for the second table, slightly different multiplier
    private int hash2(int key) {
        int hashConstant2 = 0x5bd1e995; // Another prime constant
        return (int) ((key * hashConstant2) >>> (32 - log2(capacity))) % capacity;
    }

    // Inserts a record into the cuckoo hash table
    public boolean insert(Record record) {
        int key = record.getId();
        if (contains(key)) {
            return false; // Record already exists
        }

        if (size >= capacity * LOAD_FACTOR) {
            resize();
        }

        return insertKeyWithRehash(record, MAX_REHASH_ATTEMPTS);
    }

    // Recursive rehash method for insertions
    private boolean insertKeyWithRehash(Record record, int attemptsLeft) {
        int key = record.getId();
        if (attemptsLeft == 0) {
            resize();
            return insertKeyWithRehash(record, MAX_REHASH_ATTEMPTS);
        }

        for (int i = 0; i < MAX_REHASH_ATTEMPTS; i++) {
            int hash1Index = hash1(key);

            if (table1[hash1Index] == null) {
                table1[hash1Index] = record;
                size++;
                return true;
            }

            Record temp = table1[hash1Index];
            table1[hash1Index] = record;
            record = temp;

            int hash2Index = hash2(key);
            if (table2[hash2Index] == null) {
                table2[hash2Index] = record;
                size++;
                return true;
            }

            temp = table2[hash2Index];
            table2[hash2Index] = record;
            record = temp;
        }
        return false;
    }

    // Retrieves a record by its key
    public Record get(int key) {
        int hash1Index = hash1(key);
        if (table1[hash1Index] != null && table1[hash1Index].getId() == key) {
            return table1[hash1Index];
        }

        int hash2Index = hash2(key);
        if (table2[hash2Index] != null && table2[hash2Index].getId() == key) {
            return table2[hash2Index];
        }

        return null;
    }

    // Checks if a key is present in the hash table
    public boolean contains(int key) {
        return get(key) != null;
    }

    // Deletes a record by its key
    public boolean delete(int key) {
        int hash1Index = hash1(key);
        if (table1[hash1Index] != null && table1[hash1Index].getId() == key) {
            table1[hash1Index] = null;
            size--;
            return true;
        }

        int hash2Index = hash2(key);
        if (table2[hash2Index] != null && table2[hash2Index].getId() == key) {
            table2[hash2Index] = null;
            size--;
            return true;
        }

        return false;
    }

    // Retrieves all records in the hash table
    public List<Record> getAllRecords() {
        List<Record> allRecords = new ArrayList<>();
        for (Record record : table1) {
            if (record != null) {
                allRecords.add(record);
            }
        }
        for (Record record : table2) {
            if (record != null) {
                allRecords.add(record);
            }
        }
        return allRecords;
    }

    // Resizes the hash table and rehashes all entries
    private void resize() {
        capacity *= 2;
        Record[] oldTable1 = table1;
        Record[] oldTable2 = table2;
        table1 = new Record[capacity];
        table2 = new Record[capacity];
        size = 0;

        for (Record record : oldTable1) {
            if (record != null) {
                insert(record);
            }
        }

        for (Record record : oldTable2) {
            if (record != null) {
                insert(record);
            }
        }
    }

    // Helper method to calculate log2 of a number
    private int log2(int n) {
        return (int) (Math.log(n) / Math.log(2));
    }

    // Displays the content of the hash tables (for debugging purposes)
    public void displayTables() {
        System.out.println("Table 1:");
        for (int i = 0; i < capacity; i++) {
            System.out.println("Index " + i + ": " + (table1[i] == null ? "null" : table1[i].toString()));
        }
        System.out.println("\nTable 2:");
        for (int i = 0; i < capacity; i++) {
            System.out.println("Index " + i + ": " + (table2[i] == null ? "null" : table2[i].toString()));
        }
    }
}