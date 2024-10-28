package edu.smu.smusql.Hashing.Algorithms;

import java.util.LinkedList;
import edu.smu.smusql.pair1.Record;

/*
    Approach: Uses chaining for collision handling, where each bucket contains a linked list of entries.
	Use Case: Works well for datasets with a relatively even distribution but where occasional collisions are inevitable.
 */
public class MultiplicativeHashChaining {
    private static final int MULTIPLIER = 31;
    private static final int INITIAL_CAPACITY = 16;
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    private LinkedList<Entry>[] table;
    private int size; // Tracks number of elements

    public MultiplicativeHashChaining() {
        table = createTable(INITIAL_CAPACITY);
        this.size = 0;
    }

    private LinkedList<Entry>[] createTable(int capacity) {
        LinkedList<Entry>[] newTable = new LinkedList[capacity];
        for (int i = 0; i < capacity; i++) {
            newTable[i] = new LinkedList<>();
        }
        return newTable;
    }

    private int hash(Integer key) {
        return Math.abs((key * MULTIPLIER) % table.length);
    }

    public void put(Integer key, Record value) {
        // Resize if load factor exceeds threshold
        if ((double) size / table.length > LOAD_FACTOR_THRESHOLD) {
            resize();
        }

        int index = hash(key);
        for (Entry entry : table[index]) {
            if (entry.key.equals(key)) {
                entry.value = value; // Update existing key with new Record
                return;
            }
        }
        table[index].add(new Entry(key, value)); // Insert new entry with Record
        size++;
    }

    public Record get(Integer key) {
        int index = hash(key);
        for (Entry entry : table[index]) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }
        return null;
    }

    private void resize() {
        int newCapacity = table.length * 2;
        LinkedList<Entry>[] oldTable = table;
        table = createTable(newCapacity);
        size = 0;

        for (LinkedList<Entry> bucket : oldTable) {
            for (Entry entry : bucket) {
                put(entry.key, entry.value); // Rehash all entries into the new table
            }
        }
    }

    private static class Entry {
        Integer key;
        Record value;

        Entry(Integer key, Record value) {
            this.key = key;
            this.value = value;
        }
    }
}}