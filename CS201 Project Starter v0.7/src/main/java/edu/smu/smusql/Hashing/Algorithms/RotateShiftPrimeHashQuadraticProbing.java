package edu.smu.smusql.Hashing.Algorithms;

/*
    Approach: Uses a combination of rotating bit shifts and prime multiplication to generate hash values.
        Quadratic probing handles collisions by using a quadratic function to jump to the next slot.
	Use Case: Suitable for datasets with a high risk of clustering, as quadratic probing helps disperse entries more widely than linear probing.
 */
public class RotateShiftPrimeHashQuadraticProbing {

    private static final int MULTIPLIER = 37;
    private static final int MODULUS = 1_000_003;
    private final Entry[] table;

    public RotateShiftPrimeHashQuadraticProbing(int size) {
        table = new Entry[size];
    }

    private static int hash(String key) {
        int hash = 0;
        for (int i = 0; i < key.length(); i++) {
            hash = (hash * MULTIPLIER) ^ (key.charAt(i) + (hash >>> 3));
            hash = Integer.rotateLeft(hash, 7); // Rotate bits for added randomness
        }
        return Math.abs(hash % MODULUS);
    }

    public void put(String key, String value) {
        int index = hash(key) % table.length;
        int i = 1;
        while (table[index] != null && !table[index].key.equals(key)) {
            index = (index + i * i) % table.length; // Quadratic probing
            i++;
        }
        table[index] = new Entry(key, value);
    }

    public String get(String key) {
        int index = hash(key) % table.length;
        int i = 1;
        while (table[index] != null) {
            if (table[index].key.equals(key)) return table[index].value;
            index = (index + i * i) % table.length;
            i++;
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
