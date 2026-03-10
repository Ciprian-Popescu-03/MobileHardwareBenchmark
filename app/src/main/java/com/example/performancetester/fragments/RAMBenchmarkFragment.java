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
import com.example.performancetester.benchmark.RAMBenchmark;

public class RAMBenchmarkFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_result, container, false);

        TextView tv = v.findViewById(R.id.text_result);
        Button btnRun = v.findViewById(R.id.btn_run);

        SimpleBarChart barChart = v.findViewById(R.id.bar_chart);
        SimpleLineChart lineChart = v.findViewById(R.id.line_chart);

        // Hide initially
        barChart.setVisibility(View.GONE);
        lineChart.setVisibility(View.GONE);

        // Hide headers initially (Safe check)
        View header1 = v.findViewById(R.id.tv_header_bars);
        if(header1 != null) header1.setVisibility(View.GONE);
        View header2 = v.findViewById(R.id.tv_header_line);
        if(header2 != null) header2.setVisibility(View.GONE);

        tv.setText("RAM Benchmark ready.");

        btnRun.setOnClickListener(view -> {
            btnRun.setEnabled(false);
            tv.setText("Running RAM Benchmark...");

            new Thread(() -> {
                RAMBenchmark.Result res = RAMBenchmark.run();
                int score = BenchmarkScorer.getRamScore(res);

                BenchmarkState state = BenchmarkState.getInstance();
                state.ramScore = String.valueOf(score);
                state.ramDetails = String.format("Row: %.0f | Col: %.0f | Rnd: %.0f MB/s",
                        res.rowMBps, res.colMBps, res.randomMBps);
                state.ramHistory = res.historyData;

                new Handler(Looper.getMainLooper()).post(() -> {
                    tv.setText("RAM Score: " + score + "\n\n" + state.ramDetails);

                    // 1. SHOW HEADERS
                    if(header1 != null) {
                        header1.setVisibility(View.VISIBLE);
                        ((TextView)header1).setText("Graph 1: Access Speeds (MB/s)");
                    }
                    if(header2 != null && res.historyData != null) {
                        header2.setVisibility(View.VISIBLE);
                    }

                    // 2. SETUP BAR CHART (The Speeds)
                    barChart.setVisibility(View.VISIBLE);
                    barChart.clear();
                    // Green for fast, Yellow for med, Red for slow
                    barChart.addBar("Row", (float)res.rowMBps, Color.parseColor("#4CAF50"));
                    barChart.addBar("Col", (float)res.colMBps, Color.parseColor("#FFC107"));
                    barChart.addBar("Rnd", (float)res.randomMBps, Color.parseColor("#F44336"));

                    // 3. SETUP LINE CHART (Stability)
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