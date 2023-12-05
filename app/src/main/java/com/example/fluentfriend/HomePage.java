package com.example.fluentfriend;

import android.content.Intent;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class HomePage extends AppCompatActivity {

    private User user;

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        btn = (Button) findViewById(R.id.btn_prfile);

        btn.setOnClickListener(view -> {
            Intent intent = new Intent(HomePage.this, UserProfil.class);
            startActivity(intent);
        });

    }
}