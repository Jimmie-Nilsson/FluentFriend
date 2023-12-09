package com.example.fluentfriend;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class HomePage extends AppCompatActivity {

    private User user;

    private Button btnProfile;
    private Button btnMessage;
    private Button btnMatch;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        btnProfile = (Button) findViewById(R.id.homepage_btn_profile);
        welcomeText = (TextView) findViewById(R.id.homepage_welcomeText);
        btnMatch = (Button) findViewById(R.id.matchPageBtn);
        btnMessage = (Button) findViewById(R.id.homepage_messagesbtn);

        Intent intent = getIntent();
        welcomeText.setText("Welcome " + intent.getStringExtra("firstName") + " " + intent.getStringExtra("lastName"));

        btnProfile.setOnClickListener(view -> {
            Intent intentTwo = new Intent(HomePage.this, UserProfilePage.class);
            startActivity(intentTwo);
        });

        btnMatch.setOnClickListener(view -> {
            Intent intentTwo = new Intent(HomePage.this, MatchPage.class);
            startActivity(intentTwo);
        });

        btnMessage.setOnClickListener(view -> {
            Toast.makeText(HomePage.this, "Not implemented", Toast.LENGTH_SHORT).show();
        });

    }

    public void setUser(User user){
        this.user = user;
    }
}