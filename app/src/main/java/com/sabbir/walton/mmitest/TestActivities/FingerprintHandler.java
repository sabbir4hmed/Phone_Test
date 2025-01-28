package com.sabbir.walton.mmitest.TestActivities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class FingerprintHandler extends FingerprintManager.AuthenticationCallback
{
    private CancellationSignal cancellationSignal;
    private Context context;
    private TextView statusTextView;

    public FingerprintHandler(Context context, TextView statusTextView) {
        this.context = context;
        this.statusTextView = statusTextView;
    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        Toast.makeText(context, "Authentication error\n" + errString, Toast.LENGTH_LONG).show();
        statusTextView.setText("Fingerprint error: " + errString);
    }

    @Override
    public void onAuthenticationFailed() {
        //Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG).show();
        statusTextView.setText("Fingerprint Not Detected");
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        //Toast.makeText(context, "Authentication help\n" + helpString, Toast.LENGTH_LONG).show();
        statusTextView.setText("Authentication help: " + helpString);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        //Toast.makeText(context, "Authentication succeeded", Toast.LENGTH_LONG).show();
        statusTextView.setText("Fingerprint Detected");
    }
}

