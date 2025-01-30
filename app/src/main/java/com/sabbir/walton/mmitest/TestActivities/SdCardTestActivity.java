package com.sabbir.walton.mmitest.TestActivities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.sabbir.walton.mmitest.R;

import java.io.File;

public class SdCardTestActivity extends AppCompatActivity {
    private TextView sdCardInfoTextView;
    private Button passButton;
    private Button failButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sd_card_test);

        sdCardInfoTextView = findViewById(R.id.sdCardInfoTextView);
        passButton = findViewById(R.id.passButton);
        failButton = findViewById(R.id.failButton);

        // Disable the pass button initially
        passButton.setEnabled(false);

        checkSdCard();

        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK, new Intent().putExtra("testResult", true));
                finish();
            }
        });

        failButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK, new Intent().putExtra("testResult", false));
                finish();
            }
        });
    }

    private void checkSdCard() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            File sdCard = Environment.getExternalStorageDirectory();
            String sdCardDetails = getStorageDetails(sdCard);

            File[] externalStorageFiles = getExternalFilesDirs(null);
            String externalSdCardDetails = "No external SD Card detected";
            if (externalStorageFiles.length > 1 && externalStorageFiles[1] != null) {
                externalSdCardDetails = getStorageDetails(externalStorageFiles[1]);
            }

            sdCardInfoTextView.setText("Internal Storage:\n" + sdCardDetails + "\nExternal SD Card:\n" + externalSdCardDetails);

            // Enable the pass button once an SD card is detected
            passButton.setEnabled(true);
        } else {
            sdCardInfoTextView.setText("No SD Card detected");

            // Keep the pass button disabled if no SD card is detected
            passButton.setEnabled(false);
        }
    }

    private String getStorageDetails(File path) {
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        long totalSize = totalBlocks * blockSize;
        long availableSize = availableBlocks * blockSize;

        return "Total Size: " + formatSize(totalSize) + "\nAvailable Size: " + formatSize(availableSize);
    }

    private String formatSize(long size) {
        String suffix = null;
        float fSize = size;

        if (fSize >= 1024) {
            suffix = "KB";
            fSize /= 1024;
            if (fSize >= 1024) {
                suffix = "MB";
                fSize /= 1024;
                if (fSize >= 1024) {
                    suffix = "GB";
                    fSize /= 1024;
                }
            }
        }
        StringBuilder resultBuffer = new StringBuilder(Float.toString(fSize));
        int commaOffset = resultBuffer.indexOf(".");
        if (commaOffset >= 0) {
            int charsToKeep = commaOffset + 3;
            if (charsToKeep < resultBuffer.length()) {
                resultBuffer.setLength(charsToKeep);
            }
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }
}