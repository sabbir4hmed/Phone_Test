package com.sabbir.walton.mmitest.TestActivities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class SimCardTestActivity extends AppCompatActivity {
    private static final int REQUEST_READ_PHONE_STATE = 1;

    private TextView simInfoTextView;
    private Button passButton;
    private Button failButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sim_card_test);

        simInfoTextView = findViewById(R.id.simInfoTextView);
        passButton = findViewById(R.id.passButton);
        failButton = findViewById(R.id.failButton);

        // Disable the pass button initially
        passButton.setEnabled(false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            displaySimInfo();
        }

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

    private void displaySimInfo() {
        SubscriptionManager subscriptionManager = (SubscriptionManager) getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        if (subscriptionInfoList != null && !subscriptionInfoList.isEmpty()) {
            StringBuilder simInfo = new StringBuilder();
            for (SubscriptionInfo info : subscriptionInfoList) {
                simInfo.append("SIM Slot: ").append(info.getSimSlotIndex()).append("\n");
                simInfo.append("Display Name: ").append(info.getDisplayName()).append("\n");
                simInfo.append("Carrier Name: ").append(info.getCarrierName()).append("\n");
                simInfo.append("\n");
            }
            simInfoTextView.setText(simInfo.toString());

            // Enable the pass button once SIM info is displayed
            passButton.setEnabled(true);
        } else {
            simInfoTextView.setText("No SIM cards detected");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_READ_PHONE_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displaySimInfo();
            } else {
                simInfoTextView.setText("Permission denied. Cannot access SIM information.");
            }
        }
    }
}