package com.sabbir.walton.mmitest.Report;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sabbir.walton.mmitest.BarCode.BarcodeScannerActivity;
import com.sabbir.walton.mmitest.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    private static final int BARCODE_SCANNER_REQUEST = 123;
    private RecyclerView recyclerView;
    private TestReportAdapter adapter;
    private Button submitButton;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        recyclerView = findViewById(R.id.reportRecyclerView);
        submitButton = findViewById(R.id.submitButton);

        setupRecyclerView();
        setupSubmitButton();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        Map<String, String> testResults = TestReportManager.getInstance().getTestResults();
        adapter = new TestReportAdapter(testResults);
        recyclerView.setAdapter(adapter);
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> showSubmitDialog());
    }

    private void showSubmitDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_submit_report);
        dialog.setCancelable(true);

        EditText imeiField = dialog.findViewById(R.id.imeiField);
        Button scanBarcodeButton = dialog.findViewById(R.id.scanBarcodeButton);
        EditText srNumberField = dialog.findViewById(R.id.srNumberField);
        EditText remarksField = dialog.findViewById(R.id.remarksField);
        Button submitNowButton = dialog.findViewById(R.id.submitNowButton);

        scanBarcodeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, BarcodeScannerActivity.class);
            startActivityForResult(intent, BARCODE_SCANNER_REQUEST);
        });

        submitNowButton.setOnClickListener(v -> {
            String imei = imeiField.getText().toString().trim();
            String srNumber = srNumberField.getText().toString().trim();
            String remarks = remarksField.getText().toString().trim();

            if (validateFields(imei, srNumber)) {
                handleSubmission(imei, srNumber, remarks);
                dialog.dismiss();
            }
        });

        dialog.show();

        // Set dialog width to match parent
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    private boolean validateFields(String imei, String srNumber) {
        if (imei.isEmpty()) {
            showToast("Please enter IMEI");
            return false;
        }
        if (srNumber.isEmpty()) {
            showToast("Please enter SR Number");
            return false;
        }
        return true;
    }

    private void handleSubmission(String imei, String srNumber, String remarks) {
        // Get test results from TestReportManager
        Map<String, String> testResults = TestReportManager.getInstance().getTestResults();

        // Create submission data object
        TestSubmissionData submissionData = new TestSubmissionData(
                imei,
                srNumber,
                remarks,
                testResults
        );

        // TODO: Send data to server or save locally

        showToast("Report submitted successfully");
        finish(); // Close the activity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BARCODE_SCANNER_REQUEST && resultCode == RESULT_OK && data != null) {
            String scannedImei = data.getStringExtra("barcode_result");
            if (dialog != null && dialog.isShowing()) {
                EditText imeiField = dialog.findViewById(R.id.imeiField);
                if (imeiField != null) {
                    imeiField.setText(scannedImei);
                }
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}