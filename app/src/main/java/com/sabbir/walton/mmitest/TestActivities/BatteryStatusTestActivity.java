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

import com.sabbir.walton.mmitest.R;

public class BatteryStatusTestActivity extends AppCompatActivity {

    private TextView batteryLevelText;
    private TextView batteryStatusText;
    private TextView batteryHealthText;
    private TextView powerSourceText;
    private TextView temperatureText;
    private TextView voltageText;
    private TextView technologyText;
    private TextView capacityText;
    private Button passButton;
    private Button failButton;

    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateBatteryInfo(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_status_test);

        batteryLevelText = findViewById(R.id.battery_level);
        batteryStatusText = findViewById(R.id.battery_status);
        batteryHealthText = findViewById(R.id.battery_health);
        powerSourceText = findViewById(R.id.power_source);
        temperatureText = findViewById(R.id.temperature);
        voltageText = findViewById(R.id.voltage);
        technologyText = findViewById(R.id.technology);
        capacityText = findViewById(R.id.capacity);
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

        getBatteryCapacity();
    }

    private void getBatteryCapacity() {
        BatteryManager mBatteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);

        // Get the battery's design capacity
        long designCapacity = getBatteryDesignCapacity(mBatteryManager);

        if (designCapacity > 0) {
            capacityText.setText(String.format("Battery Capacity: %d mAh", designCapacity));
        } else {
            // If we couldn't get the design capacity, try to estimate current full capacity
            long chargeCounter = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            int capacity = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

            if (chargeCounter != Long.MIN_VALUE && capacity > 0) {
                long estimatedCapacity = (chargeCounter / capacity) * 100 / 1000;
                capacityText.setText(String.format("Estimated Capacity: %d mAh", estimatedCapacity));
            } else {
                capacityText.setText("Capacity: Unable to determine");
            }
        }
    }

    private long getBatteryDesignCapacity(BatteryManager mBatteryManager) {
        Object mPowerProfile;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(this);

            batteryCapacity = (double) Class.forName(POWER_PROFILE_CLASS)
                    .getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return (long) batteryCapacity;
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
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level * 100 / (float)scale;

        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        String batteryStatus = "Unknown";
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING: batteryStatus = "Charging"; break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING: batteryStatus = "Discharging"; break;
            case BatteryManager.BATTERY_STATUS_FULL: batteryStatus = "Full"; break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING: batteryStatus = "Not Charging"; break;
        }

        int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        String batteryHealth = "Unknown";
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_GOOD: batteryHealth = "Good"; break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT: batteryHealth = "Overheat"; break;
            case BatteryManager.BATTERY_HEALTH_DEAD: batteryHealth = "Dead"; break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE: batteryHealth = "Over Voltage"; break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE: batteryHealth = "Unspecified Failure"; break;
        }

        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        String powerSource = "Unknown";
        switch (plugged) {
            case 0: powerSource = "Battery"; break;
            case BatteryManager.BATTERY_PLUGGED_AC: powerSource = "AC"; break;
            case BatteryManager.BATTERY_PLUGGED_USB: powerSource = "USB"; break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS: powerSource = "Wireless"; break;
        }

        int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);

        batteryLevelText.setText(String.format("Battery Level: %.1f%%", batteryPct));
        batteryStatusText.setText("Status: " + batteryStatus);
        batteryHealthText.setText("Health: " + batteryHealth);
        powerSourceText.setText("Power Source: " + powerSource);
        temperatureText.setText(String.format("Temperature: %.1f °C", temperature / 10f));
        voltageText.setText(String.format("Voltage: %.2f V", voltage / 1000f));
        technologyText.setText("Technology: " + technology);

        // Enable pass button if we successfully got battery info
        passButton.setEnabled(true);
    }
}