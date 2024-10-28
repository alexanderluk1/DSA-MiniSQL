package edu.smu.smusql.Hashing.Keys;

public class MultiplicativeHashChainedKey {
    private final int key;

    public MultiplicativeHashChainedKey(int key) {
        this.key = key;
    }

    @Override
    public int hashCode() {
        int primeMultiplier = 31;  // Prime multiplier for the multiplicative hash
        return key * primeMultiplier;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MultiplicativeHashChainedKey other = (MultiplicativeHashChainedKey) obj;
        return key == other.key;
    }

    @Override
    public String toString() {
        return Integer.toString(key);
    }
}