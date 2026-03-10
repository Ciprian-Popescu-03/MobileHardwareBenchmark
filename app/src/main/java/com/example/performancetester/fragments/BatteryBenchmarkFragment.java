package com.example.performancetester.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.performancetester.R;
import com.example.performancetester.benchmark.BatteryBenchmark;

public class BatteryBenchmarkFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_result, container, false);

        TextView tv = v.findViewById(R.id.text_result);
        Button btnRun = v.findViewById(R.id.btn_run);

        // Hide charts for battery (we just use text)
        v.findViewById(R.id.line_chart).setVisibility(View.GONE);

        tv.setText("Battery Benchmark ready.\n\nThis test will stress the CPU for 15 seconds to measure battery response (Voltage/Temp).");
        btnRun.setVisibility(View.VISIBLE);

        btnRun.setOnClickListener(view -> {
            btnRun.setEnabled(false);
            tv.setText("Running Battery Stress Test (30s)...\nPlease wait.");

            new Thread(() -> {
                // 1. Capture Start State
                BatteryBenchmark.Result start = BatteryBenchmark.run(getContext());

                // 2. Run Stress Load (Heavy CPU work)
                long endTime = System.currentTimeMillis() + 30000; // 30 Seconds
                while (System.currentTimeMillis() < endTime) {
                    // Burn CPU cycles
                    Math.sin(Math.random());
                    Math.sqrt(Math.random());
                }

                // 3. Capture End State
                BatteryBenchmark.Result end = BatteryBenchmark.run(getContext());

                // 4. Update UI
                new Handler(Looper.getMainLooper()).post(() -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("=== Battery Test Results ===\n\n");

                    sb.append("Level: ").append(start.level).append("% -> ").append(end.level).append("%\n");
                    sb.append("   Drop: ").append(start.level - end.level).append("%\n\n");

                    sb.append("Temp:  ").append(start.temperature).append("°C -> ").append(end.temperature).append("°C\n");
                    sb.append("   Change: ").append(String.format("%.1f", end.temperature - start.temperature)).append("°C\n\n");

                    sb.append("Voltage: ").append(start.voltage).append("mV -> ").append(end.voltage).append("mV\n");
                    sb.append("   Diff: ").append(end.voltage - start.voltage).append("mV\n\n");

                    sb.append("Charging: ").append(end.isCharging ? "Yes" : "No");

                    tv.setText(sb.toString());
                    btnRun.setEnabled(true);
                });
            }).start();
        });

        return v;
    }
}