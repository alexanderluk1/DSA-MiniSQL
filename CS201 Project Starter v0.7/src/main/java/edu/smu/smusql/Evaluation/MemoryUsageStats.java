package edu.smu.smusql.Evaluation;

import java.util.ArrayList;
import java.util.List;

public class MemoryUsageStats {
    private long totalMemoryUsed = 0;
    private long maxMemoryUsed = 0;
    private int runCount = 0;
    private List<Long> memoryUsages = new ArrayList<>();

    public void recordUsage(long memoryUsed) {
        totalMemoryUsed += memoryUsed;
        maxMemoryUsed = Math.max(maxMemoryUsed, memoryUsed);
        memoryUsages.add(memoryUsed);
        runCount++;
    }

    public double getAverageMemoryUsage() {
        return runCount > 0 ? (double) totalMemoryUsed / runCount : 0;
    }

    public long getMaxMemoryUsage() {
        return maxMemoryUsed;
    }

    public double getStandardDeviation() {
        if (runCount <= 1) return 0;
        double avg = getAverageMemoryUsage();
        double sumOfSquares = 0;
        for (long usage : memoryUsages) {
            sumOfSquares += Math.pow(usage - avg, 2);
        }
        return Math.sqrt(sumOfSquares / (runCount - 1));
    }

    public void printStats(String functionName) {
        System.out.printf("Function: %s%n", functionName);
        System.out.printf("Average Memory Usage: %.2f bytes%n", getAverageMemoryUsage());
        System.out.printf("Peak (Max) Memory Usage: %d bytes%n", getMaxMemoryUsage());
        System.out.printf("Total Memory Usage: %d bytes%n", totalMemoryUsed);
        System.out.printf("Standard Deviation: %.2f bytes%n", getStandardDeviation());
    }
}
