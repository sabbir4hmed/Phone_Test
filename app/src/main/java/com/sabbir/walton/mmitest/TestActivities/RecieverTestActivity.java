package com.sabbir.walton.mmitest.TestActivities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.sabbir.walton.mmitest.R;

import java.io.IOException;

public class RecieverTestActivity extends AppCompatActivity {

    private Button playSoundButton;
    private Button passButton;
    private Button failButton;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciever_test);

        playSoundButton = findViewById(R.id.playSoundButton);
        passButton = findViewById(R.id.passButton);
        failButton = findViewById(R.id.failButton);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // --- MediaPlayer setup using modern AudioAttributes ---
        mediaPlayer = new MediaPlayer();
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();
        mediaPlayer.setAudioAttributes(audioAttributes);

        try {
            AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.sound);
            if (afd != null) {
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                mediaPlayer.prepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnErrorListener((mp, what, extra) -> false);

        playSoundButton.setOnClickListener(view -> {
            if (mediaPlayer != null) {
                requestAudioFocus();

                // Force audio to earpiece
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                audioManager.setSpeakerphoneOn(false);

                // Max volume for earpiece
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, maxVolume, 0);

                mediaPlayer.start();
            }
        });

        passButton.setOnClickListener(view -> cleanupAndFinish(true));
        failButton.setOnClickListener(view -> cleanupAndFinish(false));
    }

    private void cleanupAndFinish(boolean result) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        abandonAudioFocus();

        // Reset audio routing
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(false);

        setResult(RESULT_OK, new Intent().putExtra("testResult", result));
        finish();
    }

    private void requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    .setAudioAttributes(audioAttributes)
                    .setWillPauseWhenDucked(true)
                    .setOnAudioFocusChangeListener(focusChange -> {
                        // Optional: handle focus changes
                    })
                    .build();

            audioManager.requestAudioFocus(audioFocusRequest);
        } else {
            audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }
    }

    private void abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioFocusRequest != null) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest);
        } else {
            audioManager.abandonAudioFocus(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        abandonAudioFocus();

        // Reset audio routing in case activity destroyed unexpectedly
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(false);
    }
}
