package com.example.performancetester.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.performancetester.BenchmarkState;
import com.example.performancetester.R;
import com.example.performancetester.SystemInfo;

public class SystemInfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_result, container, false);

        // Hide benchmark-specific UI elements safely
        hideSafe(v, R.id.btn_run);
        hideSafe(v, R.id.bar_chart);
        hideSafe(v, R.id.line_chart);
        hideSafe(v, R.id.gl_view);
        hideSafe(v, R.id.tv_header_bars);
        hideSafe(v, R.id.tv_header_line);

        TextView tv = v.findViewById(R.id.text_result);
        if (tv != null) {
            updateInfoText(tv);
        }
        return v;
    }

    // Called every time the user clicks the "System" tab
    @Override
    public void onResume() {
        super.onResume();
        View v = getView();
        if (v != null) {
            TextView tv = v.findViewById(R.id.text_result);
            if (tv != null) updateInfoText(tv);
        }
    }

    private void updateInfoText(TextView tv) {
        SystemInfo sys = new SystemInfo(getContext());
        BenchmarkState state = BenchmarkState.getInstance();
        int totalScore = state.getOverallScore();

        StringBuilder sb = new StringBuilder();

        // --- SECTION 1: OVERALL SCORE ---
        sb.append("==========================\n");
        sb.append("   OVERALL SCORE: ").append(totalScore).append("\n");
        sb.append("==========================\n\n");

        // Breakdown
        sb.append("CPU: ").append(state.cpuScore).append(" pts\n");
        sb.append("GPU: ").append(state.gpuScore).append(" pts\n");
        sb.append("RAM: ").append(state.ramScore).append(" pts\n");
        sb.append("I/O:  ").append(state.ioScore).append(" pts\n\n");

        // --- SECTION 2: HARDWARE SPECS ---
        sb.append("--- Hardware Specs ---\n\n");
        sb.append("Processor:\n").append(sys.getCpuInfo()).append("\n\n");
        sb.append("Graphics (GPU):\n").append(sys.getGpuInfo()).append("\n\n");
        sb.append("Memory (RAM):\n").append(sys.getRamInfo()).append("\n\n");
        sb.append("Storage:\n").append(sys.getStorageInfo()).append("\n\n");
        sb.append("Battery:\n").append(sys.getBatteryInfo());

        tv.setText(sb.toString());
    }

    private void hideSafe(View parent, int id) {
        View v = parent.findViewById(id);
        if (v != null) v.setVisibility(View.GONE);
    }
}