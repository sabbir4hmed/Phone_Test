package com.sabbir.walton.mmitest.BarCode;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.sabbir.walton.mmitest.R;

public class BarcodeScannerActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private CodeScanner codeScanner;
    private boolean hasPermission = false;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);
        checkCameraPermission();
    }

    private void initializeScanner() {
        try {
            CodeScannerView scannerView = findViewById(R.id.scanner_view);
            codeScanner = new CodeScanner(this, scannerView);

            // Configure scanner
            codeScanner.setAutoFocusEnabled(true);
            codeScanner.setFormats(CodeScanner.ALL_FORMATS);
            codeScanner.setAutoFocusInterval(2000L);

            setupCallbacks();

            scannerView.setOnClickListener(view -> {
                if (hasPermission) {
                    restartPreview();
                }
            });

            // Delayed start of preview
            handler.postDelayed(this::restartPreview, 1000);

        } catch (Exception e) {
            Toast.makeText(this, "Camera initialization failed", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void restartPreview() {
        if (codeScanner != null) {
            try {
                codeScanner.startPreview();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to start camera preview", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupCallbacks() {
        codeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("barcode_result", result.getText());
            setResult(RESULT_OK, resultIntent);
            finish();
        }));

        codeScanner.setErrorCallback(error -> runOnUiThread(() -> {
            Toast.makeText(this, "Scanner Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            handler.postDelayed(this::restartPreview, 1000);
        }));
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
        } else {
            hasPermission = true;
            initializeScanner();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasPermission = true;
                initializeScanner();
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasPermission) {
            handler.postDelayed(this::restartPreview, 500);
        }
    }

    @Override
    protected void onPause() {
        if (codeScanner != null) {
            codeScanner.releaseResources();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}