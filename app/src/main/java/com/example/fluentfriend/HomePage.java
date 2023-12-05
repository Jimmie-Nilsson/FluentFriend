package com.example.fluentfriend;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class HomePage extends AppCompatActivity {

    private User user;

    private Button btn;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        btn = (Button) findViewById(R.id.btn_prfile);
        welcomeText = (TextView) findViewById(R.id.homepage_welcomeText);
        Intent intent = getIntent();
        welcomeText.setText("Welcome " + intent.getStringExtra("firstName") + " " + intent.getStringExtra("lastName"));

        btn.setOnClickListener(view -> {
            Intent intentTwo = new Intent(HomePage.this, UserProfile.class);
            startActivity(intentTwo);
        });

    }

    public void setUser(User user){
        this.user = user;
    }
}