package com.example.fluentfriend;


import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;

import java.util.HashMap;

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
        FirebaseApp.initializeApp(this);
        DatabaseReference myref = FirebaseDatabase.getInstance().getReference();
        signUpBtn.setOnClickListener(view -> {
            if (!isInputCorrect()) {
                sendErrorMessage("All fields must be filled");
                return;
            }

            User user = new User(firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(), password.getText().toString());

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                   if (!dataSnapshot.child("users").child(user.getEmail()).exists()){
                       HashMap<String,User> list = new HashMap<>();
                       list.put(user.getEmail(),user);
                       usersRef.child("users").setValue(list);
                       Intent intent = new Intent(SignUp.this, HomePage.class);
                       startActivity(intent);
                   }else {
                       sendErrorMessage("User exists!");
                   }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println(databaseError.toString());
                }
            };
//
//            if (rootRef.child("users").child(user.getEmail()).getKey() == null){
//                myref.child("users").child(user.getEmail()).setValue(user);
//                Intent intent = new Intent(SignUp.this, HomePage.class);
//                startActivity(intent);
//            }else {
//                sendErrorMessage("A user with this email already exists!");
//            }
        });
        }

    private boolean isInputCorrect() { // make a method for the toString().trim().isEmpty() ch
        if (firstName.getText().toString().trim().isEmpty() || lastName.getText().toString().trim().isEmpty() || email.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty() || repeatPassword.getText().toString().trim().isEmpty()) {
            return false;
        }
        return password.getText().toString().equals(repeatPassword.getText().toString());
    }

    private void sendErrorMessage(String message) {
        Toast.makeText(SignUp.this, message, Toast.LENGTH_LONG).show();
    }
}