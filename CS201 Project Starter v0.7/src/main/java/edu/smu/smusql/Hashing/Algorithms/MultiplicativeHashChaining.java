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
    private final LinkedList<Entry>[] table;

    public MultiplicativeHashChaining(int size) {
        table = new LinkedList[size];
        for (int i = 0; i < size; i++) {
            table[i] = new LinkedList<>();
        }
    }

    private static int hash(Integer key) {
        return Math.abs((key * MULTIPLIER) % MODULUS);
    }

    public void put(Integer key, Record value) {
        int index = hash(key) % table.length;
        for (Entry entry : table[index]) {
            if (entry.key.equals(key)) {
                entry.value = value; // Update existing key with new Record
                return;
            }
        }
        table[index].add(new Entry(key, value)); // Insert new entry with Record
    }

    public Record get(Integer key) {
        int index = hash(key) % table.length;
        for (Entry entry : table[index]) {
            if (entry.key.equals(key)) return entry.value;
        }
        return null;
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