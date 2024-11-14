package edu.smu.smusql.Evaluation;

import edu.smu.smusql.pair1.Record;
import edu.smu.smusql.Hashing.Algorithms.CuckooHashTable;
import edu.smu.smusql.Hashing.Algorithms.MultiplicativeHashChaining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HashTableEvaluation {

    public static void main(String[] args) {
        int numRecords = 10000;  // Number of records to test
        double[] loadFactors = {1.0};  // Different load factors to evaluate
        // 0.25, 0.4, 0.5, 0.6, 0.75, 0.9, 1.0
        for (double loadFactor : loadFactors) {
            System.out.println("\n--- Evaluating Load Factor: " + loadFactor + " ---");

            // Initialize hash tables with the specified load factor for Multiplicative Hash Chaining
            CuckooHashTable cuckooHashTable = new CuckooHashTable(loadFactor);
            MultiplicativeHashChaining mhcHashTable = new MultiplicativeHashChaining(loadFactor);
            HashMap<Integer, Record> hashMap = new HashMap<>();

            // Generate test records
            List<Record> records = generateTestRecords(numRecords);

            // Memory and collision evaluation
            System.out.println("\nTesting Memory Usage and Collisions...");
            testMemoryUsageAndCollisions(cuckooHashTable, records, "CuckooHashTable");
            testMemoryUsageAndCollisions(mhcHashTable, records, "MHCTable");
            testMemoryUsageAndCollisions(hashMap, records, "HashMap");

            // Print results
            printCollisionPercentage(cuckooHashTable, numRecords, "CuckooHashTable");
            printCollisionPercentage(mhcHashTable, numRecords, "MHCTable");
            printClusterLengths(cuckooHashTable, mhcHashTable);

            // Print placement probabilities for CuckooHashTable
            System.out.println("CuckooHashTable Table 1 Placement Probability: " + cuckooHashTable.getTable1PlacementProbability());
            System.out.println("CuckooHashTable Table 2 Placement Probability: " + cuckooHashTable.getTable2PlacementProbability());
        }
    }

    // Generate test records with unique keys
    private static List<Record> generateTestRecords(int numRecords) {
        List<Record> records = new ArrayList<>();
        for (int i = 0; i < numRecords; i++) {
            records.add(new Record(new ArrayList<>(List.of("id", "name")), new ArrayList<>(List.of(i, "Record " + i)))); 
        }
        return records;
    }

    // Memory Usage and Collision Test
    private static void testMemoryUsageAndCollisions(Object hashTable, List<Record> records, String tableName) {
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        // Insert records and count collisions
        for (Record record : records) {
            if (hashTable instanceof CuckooHashTable) {
                ((CuckooHashTable) hashTable).insert(record);
            } else if (hashTable instanceof MultiplicativeHashChaining) {
                ((MultiplicativeHashChaining) hashTable).put(record.getId(), record);
            } else if (hashTable instanceof HashMap) {
                ((HashMap<Integer, Record>) hashTable).put(record.getId(), record);
            }
        }

        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println(tableName + " Memory Usage: " + (memoryAfter - memoryBefore) + " bytes");
    }

    // Print collision percentage for CuckooHashTable and MultiplicativeHashChaining
    private static void printCollisionPercentage(Object hashTable, int numRecords, String tableName) {
        int collisionCount = 0;

        if (hashTable instanceof CuckooHashTable) {
            collisionCount = ((CuckooHashTable) hashTable).getCollisionCount();
        } else if (hashTable instanceof MultiplicativeHashChaining) {
            collisionCount = ((MultiplicativeHashChaining) hashTable).getCollisionCount();
        }

        double collisionPercentage = (double) collisionCount / numRecords * 100;
        System.out.println(tableName + " Total Collisions: " + collisionCount);
    }

    // Print clustering details for both CuckooHashTable and MultiplicativeHashChaining
    private static void printClusterLengths(CuckooHashTable cuckooHashTable, MultiplicativeHashChaining mhcHashTable) {
        // Print cluster details for CuckooHashTable
        System.out.println("CuckooHashTable Longest Cluster Length: " + cuckooHashTable.getLongestClusterLength());
        System.out.println("CuckooHashTable Average Cluster Length: " + cuckooHashTable.getAverageClusterLength());

        // Print clustering details for MultiplicativeHashChaining
        System.out.println("MHCTable Max Bucket Length: " + mhcHashTable.getMaxBucketLength());
        System.out.println("MHCTable Average Bucket Length: " + mhcHashTable.getAverageBucketSize());
        // System.out.println("MHCTable Longest Cluster Length: " + mhcHashTable.getLongestClusterLength());
        // System.out.println("MHCTable Average Cluster Length: " + mhcHashTable.getAverageClusterLength());
    }
}
