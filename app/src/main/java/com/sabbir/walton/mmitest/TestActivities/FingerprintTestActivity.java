package com.sabbir.walton.mmitest.TestActivities;

import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.sabbir.walton.mmitest.R;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class FingerprintTestActivity extends AppCompatActivity {

    private static final String KEY_NAME = "yourKey";
    private KeyStore keyStore;
    private Cipher cipher;
    private TextView fingerprintStatusTextView;
    private Button pass;
    private Button fail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_test);

        fingerprintStatusTextView = findViewById(R.id.fingerprint_info_text);
        pass = findViewById(R.id.pass_button);
        fail = findViewById(R.id.fail_button);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkFingerprintCompatibility()) {
                generateKey();
                if (cipherInit()) {
                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerprintHandler fingerprintHandler = new FingerprintHandler(this, fingerprintStatusTextView);
                    fingerprintHandler.startAuth((FingerprintManager) getSystemService(FINGERPRINT_SERVICE), cryptoObject);
                }
            }
        } else {
            Toast.makeText(this, "Your device does not support fingerprint authentication", Toast.LENGTH_LONG).show();
        }

        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK, new Intent().putExtra("testResult", true));
                finish();
            }
        });

        fail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK, new Intent().putExtra("testResult", false));
                finish();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkFingerprintCompatibility() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if (!fingerprintManager.isHardwareDetected()) {
            //Toast.makeText(this, "Fingerprint scanner not detected in device", Toast.LENGTH_LONG).show();
            fingerprintStatusTextView.setText("Fingerprint scanner not detected in device");
            return false;
        } else if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "Permission not granted to use fingerprint scanner", Toast.LENGTH_LONG).show();
            fingerprintStatusTextView.setText("Permission not granted to use fingerprint scanner");
            return false;
        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
            //Toast.makeText(this, "No fingerprint configured. Please register at least one fingerprint in your device's Settings", Toast.LENGTH_LONG).show();
            fingerprintStatusTextView.setText("No fingerprint configured. Please register at least one fingerprint in your device's Settings");
            return false;
        } else if (!keyguardManager.isKeyguardSecure()) {
            //Toast.makeText(this, "Please enable lockscreen security in your device's Settings", Toast.LENGTH_LONG).show();
            fingerprintStatusTextView.setText("Please enable lockscreen security in your device's Settings");
            return false;
        } else {
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}