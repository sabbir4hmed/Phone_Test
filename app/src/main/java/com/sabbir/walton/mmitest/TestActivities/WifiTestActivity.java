package com.sabbir.walton.mmitest.TestActivities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sabbir.walton.mmitest.R;

import java.util.List;

public class WifiTestActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private Button scanWifiButton, passButton, failButton;
    private TextView wifiInfoText;
    private WifiManager wifiManager;
    private BroadcastReceiver wifiScanReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_test);

        initializeViews();
        setupWifiManager();
        setupButtonListeners();
        checkWifiStatus();
    }

    private void initializeViews() {
        scanWifiButton = findViewById(R.id.scan_wifi_button);
        passButton = findViewById(R.id.pass_button);
        failButton = findViewById(R.id.fail_button);
        wifiInfoText = findViewById(R.id.wifi_info_text);

        passButton.setVisibility(View.GONE);
        failButton.setVisibility(View.GONE);
    }

    private void setupWifiManager() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiScanReceiver = new WifiScanReceiver();
    }

    private void setupButtonListeners() {
        scanWifiButton.setOnClickListener(v -> requestLocationPermission());
        passButton.setOnClickListener(v -> finishWithResult(true));
        failButton.setOnClickListener(v -> finishWithResult(false));
    }

    private void checkWifiStatus() {
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "Please turn on WiFi", Toast.LENGTH_LONG).show();
        }
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startWifiScan();
        }
    }

    private void startWifiScan() {
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "Please turn on WiFi", Toast.LENGTH_LONG).show();
            return;
        }

        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        boolean success = wifiManager.startScan();
        if (success) {
            Toast.makeText(this, "Scanning WiFi networks...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to start WiFi scan", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFrequencyBand(int frequency) {
        if (frequency >= 2412 && frequency <= 2484) {
            return "2.4 GHz";
        } else if (frequency >= 5170 && frequency <= 5825) {
            return "5 GHz";
        } else {
            return "Unknown";
        }
    }

    private void finishWithResult(boolean result) {
        setResult(RESULT_OK, new Intent().putExtra("testResult", result));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startWifiScan();
            } else {
                Toast.makeText(this, "Location permission denied. Cannot scan WiFi networks.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(wifiScanReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver not registered
        }
    }

    private class WifiScanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success) {
                List<ScanResult> scanResults = wifiManager.getScanResults();
                if (!scanResults.isEmpty()) {
                    ScanResult scanResult = scanResults.get(0); // Get the first WiFi network
                    String wifiInfo = "SSID: " + scanResult.SSID + "\n" +
                            "Frequency: " + scanResult.frequency + " MHz\n" +
                            "Band: " + getFrequencyBand(scanResult.frequency) + "\n";
                    wifiInfoText.setText(wifiInfo);
                    passButton.setVisibility(View.VISIBLE);
                    failButton.setVisibility(View.VISIBLE);
                } else {
                    wifiInfoText.setText("No WiFi networks found");
                    passButton.setVisibility(View.GONE);
                    failButton.setVisibility(View.VISIBLE);
                }
            } else {
                wifiInfoText.setText("WiFi scan failed");
                passButton.setVisibility(View.GONE);
                failButton.setVisibility(View.VISIBLE);
            }
        }
    }
}