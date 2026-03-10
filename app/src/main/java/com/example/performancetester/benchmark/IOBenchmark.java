package com.example.performancetester.benchmark;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IOBenchmark {

    public static class Result {
        public double writeMBps;
        public double readMBps;
        public List<Float> historyData;
    }

    public static Result run(Context context) {
        Result result = new Result();
        result.historyData = new ArrayList<>();

        final int FILE_SIZE_MB = 40;
        byte[] buffer = new byte[1024 * 1024]; // 1MB Buffer
        new Random().nextBytes(buffer);

        File testFile = new File(context.getCacheDir(), "io_benchmark.tmp");

        try {
            // --- WRITE TEST ---
            long start = System.nanoTime();
            try (FileOutputStream fos = new FileOutputStream(testFile)) {
                for (int i = 0; i < FILE_SIZE_MB; i++) {
                    long chunkStart = System.nanoTime();

                    fos.write(buffer); // Write 1MB

                    // Measure speed of this chunk
                    long chunkTimeNs = System.nanoTime() - chunkStart;
                    double chunkSpeedMBps = 1.0 / (chunkTimeNs / 1e9);
                    result.historyData.add((float) chunkSpeedMBps);
                }
            }
            long end = System.nanoTime();
            result.writeMBps = FILE_SIZE_MB / ((end - start) / 1e9);

            // --- READ TEST ---
            start = System.nanoTime();
            try (FileInputStream fis = new FileInputStream(testFile)) {
                byte[] readBuf = new byte[1024 * 1024];
                while (fis.read(readBuf) != -1) {
                    // We just consume data here,
                    // Reading is usually too fast to measure chunk-by-chunk
                    // accurately without adding overhead, so we skip adding points
                    // or just add one average point.
                }
            }
            end = System.nanoTime();
            result.readMBps = FILE_SIZE_MB / ((end - start) / 1e9);

            // Add final read speed to graph
            result.historyData.add((float) result.readMBps);

            testFile.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}