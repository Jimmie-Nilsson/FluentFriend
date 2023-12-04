package com.example.fluentfriend;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private HashMap<String, User> userList = new HashMap<>();
    private Button btnLogIn;
    private Button btnCreateAccount;
    private TextView email;
    private  TextView password;
    private  User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addUsers(); // Lägg till användare

        btnCreateAccount = (Button) findViewById(R.id.main_btnCreateAccount);
        btnLogIn = (Button) findViewById(R.id.main_btnLogIn);
        email = (TextView) findViewById(R.id.main_emailField);
        password = (TextView) findViewById(R.id.main_passwordField) ;

        btnCreateAccount.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignUp.class);
            startActivity(intent);
        });

        btnLogIn.setOnClickListener(view -> {
            String inputEmail = email.getText().toString();
            String inputPassword = password.getText().toString();
            user = userList.get(inputEmail);

            if (inputPassword.equals(user.getPassword())) {
                Intent intent = new Intent(MainActivity.this, HomePage.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(MainActivity.this, "LOGIN FAILED", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void addUsers() {
        User userOne = new User("admin", "admin", "admin", "123");
        userList.put("admin", userOne);
    }

    public boolean addNewUser(String firstName, String lastname, String email, String password) {
        if (userList.containsKey(email)) {
            return false;
        } else {
            User user = new User(firstName,lastname,email,password);
            return true;
        }
    }
}