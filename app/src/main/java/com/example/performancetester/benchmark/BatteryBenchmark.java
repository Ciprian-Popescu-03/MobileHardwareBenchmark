package com.example.performancetester.benchmark;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryBenchmark {

    public static class Result {
        public int level;
        public float temperature; // Celsius
        public int voltage;       // mV
        public boolean isCharging;

        public Result(int level, float temperature, int voltage, boolean isCharging) {
            this.level = level;
            this.temperature = temperature;
            this.voltage = voltage;
            this.isCharging = isCharging;
        }
    }

    public static Result run(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        int level = 0;
        float temp = 0;
        int volt = 0;
        boolean charging = false;

        if (batteryStatus != null) {
            // Level
            int rawLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            level = (int) (rawLevel * 100.0 / scale);

            // Temperature (Provided in tenths of a degree Celsius, e.g., 350 = 35.0C)
            int rawTemp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            temp = rawTemp / 10.0f;

            // Voltage (mV)
            volt = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

            // Status
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            charging = (status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL);
        }

        return new Result(level, temp, volt, charging);
    }
}