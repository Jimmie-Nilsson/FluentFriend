package com.example.fluentfriend;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private Button btnLogIn;
    private Button btnCreateAccount;

    private TextInputEditText text; // ???


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCreateAccount = (Button) findViewById(R.id.main_btnCreateAccount);
        btnLogIn = (Button) findViewById(R.id.main_btnLogIn);
        //text = (E) findViewById(R.id.main_emailField) ;



        btnCreateAccount.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignUp.class);
            startActivity(intent);
        });

        btnLogIn.setOnClickListener(view -> {
            // Skriv kod för att verifera inloggingen.




            Intent intent = new Intent(MainActivity.this, HomePage.class);
            startActivity(intent);
        });

    }
}