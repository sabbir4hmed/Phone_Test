package com.sabbir.walton.mmitest.TestActivities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sabbir.walton.mmitest.R;

import java.io.IOException;

public class MicTestActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private Button startButton, stopButton, playButton, passButton, failButton;
    private TextView statusTextView;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String audioFilePath;
    private boolean isRecording = false;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mic_test);

        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);
        playButton = findViewById(R.id.play_button);
        passButton = findViewById(R.id.pass_button);
        failButton = findViewById(R.id.fail_button);
        //statusTextView = findViewById(R.id.status_text_view);

        startButton.setOnClickListener(v -> requestPermissionAndStartRecording());
        stopButton.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else if (isPlaying) {
                stopPlaying();
            }
        });
        playButton.setOnClickListener(v -> {
            if (!isPlaying) {
                playRecording();
            } else {
                stopPlaying();
            }
        });
        passButton.setOnClickListener(v -> updateMainActivity(true));
        failButton.setOnClickListener(v -> updateMainActivity(false));
    }

    private void requestPermissionAndStartRecording() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            startRecording();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(this, "Recording permission is required to use this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startRecording() {
        if (!isRecording) {
            mediaRecorder = new MediaRecorder();
            try {
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                audioFilePath = getFilesDir().getAbsolutePath() + "/sound_recording.3gp";
                mediaRecorder.setOutputFile(audioFilePath);

                mediaRecorder.prepare();
                mediaRecorder.start();
                isRecording = true;
                isPlaying = false;
                Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("MicTestActivity", "Error preparing media recorder: " + e.getMessage());
                Toast.makeText(this, "Error preparing media recorder", Toast.LENGTH_SHORT).show();
            } catch (RuntimeException e) {
                Log.e("MicTestActivity", "Error setting audio source: " + e.getMessage());
                Toast.makeText(this, "Error setting audio source", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void stopRecording() {
        if (isRecording) {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;
                Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void playRecording() {
        if (!isPlaying) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioFilePath);
                mediaPlayer.prepare();
                mediaPlayer.start();
                isPlaying = true;
                isRecording = false;
                Toast.makeText(this, "Playing recording", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("MicTestActivity", "Error playing recording: " + e.getMessage());
                Toast.makeText(this, "Error playing recording", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void stopPlaying() {
        if (isPlaying) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                isPlaying = false;
                Toast.makeText(this, "Playback stopped", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateMainActivity(boolean testResult) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("testResult", testResult);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}