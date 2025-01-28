package com.sabbir.walton.mmitest.TestActivities;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media.session.MediaButtonReceiver;

public class EarphoneTestActivity extends AppCompatActivity {

    private TextView earphoneStatusTextView;
    private Button playButton;
    private Button passButton;
    private Button failButton;
    private BroadcastReceiver receiver;
    private MediaSessionCompat mediaSession;
    private MediaPlayer mediaPlayer;
    private boolean isEarphoneConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earphone_test);

        earphoneStatusTextView = findViewById(R.id.earphoneStatusTextView);
        playButton = findViewById(R.id.playButton);
        passButton = findViewById(R.id.passButton);
        failButton = findViewById(R.id.failButton);

        // Initially disable the play button
        playButton.setEnabled(false);

        // Register a BroadcastReceiver to detect earphone insertion/removal
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        receiver = new EarphoneStateReceiver();
        registerReceiver(receiver, filter);

        // Set up MediaSession to handle media button events
        setupMediaSession();

        // Set up MediaPlayer to play sound through the earphones
        setupMediaPlayer();

        // Set up button click listeners
        playButton.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.prepareAsync();
            }
            mediaPlayer.start();
            earphoneStatusTextView.setText("Playing sound through earphones...");
        });

        passButton.setOnClickListener(v -> {
            setResult(RESULT_OK, new Intent().putExtra("testResult", true));
            finish();
        });

        failButton.setOnClickListener(v -> {
            setResult(RESULT_OK, new Intent().putExtra("testResult", false));
            finish();
        });

        // Check initial earphone state
        checkInitialEarphoneState();
    }

    private void setupMediaSession() {
        ComponentName mediaButtonReceiver = new ComponentName(this, MediaButtonReceiver.class);
        PendingIntent mediaButtonIntent = PendingIntent.getBroadcast(this, 0, new Intent(Intent.ACTION_MEDIA_BUTTON), PendingIntent.FLAG_IMMUTABLE);
        mediaSession = new MediaSessionCompat(this, "EarphoneTestActivity", mediaButtonReceiver, mediaButtonIntent);
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                KeyEvent keyEvent = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    int keyCode = keyEvent.getKeyCode();
                    if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
                        earphoneStatusTextView.setText("Earphone button pressed!");
                        return true;
                    }
                }
                return super.onMediaButtonEvent(mediaButtonEvent);
            }
        });
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        mediaSession.setActive(true);
    }

    private void setupMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.sound); // Ensure you have a sound.mp3 file in res/raw directory
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());
    }

    private void checkInitialEarphoneState() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        Intent stickyIntent = registerReceiver(null, filter);
        if (stickyIntent != null) {
            int state = stickyIntent.getIntExtra("state", -1);
            updateEarphoneState(state);
        }
    }

    private void updateEarphoneState(int state) {
        isEarphoneConnected = (state == 1);
        playButton.setEnabled(isEarphoneConnected);
        if (isEarphoneConnected) {
            earphoneStatusTextView.setText("Earphone detected. Click 'Play Sound' to test.");
        } else {
            earphoneStatusTextView.setText("Earphone not detected. Please connect earphones.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        if (mediaSession != null) {
            mediaSession.release();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    public class EarphoneStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("state")) {
                int state = intent.getIntExtra("state", 0);
                updateEarphoneState(state);
            }
        }
    }
}