package com.example.fluentfriend;


import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SignUp extends AppCompatActivity {

    EditText firstName;
    EditText lastName;
    EditText email;
    EditText password;
    EditText repeatPassword;
    Button signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firstName = findViewById(R.id.signup_FirstName);
        lastName = findViewById(R.id.signup_LastName);
        email = findViewById(R.id.signup_Email);
        password = findViewById(R.id.signup_Password);
        repeatPassword = findViewById(R.id.signup_RepeatPassword);
        signUpBtn = findViewById(R.id.signup_btnSignUp);
        signUpBtn.setOnClickListener(view -> {
            if (!isInputCorrect()) {
                sendErrorMessage("All fields must be filled");
                return;
            }
            boolean exists = MainActivity.addNewUser(firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(), password.getText().toString());
            if (!exists) {
                sendErrorMessage("An account with this Email already exists");
                return;
            }
            Intent intent = new Intent(SignUp.this, HomePage.class);
            startActivity(intent);
        });
    }

    private boolean isInputCorrect() { // make a method for the toString().trim().isEmpty() checks
        if (firstName.getText().toString().trim().isEmpty() || lastName.getText().toString().trim().isEmpty() || email.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty() || repeatPassword.getText().toString().trim().isEmpty()) {
            return false;
        }
        return password.getText().toString().equals(repeatPassword.getText().toString());
    }

    private void sendErrorMessage(String message) {
        Toast.makeText(SignUp.this, message, Toast.LENGTH_LONG).show();
    }
}