package com.sabbir.walton.mmitest.TestActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class VibrationTestActivity extends AppCompatActivity {

    private Button vibrateButton, passButton, failButton;
   // private TextView statusTextView;
    private Vibrator vibrator;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibration_test);

        vibrateButton = findViewById(R.id.vibrate_button);
        passButton = findViewById(R.id.pass_button);
        failButton = findViewById(R.id.fail_button);
        //statusTextView = findViewById(R.id.status_textview);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        handler = new Handler(Looper.getMainLooper());

        vibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vibrator != null) {
                    // Vibrate 5 times with a 1-second interval
                    for (int i = 0; i < 1; i++) {
                        final int index = i;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                                    //statusTextView.setText("Is it Vibrated?");
                                } else {
                                    vibrator.vibrate(1000); // Deprecated in API 26
                                }
                                if (index == 1) {
                                    //statusTextView.setText("Is it Vibrated?");
                                }
                            }
                        }, i * 1000); // 2000 milliseconds (2 seconds) between vibrations
                    }
                }
            }
        });

        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vibrator != null) {
                    vibrator.cancel(); // Stop the vibration
                }
                setResult(RESULT_OK, new Intent().putExtra("testResult", true));
                finish();
            }
        });

        failButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vibrator != null) {
                    vibrator.cancel(); // Stop the vibration
                }
                setResult(RESULT_OK, new Intent().putExtra("testResult", false));
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vibrator != null) {
            vibrator.cancel(); // Ensure vibration is stopped when activity is destroyed
        }
    }
}