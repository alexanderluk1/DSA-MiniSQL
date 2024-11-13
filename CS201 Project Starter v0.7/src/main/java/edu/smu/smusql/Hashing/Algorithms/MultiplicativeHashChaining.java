package edu.smu.smusql.Hashing.Algorithms;

import java.util.LinkedList;
import edu.smu.smusql.pair1.Record;

/*
    Approach: Uses chaining for collision handling, where each bucket contains a linked list of entries.
	Use Case: Works well for datasets with a relatively even distribution but where occasional collisions are inevitable.
 */
public class MultiplicativeHashChaining {
    private static final int MULTIPLIER = 31;
    private static final int MODULUS = 1_000_003;
    private static final int DEFAULT_SIZE = 1024; // Default initial size
    private LinkedList<Entry>[] table;

    public MultiplicativeHashChaining() {
        table = new LinkedList[DEFAULT_SIZE];
        for (int i = 0; i < DEFAULT_SIZE; i++) {
            table[i] = new LinkedList<>();
        }
    }

    private static int hash(Integer key) {
        return Math.abs((key * MULTIPLIER) % MODULUS);
    }

    public void put(Integer key, Record value) {
        if (getLoadFactor() > 0.75) {
            resize();
        }

        int index = hash(key) % table.length;
        for (Entry entry : table[index]) {
            if (entry.key.equals(key)) {
                entry.value = value; // Update existing key with new Record
                return;
            }
        }
        table[index].add(new Entry(key, value)); // Insert new entry with Record
    }

    private double getLoadFactor() {
        int numElements = 0;
        for (LinkedList<Entry> bucket : table) {
            numElements += bucket.size();
        }
        return (double) numElements / table.length;
    }

    public Record get(Integer key) {
        int index = hash(key) % table.length;
        for (Entry entry : table[index]) {
            if (entry.key.equals(key)) return entry.value;
        }
        return null;
    }

    public boolean delete(Integer key) {
        int index = hash(key) % table.length;
        for (Entry entry : table[index]) {
            if (entry.key.equals(key)) {
                table[index].remove(entry);
                return true; // Successfully removed the entry
            }
        }
        return false; // Key not found
    }

    public boolean update(Integer key, Record newValue) {
        int index = hash(key) % table.length;
        for (Entry entry : table[index]) {
            if (entry.key.equals(key)) {
                entry.value = newValue; // Update the value for the existing key
                return true; // Successfully updated the entry
            }
        }
        return false; // Key not found
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

    private void resize() {
        int newSize = table.length * 2;
        LinkedList<Entry>[] newTable = new LinkedList[newSize];
        for (int i = 0; i < newSize; i++) {
            newTable[i] = new LinkedList<>();
        }

        for (LinkedList<Entry> bucket : table) {
            for (Entry entry : bucket) {
                int newIndex = hash(entry.key) % newSize;
                newTable[newIndex].add(entry);
            }
        }

        table = newTable; // Replace old table with the new, resized table
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