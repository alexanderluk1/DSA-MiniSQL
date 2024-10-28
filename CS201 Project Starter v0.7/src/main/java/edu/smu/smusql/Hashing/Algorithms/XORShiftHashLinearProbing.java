package edu.smu.smusql.Hashing.Algorithms;

/*
    Approach: Uses XOR and bit shifts for hashing, with linear probing for collision handling.
        Using linear probing, the algorithm searches sequentially for the next available slot.
    Use Case: Useful for situations where hash table resizing is feasible and data patterns might lead to consecutive hash values.
 */
public class XORShiftHashLinearProbing {

    private static final int MODULUS = 1_000_003;
    private final Entry[] table;

    public XORShiftHashLinearProbing(int size) {
        table = new Entry[size];
    }

    private static int hash(String key) {
        int hash = 0;
        for (int i = 0; i < key.length(); i++) {
            hash ^= (key.charAt(i) << (i % 5));
        }
        return Math.abs(hash % MODULUS);
    }

    public void put(String key, String value) {
        int index = hash(key) % table.length;
        while (table[index] != null && !table[index].key.equals(key)) {
            index = (index + 1) % table.length; // Linear probing
        }
        table[index] = new Entry(key, value);
    }

    public String get(String key) {
        int index = hash(key) % table.length;
        while (table[index] != null) {
            if (table[index].key.equals(key)) return table[index].value;
            index = (index + 1) % table.length;
        }
        return null;
    }

    private static class Entry {
        String key, value;
        Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}