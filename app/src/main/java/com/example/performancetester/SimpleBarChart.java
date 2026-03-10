package com.example.performancetester;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class SimpleBarChart extends View {

    private final List<BarItem> items = new ArrayList<>();
    private final Paint paint = new Paint();
    private float maxValue = 0;

    public static class BarItem {
        String label;
        float value;
        int color;

        public BarItem(String label, float value, int color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }
    }

    public SimpleBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addBar(String label, float value, int color) {
        items.add(new BarItem(label, value, color));
        // Update max value to scale the bars correctly
        if (value > maxValue) maxValue = value;
        invalidate();
    }

    public void clear() {
        items.clear();
        maxValue = 0;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (items.isEmpty()) return;

        float width = getWidth();
        float height = getHeight();
        // Add padding so text doesn't get cut off
        float paddingBottom = 100f;
        float paddingTop = 50f;

        // Calculate dynamic width for bars
        float barWidth = (width / items.size()) * 0.6f;
        float spacing = (width / items.size());

        paint.setTextSize(40);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);

        // Prevent divide by zero if max is 0
        float safeMax = (maxValue == 0) ? 1 : maxValue;

        for (int i = 0; i < items.size(); i++) {
            BarItem item = items.get(i);

            // Calculate bar height relative to the max score
            float availableHeight = height - paddingBottom - paddingTop;
            float barHeight = (item.value / safeMax) * availableHeight;

            float cx = (i * spacing) + (spacing / 2);
            float left = cx - (barWidth / 2);
            float right = cx + (barWidth / 2);
            float top = height - paddingBottom - barHeight;
            float bottom = height - paddingBottom;

            // 1. Draw Bar
            paint.setColor(item.color);
            canvas.drawRect(left, top, right, bottom, paint);

            // 2. Draw Value (Points) on TOP of the bar
            paint.setColor(Color.BLACK);
            paint.setFakeBoldText(true);
            canvas.drawText(String.valueOf((int)item.value), cx, top - 15, paint);

            // 3. Draw Label (e.g., "Single-Core") BELOW the bar
            paint.setFakeBoldText(false);
            canvas.drawText(item.label, cx, height - 40, paint);
        }
    }
}