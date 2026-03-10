package com.example.performancetester;

import com.example.performancetester.benchmark.IOBenchmark;
import com.example.performancetester.benchmark.RAMBenchmark;

public class BenchmarkScorer {

    // --- NEW CALIBRATION (Targeting Galaxy S8+ ~1000pts) ---

    // CPU: Older flagships (S8) take about 4-8 seconds for heavy workloads.
    // We set the reference to 5.0 seconds.
    // If a phone does it in 2.5s, it gets 2000 pts.
    private static final double REF_SINGLE_CORE_SEC = 5.0;
    private static final double REF_MULTI_CORE_SEC  = 2.0;

    // RAM: Java memory copy is slow. A decent Java copy speed is ~300 MB/s.
    // We lowered this from 10,000 to 300 to match Java's reality.
    private static final double REF_RAM_SPEED = 300.0;

    // I/O: 500 MB/s is still a good standard for UFS 2.1 storage.
    private static final double REF_IO_SPEED = 500.0;

    // GPU: The test is lightweight, so high FPS is expected.
    // We raise the bar from 60 FPS to 350 FPS to normalize the score.
    private static final double REF_GPU_FPS = 60.0;


    // --- SCORING FUNCTIONS ---

    public static int getSingleCoreScore(double timeSec) {
        if (timeSec < 0.001) timeSec = 0.001;
        return (int) (1000 * (REF_SINGLE_CORE_SEC / timeSec));
    }

    public static int getMultiCoreScore(double timeSec) {
        if (timeSec < 0.001) timeSec = 0.001;
        return (int) (1000 * (REF_MULTI_CORE_SEC / timeSec));
    }

    public static int getGpuScore(float avgFps) {
        // If FPS is 350, Score is 1000.
        return (int) (1000 * (avgFps / REF_GPU_FPS));
    }

    public static int getRamScore(RAMBenchmark.Result res) {
        // Average the three tests
        double avgSpeed = (res.rowMBps + res.colMBps + res.randomMBps) / 3.0;
        return (int) (1000 * (avgSpeed / REF_RAM_SPEED));
    }

    public static int getIoScore(IOBenchmark.Result res) {
        // Weighted average: Write 70%, Read 30%
        double effectiveSpeed = (res.writeMBps * 0.7) + (res.readMBps * 0.3);
        return (int) (1000 * (effectiveSpeed / REF_IO_SPEED));
    }
}