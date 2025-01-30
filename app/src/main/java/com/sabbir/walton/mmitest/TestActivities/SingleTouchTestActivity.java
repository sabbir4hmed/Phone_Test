package com.sabbir.walton.mmitest.TestActivities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.sabbir.walton.mmitest.R;

public class SingleTouchTestActivity extends AppCompatActivity {

    private DrawingView drawingView;
    private AlertDialog resultDialog;
    private AlertDialog startDrawingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_touch_test);

        drawingView = findViewById(R.id.drawing_view);
        drawingView.setOnFullScreenCoveredListener(isCovered -> showResultDialog());

        // Allow layout to extend into the cutout area
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        // Apply Immersive Mode
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_VISIBLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // Show the initial alert dialog
        showStartDrawingDialog();
    }

    private void showStartDrawingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Alert!")
                .setMessage("Please start drawing on the screen. Do not stop drawing until test completion .")
                .setPositiveButton("Start", (dialog, which) -> {
                    // User chooses to start drawing
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Handle cancellation (if needed)
                    finish(); // Optionally close the activity
                })
                .setCancelable(false);

        startDrawingDialog = builder.create();
        startDrawingDialog.show();
    }

    private void showResultDialog() {
        if (isFinishing()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Test Result")
                .setMessage("Did the test pass?")
                .setPositiveButton("Pass", (dialog, which) -> {
                    returnResult(true);
                })
                .setNegativeButton("Fail", (dialog, which) -> {
                    returnResult(false);
                })
                .setCancelable(false);

        resultDialog = builder.create();
        resultDialog.show();
    }

    @Override
    protected void onDestroy() {
        if (resultDialog != null && resultDialog.isShowing()) {
            resultDialog.dismiss();
        }
        if (startDrawingDialog != null && startDrawingDialog.isShowing()) {
            startDrawingDialog.dismiss();
        }
        super.onDestroy();
    }

    private void returnResult(boolean result) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("testResult", result);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}