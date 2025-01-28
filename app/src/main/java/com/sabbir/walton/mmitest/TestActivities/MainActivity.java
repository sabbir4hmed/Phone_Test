package com.sabbir.walton.mmitest.TestActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CardView aboutCard, umCard, scCard, feedbackCard, diagnosticCard, specCard;
    private ImageSlider imageSlider;
    private DatabaseReference databaseReference;
    private List<String> imageLinks = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        // Set status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.skytint));
        // For light colored status bar, use dark icons
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        // Initialize ImageSlider
        imageSlider = findViewById(R.id.image_slider);
        databaseReference = FirebaseDatabase.getInstance().getReference("images");

        // Initialize marquee text
        initializeMarqueeText();

        // Initialize cards
        initializeCards();

        // Set click listeners
        setCardClickListeners();

        // Setup welcome message
        setupWelcomeMessage();

        // Fetch image URLs for slider
        fetchImageUrls();
    }

    private void initializeCards() {
        aboutCard = findViewById(R.id.about_card);
        umCard = findViewById(R.id.um_card);
        scCard = findViewById(R.id.sc_card);
        feedbackCard = findViewById(R.id.feedback_card);
        diagnosticCard = findViewById(R.id.diagnostic_card);

    }

    private void setCardClickListeners() {
        umCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserManual.class);
            startActivity(intent);
        });

        scCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ServiceCenter.class);
            startActivity(intent);
        });

        aboutCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, About.class);
            startActivity(intent);
        });

        feedbackCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
            startActivity(intent);
        });

        diagnosticCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PhoneDiagnostic.class);
            startActivity(intent);
        });


    }

    private void initializeMarqueeText() {
        TextView scrollingText = findViewById(R.id.scrolling_text);
        scrollingText.setSelected(true);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("scrolling_text").document("text");

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                String scrollingTextString = document.getString("text");
                scrollingText.setText(scrollingTextString);
                scrollingText.setSelected(true);
            }
        });
    }

    private void setupWelcomeMessage() {
        TextView welcomeMessage = findViewById(R.id.welcome_message);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hour < 12) {
            greeting = "Good Morning";
        } else if (hour < 18) {
            greeting = "Good Afternoon";
        } else {
            greeting = "Good Evening";
        }

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isFirstVisit = sharedPreferences.getBoolean("isFirstVisit", true);

        String welcomeText = isFirstVisit ? "Welcome" : "Welcome back";
        sharedPreferences.edit().putBoolean("isFirstVisit", false).apply();

        welcomeMessage.setText(welcomeText + ", " + greeting + "!");
        welcomeMessage.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    }

    private void fetchImageUrls() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<SlideModel> slideModelList = new ArrayList<>();
                imageLinks.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ImageDataModel imageDataModel = snapshot.getValue(ImageDataModel.class);
                    if (imageDataModel != null) {
                        slideModelList.add(new SlideModel(imageDataModel.url, ScaleTypes.FIT));
                        imageLinks.add(imageDataModel.link);
                    }
                }
                imageSlider.setImageList(slideModelList);
                setItemClickListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private void setItemClickListener() {
        imageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int i) {
                String url = imageLinks.get(i);
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browser);
            }

            @Override
            public void doubleClick(int i) {
                // Handle double click if needed
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}