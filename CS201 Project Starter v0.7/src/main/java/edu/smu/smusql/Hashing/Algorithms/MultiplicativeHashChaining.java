package edu.smu.smusql.Hashing.Algorithms;

import java.util.LinkedList;
import edu.smu.smusql.pair1.Record;

public class MultiplicativeHashChaining {
    private static final int MULTIPLIER = 31;
    private static final int INITIAL_SIZE = 1024; // Default initial size
    private static final int PRIME_MODULUS = 1_000_003; // Large prime modulus for better distribution

    private LinkedList<Entry>[] table;
    private int collisionCount;
    private final double loadFactorThreshold;
    private int entryCount;
    private int maxBucketLength;

    public MultiplicativeHashChaining(double loadFactorThreshold) {
        table = createTable(INITIAL_SIZE);
        collisionCount = 0;
        entryCount = 0;
        this.loadFactorThreshold = loadFactorThreshold;
        maxBucketLength = 0;
    }

    // Method to create a table of LinkedLists
    private LinkedList<Entry>[] createTable(int size) {
        LinkedList<Entry>[] newTable = new LinkedList[size];
        for (int i = 0; i < size; i++) {
            newTable[i] = new LinkedList<>();
        }
        return newTable;
    }

    // Multiplicative hash function with prime modulus
    private static int hash(Integer key, int tableSize) {
        return Math.abs((key * MULTIPLIER) % PRIME_MODULUS) % tableSize;
    }

    public void put(Integer key, Record value) {
        if ((double) entryCount / table.length >= loadFactorThreshold) {
            resize();
        }
    
        int index = hash(key, table.length);
        LinkedList<Entry> bucket = table[index];
    
        if (!bucket.isEmpty()) {
            collisionCount++;  // Increment collision count for a non-empty bucket
        }

        for (Entry entry : bucket) {
            if (entry.key.equals(key)) {
                entry.value = value; // Update value for the existing key
                return;
            }
        }

        bucket.add(new Entry(key, value)); // Insert new entry
        entryCount++;
    
        // Update max bucket length if this bucket's length exceeds the current max
        maxBucketLength = Math.max(maxBucketLength, bucket.size());
    }
    
    

    private double getLoadFactor() {
        int numElements = 0;
        for (LinkedList<Entry> bucket : table) {
            numElements += bucket.size();
        }
        return (double) numElements / table.length;
    }

    public Record get(Integer key) {
        int index = hash(key, table.length);
        for (Entry entry : table[index]) {
            if (entry.key.equals(key)) return entry.value;
        }
        return null;
    }

    public boolean delete(Integer key) {
        int index = hash(key, table.length);
        LinkedList<Entry> bucket = table[index];
        for (Entry entry : bucket) {
            if (entry.key.equals(key)) {
                bucket.remove(entry);
                entryCount--;
                return true;
            }
        }
        return false;
    }

    public boolean update(Integer key, Record newValue) {
        int index = hash(key, table.length);
        for (Entry entry : table[index]) {
            if (entry.key.equals(key)) {
                entry.value = newValue; // Update value for the existing key
                return true;
            }
        }
        return false;
    }

    public LinkedList<Integer> getAllKeys() {
        LinkedList<Integer> keys = new LinkedList<>();
        for (LinkedList<Entry> bucket : table) {
            for (Entry entry : bucket) {
                keys.add(entry.key);
            }
        }
        return keys;
    }

    public int getCollisionCount() {
        return collisionCount;
    }

    public double getAverageBucketSize() {
        return (double) entryCount / table.length;
    }

    public int getMaxBucketLength() {
        return maxBucketLength;
    }

    // Resizes the hash table when load factor exceeds threshold
    private void resize() {
        int newSize = table.length * 2; // Typically double the size
        LinkedList<Entry>[] newTable = createTable(newSize);

        // collisionCount = 0; // Reset collision count for the new table
        entryCount = 0; // Recalculate entry count during rehash
        maxBucketLength = 0;

        for (LinkedList<Entry> bucket : table) {
            for (Entry entry : bucket) {
                int newIndex = hash(entry.key, newSize);
                LinkedList<Entry> newBucket = newTable[newIndex];

                if (!newBucket.isEmpty()) {
                    collisionCount++;
                }

                newBucket.add(entry);
                entryCount++;

                // Update max bucket length if this bucket's length exceeds the current max
                maxBucketLength = Math.max(maxBucketLength, newBucket.size());
            }
        }

        table = newTable; // Replace old table with the new resized table
    }

    private static class Entry {
        Integer key;
        Record value;

        Entry(Integer key, Record value) {
            this.key = key;
            this.value = value;
        }
    }
}
