package com.sabbir.walton.mmitest.TestActivities;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sabbir.walton.mmitest.R;

public class ProximitySensorTestActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private TextView proximityValueText;
    private TextView instructionText;
    private Button passButton;
    private Button failButton;

    private boolean nearDetected = false;
    private boolean farDetected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity_sensor_test);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        proximityValueText = findViewById(R.id.proximity_value);
        instructionText = findViewById(R.id.instruction_text);
        passButton = findViewById(R.id.pass_button);
        failButton = findViewById(R.id.fail_button);

        if (proximitySensor == null) {
            proximityValueText.setText("Proximity sensor not available");
            passButton.setEnabled(false);
        } else {
            instructionText.setText("Place your hand close to the sensor, then move it away");
        }

        passButton.setOnClickListener(v -> {
            setResult(RESULT_OK, new Intent().putExtra("testResult", true));
            finish();
        });

        failButton.setOnClickListener(v -> {
            setResult(RESULT_OK, new Intent().putExtra("testResult", false));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (proximitySensor != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];
            proximityValueText.setText("Proximity: " + distance);

            if (distance == 0.0) {  // Near value
                nearDetected = true;
                instructionText.setText("Near detected! Now move your hand away.");
            } else if (distance == proximitySensor.getMaximumRange()) {  // Far value
                farDetected = true;
                //instructionText.setText("Proximity Sensor Worked! Test complete.");
            }

            if (nearDetected && farDetected) {
                passButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this test
    }
}