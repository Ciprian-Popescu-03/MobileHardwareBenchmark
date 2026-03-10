package com.example.performancetester;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class SimpleLineChart extends View {

    private List<Float> dataPoints = new ArrayList<>();
    private Paint linePaint = new Paint();
    private Paint gridPaint = new Paint();
    private Path path = new Path();

    public SimpleLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Setup Line Style
        linePaint.setColor(Color.parseColor("#FF6200EE")); // Purple line
        linePaint.setStrokeWidth(8f);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);

        // Setup Grid/Text Style
        gridPaint.setColor(Color.LTGRAY);
        gridPaint.setTextSize(30f);
        gridPaint.setStrokeWidth(2f);
    }

    public void setData(List<Float> points) {
        this.dataPoints = new ArrayList<>(points);
        invalidate(); // Trigger redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (dataPoints == null || dataPoints.size() < 2) return;

        float width = getWidth();
        float height = getHeight();
        float padding = 50f;

        // 1. Find Min and Max to scale the graph
        float min = dataPoints.get(0);
        float max = dataPoints.get(0);
        for (float f : dataPoints) {
            if (f < min) min = f;
            if (f > max) max = f;
        }

        // Add a little buffer so the line doesn't touch the top/bottom exactly
        float range = max - min;
        if (range == 0) range = 1; // Avoid divide by zero

        // 2. Build the Line Path
        path.reset();

        // Calculate X spacing (width divided by number of points)
        float stepX = (width - 2 * padding) / (dataPoints.size() - 1);

        for (int i = 0; i < dataPoints.size(); i++) {
            float val = dataPoints.get(i);

            // Normalize value to 0..1 range, then flip Y (because 0 is top in Android)
            float normalized = (val - min) / range;
            float x = padding + (i * stepX);
            float y = (height - padding) - (normalized * (height - 2 * padding));

            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }

            // Optional: Draw a small dot at each point
            canvas.drawCircle(x, y, 6f, linePaint);
        }

        // 3. Draw the Line
        canvas.drawPath(path, linePaint);

        // 4. Draw Simple Labels (Min and Max)
        gridPaint.setColor(Color.BLACK);
        canvas.drawText("Max: " + (int)max, padding, padding - 10, gridPaint);
        canvas.drawText("Min: " + (int)min, padding, height - 10, gridPaint);
    }
}