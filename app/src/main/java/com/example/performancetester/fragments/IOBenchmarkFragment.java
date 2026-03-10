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
import com.example.performancetester.benchmark.IOBenchmark;

public class IOBenchmarkFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_result, container, false);

        TextView tv = v.findViewById(R.id.text_result);
        Button btnRun = v.findViewById(R.id.btn_run);

        SimpleBarChart barChart = v.findViewById(R.id.bar_chart);
        SimpleLineChart lineChart = v.findViewById(R.id.line_chart);

        barChart.setVisibility(View.GONE);
        lineChart.setVisibility(View.GONE);

        // Hide headers
        View header1 = v.findViewById(R.id.tv_header_bars);
        if(header1 != null) header1.setVisibility(View.GONE);
        View header2 = v.findViewById(R.id.tv_header_line);
        if(header2 != null) header2.setVisibility(View.GONE);

        tv.setText("Disk I/O Benchmark ready.");

        btnRun.setOnClickListener(view -> {
            btnRun.setEnabled(false);
            tv.setText("Running Disk I/O Benchmark...");

            new Thread(() -> {
                IOBenchmark.Result res = IOBenchmark.run(getContext());
                int score = BenchmarkScorer.getIoScore(res);

                BenchmarkState state = BenchmarkState.getInstance();
                state.ioScore = String.valueOf(score);
                state.ioDetails = String.format("Write: %.0f | Read: %.0f MB/s",
                        res.writeMBps, res.readMBps);
                state.ioHistory = res.historyData;

                new Handler(Looper.getMainLooper()).post(() -> {
                    tv.setText("Disk Score: " + score + "\n\n" + state.ioDetails);

                    // 1. SHOW HEADERS
                    if(header1 != null) {
                        header1.setVisibility(View.VISIBLE);
                        ((TextView)header1).setText("Graph 1: Read vs Write (MB/s)");
                    }
                    if(header2 != null && res.historyData != null) {
                        header2.setVisibility(View.VISIBLE);
                    }

                    // 2. SETUP BAR CHART
                    barChart.setVisibility(View.VISIBLE);
                    barChart.clear();
                    // Blue for Read, Orange for Write
                    barChart.addBar("Write", (float)res.writeMBps, Color.parseColor("#FF9800"));
                    barChart.addBar("Read", (float)res.readMBps, Color.parseColor("#2196F3"));

                    // 3. SETUP LINE CHART
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