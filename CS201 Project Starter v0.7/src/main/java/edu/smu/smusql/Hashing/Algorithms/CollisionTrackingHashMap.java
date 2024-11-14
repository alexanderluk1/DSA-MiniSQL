package edu.smu.smusql.Hashing.Algorithms;

import java.util.HashMap;

public class CollisionTrackingHashMap<K, V> extends HashMap<K, V> {
    private int collisionCount = 0;  // To track total collisions

    @Override
    public V put(K key, V value) {
        // Check if the bucket at the computed index is already occupied
        if (this.containsKey(key)) {
            collisionCount++;  // Increment collision count
        }
        return super.put(key, value);
    }

    public int getCollisionCount() {
        return collisionCount;
    }

    public void resetCollisionCount() {
        collisionCount = 0;
    }
}
