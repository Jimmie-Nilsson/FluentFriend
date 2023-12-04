package com.example.fluentfriend;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private Button btnLogIn;
    private Button btnCreateAccount;
    private TextView email;
    private  TextView password;
    private  User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCreateAccount = (Button) findViewById(R.id.main_btnCreateAccount);
        btnLogIn = (Button) findViewById(R.id.main_btnLogIn);
        email = (TextView) findViewById(R.id.main_emailField);
        password = (TextView) findViewById(R.id.main_passwordField) ;

        btnCreateAccount.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignUp.class);
            startActivity(intent);
        });

        btnLogIn.setOnClickListener(view -> {
            // Skriv kod för att verifera inloggingen.
            String inputEmail = email.getText().toString();
            String inputPassword = password.getText().toString();

            if (inputEmail.equals("admin") && inputPassword.equals("123")) {
                Intent intent = new Intent(MainActivity.this, HomePage.class);
                startActivity(intent);
            }
            else {
                // Do somtehing
                Toast.makeText(MainActivity.this, "LOGIN FAILED", Toast.LENGTH_LONG).show();
            }
        });
    }
}