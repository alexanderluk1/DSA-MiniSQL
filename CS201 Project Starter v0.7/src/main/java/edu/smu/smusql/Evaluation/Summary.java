package edu.smu.smusql.Evaluation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import edu.smu.smusql.enums.QueryToExecute;

import java.util.HashMap;

public class Summary {
    private static final Map<QueryToExecute, Double[]> timingsMap = new HashMap<>();

    public static void clearSummaryCSV() {
        String csvFile = "query_summary.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, false))) {
            // Create an empty file (overwrites any existing data)
            writer.write(""); 
        } catch (IOException e) {
            System.err.println("Error clearing CSV file: " + e.getMessage());
        }
    }

    public static void storeTiming(QueryToExecute functionName, double time, int numberOfQueries) {
        // Initialize the array if it's the first time we're recording for this function
        timingsMap.putIfAbsent(functionName, new Double[4]);

        // Store the timing in the appropriate index based on number of queries
        if (numberOfQueries == 1000) {
            timingsMap.get(functionName)[0] = time;
        } else if (numberOfQueries == 10000) {
            timingsMap.get(functionName)[1] = time;
        } else if (numberOfQueries == 100000) {
            timingsMap.get(functionName)[2] = time;
        } else if (numberOfQueries == 1000000) {
            timingsMap.get(functionName)[3] = time;
        }
    }

    public static void writeAllTimingsToCSV() {
        String csvFile = "query_summary.csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, true))) {
            // Write header if it's the first entry
            writer.write("Function,Time 1000,Time 10000,Time 100000,Time 1000000\n");
                
            // Write each function's timings to the CSV
            for (Map.Entry<QueryToExecute, Double[]> entry : timingsMap.entrySet()) {
                QueryToExecute function = entry.getKey();
                Double[] times = entry.getValue();
                writer.write(function + "," + times[0] + "," + times[1] + "," + times[2] + "," + times[3] + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}
