package com.sabbir.walton.mmitest.TestActivities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LcdTestActivity extends AppCompatActivity {

    private ConstraintLayout rootLayout;
    private Button passButton;
    private Button failButton;
    private TextView resultTextView;
    private List<Integer> colors;
    private int currentColorIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Allow layout to extend into the cutout area
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        // Set up full screen and hide system UI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        setContentView(R.layout.activity_lcd_test);

        // Initialize views
        rootLayout = findViewById(R.id.rootLayout);
        passButton = findViewById(R.id.passButton);
        failButton = findViewById(R.id.failButton);
        resultTextView = findViewById(R.id.lcdInfoTextView);

        // Initialize color list and index
        colors = new ArrayList<>(Arrays.asList(
                Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
                Color.CYAN, Color.MAGENTA, Color.WHITE
        ));
        currentColorIndex = 0;

        // Initially hide the pass, fail buttons and result text view
        passButton.setVisibility(View.GONE);
        failButton.setVisibility(View.GONE);
        resultTextView.setVisibility(View.GONE);

        // Set the initial color
        rootLayout.setBackgroundColor(colors.get(currentColorIndex));

        // Handle background color changes on layout click
        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentColorIndex < colors.size() - 1) {
                    currentColorIndex++;
                    rootLayout.setBackgroundColor(colors.get(currentColorIndex));
                } else {
                    showResultUI();
                }
            }
        });

        // Handle pass button click
        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResultAndFinish(true);
            }
        });

        // Handle fail button click
        failButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResultAndFinish(false);
            }
        });
    }

    // Set result and finish activity
    private void setResultAndFinish(boolean testResult) {
        setResult(RESULT_OK, new Intent().putExtra("testResult", testResult));
        finish();
    }

    // Show pass and fail buttons and result text view
    private void showResultUI() {
        passButton.setVisibility(View.VISIBLE);
        failButton.setVisibility(View.VISIBLE);
        resultTextView.setVisibility(View.VISIBLE);
    }
}