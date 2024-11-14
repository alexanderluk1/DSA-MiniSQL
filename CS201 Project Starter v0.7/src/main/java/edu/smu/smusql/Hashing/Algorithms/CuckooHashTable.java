package edu.smu.smusql.Hashing.Algorithms;

import edu.smu.smusql.pair1.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CuckooHashTable {
    private final double loadFactor;
    private static final int INITIAL_CAPACITY = 16;
    private static final int MAX_REHASH_ATTEMPTS = 32;

    private Record[] table1;
    private Record[] table2;
    private int size;
    private int capacity;
    private final Random random;
    private int collisionCount;
    private int table1PlacementCount;
    private int table2PlacementCount;

    public CuckooHashTable(double loadFactor) {
        this.loadFactor = loadFactor;
        this.capacity = INITIAL_CAPACITY;
        this.table1 = new Record[capacity];
        this.table2 = new Record[capacity];
        this.size = 0;
        this.random = new Random();
        this.collisionCount = 0;
        this.table1PlacementCount = 0;
        this.table2PlacementCount = 0;
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

        if (size >= capacity * loadFactor) {
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
                table1PlacementCount++; // Count successful placements in table1
                return true;
            }

            collisionCount++;
            Record temp = table1[hash1Index];
            table1[hash1Index] = record;
            record = temp;

            int hash2Index = hash2(key);
            if (table2[hash2Index] == null) {
                table2[hash2Index] = record;
                size++;
                table2PlacementCount++; // Count successful placements in table2
                return true;
            }

            collisionCount++;
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
        table1PlacementCount = 0; // Reset placement counts on resize
        table2PlacementCount = 0;

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

    // Method to get the current number of collisions
    public int getCollisionCount() {
        return collisionCount;
    }

    // Method to get the probability of placement in table1
    public double getTable1PlacementProbability() {
        int totalPlacements = table1PlacementCount + table2PlacementCount;
        return totalPlacements == 0 ? 0 : (double) table1PlacementCount / totalPlacements;
    }

    // Method to get the probability of placement in table2
    public double getTable2PlacementProbability() {
        int totalPlacements = table1PlacementCount + table2PlacementCount;
        return totalPlacements == 0 ? 0 : (double) table2PlacementCount / totalPlacements;
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

    // Method to calculate the longest cluster length in both tables
    public int getLongestClusterLength() {
        return Math.max(calculateLongestCluster(table1), calculateLongestCluster(table2));
    }

    // Method to calculate the average cluster length in both tables
    public double getAverageClusterLength() {
        int table1Clusters = countClusters(table1);
        int table2Clusters = countClusters(table2);
        int totalClusters = table1Clusters + table2Clusters;

        int totalLength = sumClusterLengths(table1) + sumClusterLengths(table2);
        return totalClusters == 0 ? 0 : (double) totalLength / totalClusters;
    }

    // Helper method to calculate the longest cluster length in a single table
    private int calculateLongestCluster(Record[] table) {
        int maxClusterLength = 0;
        int currentClusterLength = 0;

        for (Record record : table) {
            if (record != null) {
                currentClusterLength++;
            } else {
                maxClusterLength = Math.max(maxClusterLength, currentClusterLength);
                currentClusterLength = 0;
            }
        }
        return Math.max(maxClusterLength, currentClusterLength);
    }

    // Helper method to count clusters in a single table
    private int countClusters(Record[] table) {
        int clusterCount = 0;
        boolean inCluster = false;

        for (Record record : table) {
            if (record != null) {
                if (!inCluster) {
                    inCluster = true;
                    clusterCount++;
                }
            } else {
                inCluster = false;
            }
        }
        return clusterCount;
    }

    // Helper method to sum lengths of all clusters in a single table
    private int sumClusterLengths(Record[] table) {
        int totalLength = 0;
        int currentClusterLength = 0;

        for (Record record : table) {
            if (record != null) {
                currentClusterLength++;
            } else {
                totalLength += currentClusterLength;
                currentClusterLength = 0;
            }
        }
        totalLength += currentClusterLength;
        return totalLength;
    }
}
