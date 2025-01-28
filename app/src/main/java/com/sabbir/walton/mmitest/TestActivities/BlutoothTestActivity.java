package com.sabbir.walton.mmitest.TestActivities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.sabbir.walton.mmitest.R;

public class BlutoothTestActivity extends AppCompatActivity {

    private static final int BLUETOOTH_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private Button scanBluetoothButton, passButton, failButton;
    private TextView bluetoothInfoText;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothScanReceiver bluetoothReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blutooth_test);

        scanBluetoothButton = findViewById(R.id.scan_bluetooth_button);
        passButton = findViewById(R.id.pass_button);
        failButton = findViewById(R.id.fail_button);
        bluetoothInfoText = findViewById(R.id.bluetooth_info_text);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothReceiver = new BluetoothScanReceiver();

        // Set initial visibility for pass and fail buttons
        passButton.setVisibility(View.GONE);
        failButton.setVisibility(View.GONE);

        scanBluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestBluetoothPermission();
            }
        });

        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent().putExtra("testResult", true));
                finish();
            }
        });

        failButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent().putExtra("testResult", false));
                finish();
            }
        });
    }

    private void requestBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For Android 12 and above
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.BLUETOOTH_SCAN,
                        android.Manifest.permission.BLUETOOTH_CONNECT
                }, BLUETOOTH_PERMISSION_REQUEST_CODE);
            } else {
                scanBluetoothDevices();
            }
        } else {
            // For Android 11 and below
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.BLUETOOTH,
                        android.Manifest.permission.BLUETOOTH_ADMIN
                }, BLUETOOTH_PERMISSION_REQUEST_CODE);
            } else {
                scanBluetoothDevices();
            }
        }
    }

    private void scanBluetoothDevices() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if Bluetooth is enabled
        if (!bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_PERMISSION_REQUEST_CODE);
            }
        } else {
            // Start scanning for Bluetooth devices
            startBluetoothScanning();
        }
    }

    private void startBluetoothScanning() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            bluetoothAdapter.startDiscovery();
            registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            Log.d("BluetoothTest", "Discovery started...");
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_SCAN}, BLUETOOTH_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                // Bluetooth is now enabled, start scanning
                startBluetoothScanning();
            } else {
                Toast.makeText(this, "Bluetooth is required for this test", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                scanBluetoothDevices();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the Bluetooth receiver to prevent memory leaks
        try {
            unregisterReceiver(bluetoothReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver not registered
            Log.d("BluetoothTest", "Receiver was not registered");
        }
    }

    private class BluetoothScanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Ensure BLUETOOTH_CONNECT permission is granted
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    // Ensure that the device is not null
                    if (device != null) {
                        String deviceInfo = "Name: " + (device.getName() != null ? device.getName() : "Unknown") + "\n" +
                                "Address: " + device.getAddress() + "\n";
                        bluetoothInfoText.append(deviceInfo + "\n"); // Append each device found
                        Log.d("BluetoothTest", "Device found: " + deviceInfo);

                        passButton.setVisibility(View.VISIBLE);
                        failButton.setVisibility(View.VISIBLE);

                        // Optional: Stop discovery after finding the first device
                        bluetoothAdapter.cancelDiscovery();
                    } else {
                        Log.e("BluetoothTest", "Device is null, unable to display information.");
                    }
                } else {
                    Log.e("BluetoothTest", "BLUETOOTH_CONNECT permission not granted.");
                }
            }
        }
    }
}