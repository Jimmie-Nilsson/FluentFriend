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
            if (isInputCorrect()){
               boolean exists = MainActivity.addNewUser(firstName.getText().toString(),lastName.getText().toString(),email.getText().toString(),password.getText().toString());
               if (exists){
                   Intent intent = new Intent(SignUp.this, HomePage.class);
                   startActivity(intent);
               }else {
                   Toast.makeText(SignUp.this, "An account with this Email already exists", Toast.LENGTH_SHORT).show();
               }
            }else {
                Toast.makeText(SignUp.this, "Wrong Input", Toast.LENGTH_LONG).show();
            }
        });
    }
    protected boolean isInputCorrect(){
        if (firstName.getText() == null || lastName.getText() == null || email.getText() == null || password.getText() == null || repeatPassword.getText() == null){
            return false;
        }
        return password.getText().equals(repeatPassword.getText());
    }
}