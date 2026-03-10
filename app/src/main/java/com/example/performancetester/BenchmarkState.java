package com.example.performancetester;

import com.example.performancetester.benchmark.BatteryBenchmark;
import java.util.ArrayList;
import java.util.List;

public class BenchmarkState {
    private static BenchmarkState instance;

    // Scores & Text
    public String cpuScore = "0"; // Changed default to "0" for easier parsing
    public String cpuSingle = "-";
    public String cpuMulti = "-";

    public String gpuScore = "0";
    public String gpuDetails = "-";

    public String ramScore = "0";
    public String ramDetails = "-";

    public String ioScore = "0";
    public String ioDetails = "-";

    public String systemInfo = "Loading...";

    // Graph Data
    public List<Float> cpuHistory = new ArrayList<>();
    public List<Float> ramHistory = new ArrayList<>();
    public List<Float> ioHistory = new ArrayList<>();
    public List<Float> gpuHistory = new ArrayList<>();

    // Battery
    public BatteryBenchmark.Result startBattery;
    public BatteryBenchmark.Result endBattery;
    public boolean isTesting = false;

    public static BenchmarkState getInstance() {
        if (instance == null) instance = new BenchmarkState();
        return instance;
    }

    // --- NEW: Calculate Total Score ---
    // Inside BenchmarkState.java

    public int getOverallScore() {
        int cpu = safeParse(cpuScore);
        int gpu = safeParse(gpuScore);
        int ram = safeParse(ramScore);
        int io = safeParse(ioScore);

        int validTests = 0;
        if (cpu > 0) validTests++;
        if (gpu > 0) validTests++;
        if (ram > 0) validTests++;
        if (io > 0) validTests++;

        if (validTests == 0) return 0;

        // Returns an average.
        // A perfectly "average" phone will score 1000 overall.
        return (cpu + gpu + ram + io) / validTests;
    }

    private int safeParse(String val) {
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return 0; // If text is "Running..." or "-", return 0
        }
    }
}