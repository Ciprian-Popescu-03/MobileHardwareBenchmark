package com.example.performancetester.fragments;

import android.graphics.Color;
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
import com.example.performancetester.SimpleBarChart;
import com.example.performancetester.SimpleLineChart;
import com.example.performancetester.benchmark.CPUBenchmark;

public class CPUBenchmarkFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_result, container, false);

        TextView tv = v.findViewById(R.id.text_result);
        Button btnRun = v.findViewById(R.id.btn_run);

        SimpleBarChart barChart = v.findViewById(R.id.bar_chart);
        SimpleLineChart lineChart = v.findViewById(R.id.line_chart);

        // Hide charts initially
        barChart.setVisibility(View.GONE);
        lineChart.setVisibility(View.GONE);

        tv.setText("CPU Benchmark ready.");

        btnRun.setOnClickListener(view -> {
            btnRun.setEnabled(false);
            barChart.setVisibility(View.GONE);
            lineChart.setVisibility(View.GONE);
            tv.setText("Running CPU Benchmark... Please wait.");

            new Thread(() -> {
                CPUBenchmark.Result res = CPUBenchmark.run();

                int sSingle = BenchmarkScorer.getSingleCoreScore(res.singleCoreSeconds);
                int sMulti = BenchmarkScorer.getMultiCoreScore(res.multiCoreSeconds);
                // Weighted Average: Multi-core counts for 60%, Single-core for 40%
                int total = (int) ((sSingle * 0.4) + (sMulti * 0.6));

                BenchmarkState state = BenchmarkState.getInstance();
                state.cpuScore = String.valueOf(total);

                // ADDED SECONDS HERE
                state.cpuSingle = String.format("Single: %d (%.2fs)", sSingle, res.singleCoreSeconds);
                state.cpuMulti = String.format("Multi: %d (%.2fs)", sMulti, res.multiCoreSeconds);
                state.cpuHistory = res.historyData;

                new Handler(Looper.getMainLooper()).post(() -> {
                    // Update Text to show Score AND Time
                    tv.setText("Total Score: " + total + "\n\n" +
                            state.cpuSingle + "\n" +
                            state.cpuMulti);

                    barChart.setVisibility(View.VISIBLE);
                    barChart.clear();
                    barChart.addBar("Single", sSingle, Color.parseColor("#2196F3"));
                    barChart.addBar("Multi", sMulti, Color.parseColor("#FF9800"));

                    if (res.historyData != null && !res.historyData.isEmpty()) {
                        lineChart.setVisibility(View.VISIBLE);
                        lineChart.setData(res.historyData);
                    }

                    btnRun.setEnabled(true);
                });
            }).start();
        });

        return v;
    }
}