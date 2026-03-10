package com.example.performancetester.fragments;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.example.performancetester.BenchmarkScorer;
import com.example.performancetester.BenchmarkState;
import com.example.performancetester.R;
import com.example.performancetester.SimpleLineChart;
import com.example.performancetester.benchmark.RealGPURenderer;
import java.util.ArrayList;
import java.util.List;

public class GPUBenchmarkFragment extends Fragment {

    private GLSurfaceView glView;
    private RealGPURenderer renderer;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_result, container, false);

        TextView tv = v.findViewById(R.id.text_result);
        Button btnRun = v.findViewById(R.id.btn_run);
        SimpleLineChart chart = v.findViewById(R.id.line_chart);

        // Find the GLSurfaceView (Make sure it exists in XML as we added before)
        glView = v.findViewById(R.id.gl_view);

        // Initial State
        chart.setVisibility(View.GONE);
        glView.setVisibility(View.GONE);
        tv.setText("GPU Benchmark\n\nTest: 3D Grid Rendering");

        btnRun.setOnClickListener(view -> startBenchmark(btnRun, tv, chart));

        return v;
    }

    private void startBenchmark(Button btnRun, TextView tv, SimpleLineChart chart) {
        btnRun.setEnabled(false);
        chart.setVisibility(View.GONE);
        glView.setVisibility(View.VISIBLE);

        // Initialize Renderer
        glView.setEGLContextClientVersion(2);
        renderer = new RealGPURenderer();
        glView.setRenderer(renderer);

        tv.setText("Running Graphics Test...");

        new Thread(() -> {
            List<Float> fpsHistory = new ArrayList<>();
            long startTime = System.currentTimeMillis();
            long lastCheck = startTime;
            long lastFrames = 0;

            // Run for 8 seconds
            while (System.currentTimeMillis() - startTime < 8000) {
                try { Thread.sleep(500); } catch (InterruptedException e) {}

                long now = System.currentTimeMillis();
                long currentFrames = renderer.frameCount;

                // Calculate FPS
                double elapsedSeconds = (now - lastCheck) / 1000.0;
                float fps = (float) ((currentFrames - lastFrames) / elapsedSeconds);

                lastCheck = now;
                lastFrames = currentFrames;
                fpsHistory.add(fps);

                // Update UI text
                handler.post(() -> tv.setText("Rendering... FPS: " + (int)fps));
            }

            // Finish
            float avgFps = 0;
            for(float f : fpsHistory) avgFps += f;
            if(!fpsHistory.isEmpty()) avgFps /= fpsHistory.size();

            int score = BenchmarkScorer.getGpuScore(avgFps);

            // Save
            BenchmarkState state = BenchmarkState.getInstance();
            state.gpuScore = String.valueOf(score);
            state.gpuDetails = String.format("Avg FPS: %.1f", avgFps);
            state.gpuHistory = fpsHistory;

            handler.post(() -> {
                glView.setVisibility(View.GONE);
                chart.setVisibility(View.VISIBLE);
                chart.setData(fpsHistory);
                tv.setText("GPU Score: " + score + "\n\n" + state.gpuDetails);
                btnRun.setEnabled(true);
            });
        }).start();
    }

}