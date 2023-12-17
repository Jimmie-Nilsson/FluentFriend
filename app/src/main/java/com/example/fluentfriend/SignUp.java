package com.example.fluentfriend;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.database.*;

import java.util.Set;

public class SignUp extends AppCompatActivity {

    EditText firstName;
    EditText lastName;
    EditText email;
    EditText password;
    EditText repeatPassword;
    Button signUpBtn;

    FirebaseDatabase db = FirebaseDatabase.getInstance("https://fluent-friend-dad39-default-rtdb.firebaseio.com/");
    DatabaseReference myRef = db.getReference();


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

        // Read from the database
        signUpBtn.setOnClickListener(view -> {
            if (!isInputCorrect()) {
                sendErrorMessage("All fields must be filled");
                return;
            }
            if (!doesPasswordsMatch()) {
                sendErrorMessage("Passwords do not match");
                return;
            }
            // If we can create a new user then start MainActivity JN
            if (writeNewUser()) {
                Intent intent = new Intent(SignUp.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean doesPasswordsMatch() {
        return password.getText().toString().equals(repeatPassword.getText().toString());
    }

    private boolean isInputCorrect() { // make a method for the toString().trim().isEmpty() checks
        if (firstName.getText().toString().trim().isEmpty() || lastName.getText().toString().trim().isEmpty() || email.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty() || repeatPassword.getText().toString().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    private String normalizeString(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    private void sendErrorMessage(String message) {
        Toast.makeText(SignUp.this, message, Toast.LENGTH_LONG).show();
    }

    private boolean writeNewUser() {
        User user = new User(normalizeString(firstName.getText().toString()), normalizeString(lastName.getText().toString()), email.getText().toString().toLowerCase(), password.getText().toString());
        Set<String> users = MainActivity.getRegisteredUsers();
        if (!users.contains(user.getEmail())) {
            myRef.child("users").child(user.getEmail()).setValue(user);
            return true;
        } else {
            sendErrorMessage("A user with this email already exists, try logging in instead!");
            return false;
        }
    }
}