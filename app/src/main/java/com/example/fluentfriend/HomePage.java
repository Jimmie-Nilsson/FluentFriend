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
        welcomeText.setText("Welcome " + user.getFirstName() + " " + user.getLastName());

        btn.setOnClickListener(view -> {
            Intent intent = new Intent(HomePage.this, UserProfile.class);
            startActivity(intent);
        });

    }

    public  void setUser(User user){
        this.user = user;
    }
}