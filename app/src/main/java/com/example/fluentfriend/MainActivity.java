package com.example.fluentfriend;

import android.content.Intent;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    Button signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signInBtn = (Button) findViewById(R.id.button2);
        signInBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, HomePage.class);
            startActivity(intent);

        });
    }
}