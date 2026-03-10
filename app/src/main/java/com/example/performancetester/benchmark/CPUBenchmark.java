package com.example.performancetester.benchmark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CPUBenchmark {

    public static class Result {
        public double singleCoreSeconds;
        public double multiCoreSeconds;
        public List<Float> historyData;
    }

    // --- ALGORITHMS (Same as before) ---
    private static long fib(int n) {
        if (n <= 1) return n;
        return fib(n - 1) + fib(n - 2);
    }
    private static int sieve(int limit) {
        boolean[] isPrime = new boolean[limit + 1];
        Arrays.fill(isPrime, true);
        int count = 0;
        for (int p = 2; p * p <= limit; p++) {
            if (isPrime[p]) {
                for (int i = p * p; i <= limit; i += p) isPrime[i] = false;
            }
        }
        for (int i = 2; i <= limit; i++) if (isPrime[i]) count++;
        return count;
    }
    private static double matrixMult(int N) {
        double[][] A = new double[N][N];
        double[][] B = new double[N][N];
        for(int i=0; i<N; i++) {
            for(int j=0; j<N; j++) {
                A[i][j] = 1.0 + (i+j)%10;
                B[i][j] = 1.0 + (i*j)%10;
            }
        }
        double sum = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < N; k++) sum += A[i][k] * B[k][j];
            }
        }
        return sum;
    }

    // --- WORKLOADS ---
    private static void runHeavyWorkload() {
        fib(39);
        sieve(3_000_000);
        matrixMult(150);
    }

    private static void runLightWorkload() {
        fib(32);
        sieve(200_000);
        matrixMult(40);
    }

    // --- RUNNER ---
    public static Result run() {
        Result result = new Result();
        result.historyData = new ArrayList<>();

        // 1. SINGLE CORE SCORE
        long start = System.nanoTime();
        runHeavyWorkload();
        long end = System.nanoTime();
        result.singleCoreSeconds = (end - start) / 1e9;

        // 2. MULTI CORE SCORE
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(cores);
        start = System.nanoTime();
        for (int i = 0; i < cores; i++) {
            executor.submit(CPUBenchmark::runHeavyWorkload);
        }
        executor.shutdown();
        try { executor.awaitTermination(60, TimeUnit.SECONDS); } catch (Exception e){}
        end = System.nanoTime();
        result.multiCoreSeconds = (end - start) / 1e9;

        // 3. GRAPH: PERCENTAGE STABILITY (Throttling Test)
        long testDuration = 5000;
        long testStart = System.currentTimeMillis();

        double baselineTime = 0;
        boolean isBaselineSet = false;

        while (System.currentTimeMillis() - testStart < testDuration) {
            long batchStart = System.nanoTime();

            // Run a batch
            for(int i=0; i<5; i++) runLightWorkload();

            long batchEnd = System.nanoTime();
            double currentTime = (batchEnd - batchStart); // In Nanoseconds

            // Set the first valid run as the 100% baseline
            if (!isBaselineSet) {
                baselineTime = currentTime;
                isBaselineSet = true;
                result.historyData.add(100f); // Starts at 100%
            } else {
                // Calculate percentage relative to baseline
                // If current takes 2x longer, score is 50%
                float percentage = (float) ((baselineTime / currentTime) * 100.0);

                // Cap at 100% (ignore minor random speedups)
                if (percentage > 100f) percentage = 100f;

                result.historyData.add(percentage);
            }
        }

        return result;
    }
}