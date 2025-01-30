package com.sabbir.walton.mmitest.TestActivities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.sabbir.walton.mmitest.R;

public class MultiTouchTestActivity extends AppCompatActivity {

    private MultiTouchView multiTouchView;
    private TextView fingerCountTextView;
    private Button passButton;
    private Button failButton;
    private LinearLayout buttonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_touch);

        fingerCountTextView = findViewById(R.id.fingerCountTextView);
        //passButton = findViewById(R.id.passButton);
        //failButton = findViewById(R.id.failButton);
        buttonLayout = findViewById(R.id.buttonLayout);

        fingerCountTextView.setVisibility(View.INVISIBLE);
        //passButton.setVisibility(View.INVISIBLE);
        //failButton.setVisibility(View.INVISIBLE);

        // Allow layout to extend into the cutout area
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        ConstraintLayout rootLayout = findViewById(R.id.rootLayout);
        multiTouchView = new MultiTouchView(this);
        rootLayout.addView(multiTouchView); // Add the view to the layout
    }

    private class MultiTouchView extends View {
        private Paint paint = new Paint();
        private int[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.CYAN};
        private float[] xCoords = new float[10];
        private float[] yCoords = new float[10];
        private boolean isTesting = false;

        public MultiTouchView(Context context) {
            super(context);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(10); // Increase stroke width to 10
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for (int i = 0; i < xCoords.length; i++) {
                if (xCoords[i] != 0 && yCoords[i] != 0) {
                    paint.setColor(colors[i % colors.length]);
                    canvas.drawCircle(xCoords[i], yCoords[i], 100, paint); // Increase circle radius to 100
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int action = event.getActionMasked();
            int pointerCount = event.getPointerCount();

            if (action == MotionEvent.ACTION_DOWN) {
                isTesting = true;
                fingerCountTextView.setVisibility(View.VISIBLE);
               //passButton.setVisibility(View.INVISIBLE);
               // failButton.setVisibility(View.INVISIBLE);
            }

            if (isTesting) {
                fingerCountTextView.setText("Fingers: " + pointerCount);

                for (int i = 0; i < 10; i++) {
                    if (i < pointerCount) {
                        int id = event.getPointerId(i);
                        xCoords[id] = event.getX(i);
                        yCoords[id] = event.getY(i);
                    } else {
                        xCoords[i] = 0;
                        yCoords[i] = 0;
                    }
                }
                invalidate();
            }

            if (action == MotionEvent.ACTION_UP) {
                isTesting = false;
                fingerCountTextView.setText("Fingers: 0");
                for (int i = 0; i < 10; i++) {
                    xCoords[i] = 0;
                    yCoords[i] = 0;
                }
                invalidate();
                showConfirmationDialog();
            }

            return true;
        }
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Test Result");
        builder.setMessage("The multi-touch test pass or fail?");
        builder.setPositiveButton("Pass", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(Activity.RESULT_OK, new Intent().putExtra("testResult", true));
                finish();
            }
        });
        builder.setNegativeButton("Fail", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(Activity.RESULT_OK, new Intent().putExtra("testResult", false));
                finish();
            }
        });
        builder.show();
    }
}