package com.example.performancetester;

import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SystemInfo {

    private final Context context;

    public SystemInfo(Context context) {
        this.context = context;
    }

    public String getCpuInfo() {
        String model = null;

        // Try reading from /proc/cpuinfo
        try (BufferedReader br = new BufferedReader(new FileReader("/proc/cpuinfo"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.toLowerCase().contains("hardware") ||
                        line.toLowerCase().contains("model name") ||
                        line.toLowerCase().contains("processor")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length > 1) {
                        model = parts[1].trim();
                    }
                    break;
                }
            }
        } catch (IOException ignored) {}

        // Fallback for missing info
        if (model == null || model.equals("0") || model.isEmpty()) {
            String socModel = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                try {
                    socModel = Build.SOC_MODEL;
                } catch (Exception ignored) {}
            }
            if (socModel == null || socModel.isEmpty()) {
                socModel = Build.HARDWARE;
            }
            model = Build.MANUFACTURER.toUpperCase() + " " + socModel;
        }

        int cores = Runtime.getRuntime().availableProcessors();
        return model + " | " + cores + " cores";
    }

    public String getGpuInfo() {
        try {
            EGLDisplay display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
            int[] version = new int[2];
            EGL14.eglInitialize(display, version, 0, version, 1);

            int[] attribList = { EGL14.EGL_RENDERABLE_TYPE, 4, EGL14.EGL_NONE };
            EGLConfig[] configs = new EGLConfig[1];
            int[] numConfigs = new int[1];
            EGL14.eglChooseConfig(display, attribList, 0, configs, 0, 1, numConfigs, 0);
            EGLConfig config = configs[0];

            int[] contextAttribs = {0x3098, 2, EGL14.EGL_NONE}; // OpenGL ES 2
            EGLContext context = EGL14.eglCreateContext(display, config, EGL14.EGL_NO_CONTEXT, contextAttribs, 0);

            int[] surfaceAttribs = { EGL14.EGL_WIDTH, 1, EGL14.EGL_HEIGHT, 1, EGL14.EGL_NONE };
            EGLSurface surface = EGL14.eglCreatePbufferSurface(display, config, surfaceAttribs, 0);

            EGL14.eglMakeCurrent(display, surface, surface, context);

            String renderer = GLES20.glGetString(GLES20.GL_RENDERER);
            String vendor = GLES20.glGetString(GLES20.GL_VENDOR);
            String versionStr = GLES20.glGetString(GLES20.GL_VERSION);

            EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroySurface(display, surface);
            EGL14.eglDestroyContext(display, context);
            EGL14.eglTerminate(display);

            return vendor + " " + renderer + " (" + versionStr.split(" ")[0] + ")";
        } catch (Exception e) {
            e.printStackTrace();
            return "GPU info unavailable";
        }
    }

    public String getRamInfo() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);

        long totalMb = mi.totalMem / (1024 * 1024);
        return totalMb + " MB total RAM";
    }

    public String getBatteryInfo() {
        BatteryManager bm = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        int level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        return "Battery: " + level + "%";
    }

    public String getStorageInfo() {
        File path = Environment.getDataDirectory();
        long freeBytes = path.getFreeSpace();
        long totalBytes = path.getTotalSpace();

        long freeGB = freeBytes / (1024 * 1024 * 1024);
        long totalGB = totalBytes / (1024 * 1024 * 1024);

        return "Storage: " + freeGB + " GB free / " + totalGB + " GB total";
    }
}
