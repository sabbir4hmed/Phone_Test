package com.sabbir.walton.mmitest.TestActivities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sabbir.walton.mmitest.R;

public class Phonediagnostic extends AppCompatActivity {
    // Button declarations with more consistent naming
    private Button simCardButton, sdCardButton, lcdButton, multiTouchButton;
    private Button singleTouchButton, flashLightButton, keyButton, speakerButton;
    private Button receiverButton, earphoneButton, micButton, callButton;
    private Button vibrationButton, gpsButton, wifiButton, bluetoothButton;
    private Button fingerprintButton, batteryButton, rearCameraButton, frontCameraButton;
    private Button proximitySensorButton, chargingButton, fullTestButton;
    private Button textButton, reportButton;

    private boolean isFullTestRunning = false;
    private int currentTestIndex = 0;

    // Array of test activities in order
    private final Class<?>[] TEST_ACTIVITIES = {
            SimCardTestActivity.class,
            SdCardTestActivity.class,
            LcdTestActivity.class,
            MultiTouchTestActivity.class,
            SingleTouchTestActivity.class,
            FlashLightTestActivity.class,
            ButtonTestActivity.class,
            SpeakerTestActivity.class,
            RecieverTestActivity.class,
            EarphoneTestActivity.class,
            MicTestActivity.class,
            CallTestActivity.class,
            VibrationTestActivity.class,
            GpsTestActivity.class,
            WifiTestActivity.class,
            BlutoothTestActivity.class,
            FingerprintTestActivity.class,
            BatteryStatusTestActivity.class,
            RearCameraTestActivity.class,
            FrontCameraTestActivity.class,
            ProximitySensorTestActivity.class,
            ChargingTestActivity.class
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_diagnostic);
        initializeButtons();
        setupButtonListeners();
    }

    private void initializeButtons() {
        simCardButton = findViewById(R.id.simCardTestButton);
        sdCardButton = findViewById(R.id.sdCardTestButton);
        lcdButton = findViewById(R.id.lcdTestButton);
        multiTouchButton = findViewById(R.id.multiTouchTestButton);
        singleTouchButton = findViewById(R.id.singleTouchTestButton);
        flashLightButton = findViewById(R.id.flashLightTestButton);
        keyButton = findViewById(R.id.keyTestButton);
        speakerButton = findViewById(R.id.speakerTestButton);
        receiverButton = findViewById(R.id.recieverTestButton);
        earphoneButton = findViewById(R.id.earphoneTestButton);
        micButton = findViewById(R.id.micTestButton);
        callButton = findViewById(R.id.callTestButton);
        vibrationButton = findViewById(R.id.vibrationTestButton);
        gpsButton = findViewById(R.id.gpsTestButton);
        wifiButton = findViewById(R.id.wifiTestButton);
        bluetoothButton = findViewById(R.id.BTTestButton);
        fingerprintButton = findViewById(R.id.fPTestButton);
        batteryButton = findViewById(R.id.bttryTestButton);
        rearCameraButton = findViewById(R.id.rcTestButton);
        frontCameraButton = findViewById(R.id.fcTestButton);
        proximitySensorButton = findViewById(R.id.PsTestButton);
        chargingButton = findViewById(R.id.ctTestButton);
        fullTestButton = findViewById(R.id.fullTestButton);
        textButton = findViewById(R.id.textButton);
        reportButton = findViewById(R.id.reportButton);
    }

    private void setupButtonListeners() {
        // Set individual test button listeners
        for (int i = 0; i < TEST_ACTIVITIES.length; i++) {
            final int testIndex = i;
            getButtonForTest(testIndex).setOnClickListener(v ->
                    startTest(TEST_ACTIVITIES[testIndex], testIndex));
        }

        // Set full test button listener
        fullTestButton.setOnClickListener(v -> startFullTestSequence());

        // Set text and report button listeners
        textButton.setOnClickListener(v -> handleTextButton());
        reportButton.setOnClickListener(v -> handleReportButton());
    }

    private void startTest(Class<?> testActivity, int requestCode) {
        Intent intent = new Intent(this, testActivity);
        startActivityForResult(intent, requestCode);
    }

    private void startFullTestSequence() {
        if (isFullTestRunning) {
            Toast.makeText(this, "Full Test already in progress", Toast.LENGTH_SHORT).show();
            return;
        }
        isFullTestRunning = true;
        currentTestIndex = 0;
        setImmersiveMode(true);
        startNextTest();
    }

    private void startNextTest() {
        if (currentTestIndex < TEST_ACTIVITIES.length) {
            startTest(TEST_ACTIVITIES[currentTestIndex], currentTestIndex);
        } else {
            completeFullTest();
        }
    }

    private void completeFullTest() {
        isFullTestRunning = false;
        setImmersiveMode(false);
        Toast.makeText(this, "Full Test completed", Toast.LENGTH_SHORT).show();
    }

    private void setImmersiveMode(boolean enable) {
        View decorView = getWindow().getDecorView();
        if (enable) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        } else {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.hasExtra("testResult")) {
            updateTestResult(requestCode, data.getBooleanExtra("testResult", false));
        }

        if (isFullTestRunning) {
            currentTestIndex++;
            startNextTest();
        }
    }

    private void updateTestResult(int testIndex, boolean passed) {
        Button testButton = getButtonForTest(testIndex);
        if (testButton != null) {
            testButton.setBackgroundColor(passed ? Color.GREEN : Color.RED);
        }
    }

    private Button getButtonForTest(int index) {
        switch (index) {
            case 0: return simCardButton;
            case 1: return sdCardButton;
            case 2: return lcdButton;
            case 3: return multiTouchButton;
            case 4: return singleTouchButton;
            case 5: return flashLightButton;
            case 6: return keyButton;
            case 7: return speakerButton;
            case 8: return receiverButton;
            case 9: return earphoneButton;
            case 10: return micButton;
            case 11: return callButton;
            case 12: return vibrationButton;
            case 13: return gpsButton;
            case 14: return wifiButton;
            case 15: return bluetoothButton;
            case 16: return fingerprintButton;
            case 17: return batteryButton;
            case 18: return rearCameraButton;
            case 19: return frontCameraButton;
            case 20: return proximitySensorButton;
            case 21: return chargingButton;
            default: return null;
        }
    }

    private void handleTextButton() {
        // Implement text button functionality
        Toast.makeText(this, "Text button clicked", Toast.LENGTH_SHORT).show();
    }

    private void handleReportButton() {
        // Implement report button functionality
        Toast.makeText(this, "Report button clicked", Toast.LENGTH_SHORT).show();
    }
}

