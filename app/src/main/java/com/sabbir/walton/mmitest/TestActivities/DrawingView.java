package com.sabbir.walton.mmitest.TestActivities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;

public class DrawingView extends View {

    private Paint paint;
    private boolean[][] coveredPixels;
    private OnFullScreenCoveredListener listener;

    public interface OnFullScreenCoveredListener {
        void onFullScreenCovered(boolean isCovered);
    }

    public DrawingView(Context context) {
        super(context);
        init(context, null);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
    }

    public void setOnFullScreenCoveredListener(OnFullScreenCoveredListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        coveredPixels = new boolean[w][h];

        // Get Notch Insets (Optional)
        WindowInsets insets = getRootWindowInsets();
        if (insets != null) {
            // Use insets.getInsets(WindowInsets.Type.systemBars()) to get the notch area
            // ... adjust your coverage logic based on these insets
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        // Draw something to visualize the touch
        paint.setColor(Color.GREEN);
        float dotSize = 150; // Use a fixed value instead of referencing a resource

        for (int x = 0; x < coveredPixels.length; x++) {
            for (int y = 0; y < coveredPixels[x].length; y++) {
                if (coveredPixels[x][y]) {
                    canvas.drawCircle(x, y, dotSize / 2, paint); // Draw circle with adjusted size
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()) {
                    coveredPixels[x][y] = true;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isFullScreenCovered()) {
                    if (listener != null) {
                        listener.onFullScreenCovered(true);
                    }
                } else {
                    if (listener != null) {
                        listener.onFullScreenCovered(false);
                    }
                }
                break;
        }
        return true;
    }

    private boolean isFullScreenCovered() {
        for (int x = 0; x < coveredPixels.length;x++) {
            for (int y = 0; y < coveredPixels[x].length; y++) {
                // Exclude the notch area from the check (if notch insets are obtained)
                // if (x >= notchLeft && x <= notchRight && y >= notchTop && y <= notchBottom) {
                //     continue;
                // }
                if (!coveredPixels[x][y]) {
                    return false;
                }
            }
        }
        return true;
    }
}