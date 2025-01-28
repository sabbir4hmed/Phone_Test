package com.sabbir.walton.mmitest.TestActivities;

import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class FlashLightTestActivity extends AppCompatActivity {

    private Button toggleFlashButton;
    private Button passButton;
    private Button failButton;
    private boolean isFlashOn = false;
    private CameraManager cameraManager;
    private String cameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flash_light_test);

        toggleFlashButton = findViewById(R.id.toggleFlashButton);
        passButton = findViewById(R.id.passButton);
        failButton = findViewById(R.id.failButton);

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        toggleFlashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFlashlight();
            }
        });

        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK, new Intent().putExtra("testResult", true));
                finish();
            }
        });

        failButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK, new Intent().putExtra("testResult", false));
                finish();
            }
        });

    }

    private void toggleFlashlight() {
        try {
            if (isFlashOn) {
                cameraManager.setTorchMode(cameraId, false);
                //toggleFlashButton.setText("Turn ON Flashlight");
                isFlashOn = false;
            } else {
                cameraManager.setTorchMode(cameraId, true);
                //toggleFlashButton.setText("Turn OFF Flashlight");
                isFlashOn = true;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Toast.makeText(this, "Flash Light Error!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ensure the flashlight is turned off when the activity is destroyed
        if (isFlashOn) {
            try {
                cameraManager.setTorchMode(cameraId, false);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }
}