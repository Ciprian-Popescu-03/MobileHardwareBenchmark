package com.example.performancetester.benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RAMBenchmark {

    private static final int MATRIX_SIZE = 1000;

    public static class Result {
        public double rowMBps;
        public double colMBps;
        public double randomMBps;
        public List<Float> historyData; // Progression over time
    }

    public static Result run() {
        Result result = new Result();
        result.historyData = new ArrayList<>();

        int[][] matrix = new int[MATRIX_SIZE][MATRIX_SIZE];
        Random r = new Random();

        // 1. Setup Data
        for (int i = 0; i < MATRIX_SIZE; i++)
            for (int j = 0; j < MATRIX_SIZE; j++)
                matrix[i][j] = r.nextInt();

        long totalBytes = (long) MATRIX_SIZE * MATRIX_SIZE * 4;

        // --- ROW ACCESS (Sequential) ---
        long start = System.nanoTime();
        long sum = 0;
        // Check speed every 100 rows to create graph points
        for (int i = 0; i < MATRIX_SIZE; i++) {
            long batchStart = System.nanoTime();
            for (int j = 0; j < MATRIX_SIZE; j++) sum += matrix[i][j];

            // Record speed of this chunk (arbitrary scale for graph)
            if (i % 50 == 0) {
                double batchTime = (System.nanoTime() - batchStart) / 1e9;
                // MB/s for this small chunk
                double speed = ((MATRIX_SIZE * 4) / 1_000_000.0) / batchTime;
                result.historyData.add((float) speed);
            }
        }
        long time = System.nanoTime() - start;
        result.rowMBps = (totalBytes / 1_000_000.0) / (time / 1e9);

        // --- COL ACCESS (Non-Sequential) ---
        start = System.nanoTime();
        sum = 0;
        for (int j = 0; j < MATRIX_SIZE; j++) {
            for (int i = 0; i < MATRIX_SIZE; i++) sum += matrix[i][j];
        }
        time = System.nanoTime() - start;
        result.colMBps = (totalBytes / 1_000_000.0) / (time / 1e9);

        // Just add a dip in the graph to show we moved to a slower phase
        result.historyData.add((float) result.colMBps);

        // --- RANDOM ACCESS ---
        start = System.nanoTime();
        for (int k = 0; k < MATRIX_SIZE * MATRIX_SIZE; k++) {
            int x = r.nextInt(MATRIX_SIZE);
            int y = r.nextInt(MATRIX_SIZE);
            sum += matrix[x][y];
        }
        time = System.nanoTime() - start;
        result.randomMBps = (totalBytes / 1_000_000.0) / (time / 1e9);

        result.historyData.add((float) result.randomMBps);

        return result;
    }
}