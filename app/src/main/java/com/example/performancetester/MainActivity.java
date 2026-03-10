package com.example.performancetester;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import com.example.performancetester.fragments.*;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager2 viewPager = findViewById(R.id.pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        viewPager.setAdapter(new ViewPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("System"); break;
                case 1: tab.setText("CPU"); break;
                case 2: tab.setText("GPU"); break;
                case 3: tab.setText("RAM"); break;
                case 4: tab.setText("I/O"); break;
                case 5: tab.setText("Battery"); break;
            }
        }).attach();
    }

    static class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(@NonNull FragmentActivity fa) { super(fa); }

        @NonNull @Override public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new SystemInfoFragment();
                case 1: return new CPUBenchmarkFragment();
                case 2: return new GPUBenchmarkFragment();
                case 3: return new RAMBenchmarkFragment();
                case 4: return new IOBenchmarkFragment();
                case 5: return new BatteryBenchmarkFragment();
                default: return new SystemInfoFragment();
            }
        }
        @Override public int getItemCount() { return 6; }
    }
}