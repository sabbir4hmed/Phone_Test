package com.sabbir.walton.mmitest.TestActivities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChargingTestActivity extends AppCompatActivity {

    private TextView chargingStatusText;
    private TextView batteryLevelText;
    private TextView voltageText;
    private TextView temperatureText;
    private TextView plugInChargerMessage;
    private Button passButton;
    private Button failButton;

    private final BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateBatteryInfo(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_test);

        chargingStatusText = findViewById(R.id.charging_status);
        batteryLevelText = findViewById(R.id.battery_level);
        voltageText = findViewById(R.id.voltage);
        temperatureText = findViewById(R.id.temperature);
        plugInChargerMessage = findViewById(R.id.instruction_text);
        passButton = findViewById(R.id.pass_button);
        failButton = findViewById(R.id.fail_button);

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
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryInfoReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(batteryInfoReceiver);
    }

    private void updateBatteryInfo(Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level * 100 / (float)scale;

        int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);

        chargingStatusText.setText("Charging Status: " + (isCharging ? "Charging" : "Not Charging"));
        batteryLevelText.setText(String.format("Battery Level: %.1f%%", batteryPct));
        voltageText.setText(String.format("Voltage: %.2f V", voltage / 1000f));
        temperatureText.setText(String.format("Temperature: %.1f °C", temperature / 10f));

        // Show or hide the "Please plug in charger" message based on charging status
        if (isCharging) {
            plugInChargerMessage.setVisibility(TextView.GONE);
            passButton.setEnabled(true);  // Enable the pass button when charging
        } else {
            plugInChargerMessage.setVisibility(TextView.VISIBLE);
            passButton.setEnabled(false);  // Disable the pass button when not charging
        }
    }
}