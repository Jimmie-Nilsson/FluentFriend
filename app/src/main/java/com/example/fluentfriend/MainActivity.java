package com.example.fluentfriend;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static HashMap<String, User> userList = new HashMap<>();
    private Button btnLogIn;
    private Button btnCreateAccount;
    private TextView email;
    private  TextView password;
    private  User user;

    // test code here
    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addUsers(); // Lägg till användare


        db = FirebaseFirestore.getInstance();





        btnCreateAccount = (Button) findViewById(R.id.saveSettingsButton);
        btnLogIn = (Button) findViewById(R.id.main_btnLogIn);
        email = (TextView) findViewById(R.id.main_emailField);
        password = (TextView) findViewById(R.id.main_passwordField) ;

        btnCreateAccount.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignUp.class);
            startActivity(intent);
        });

        btnLogIn.setOnClickListener(view -> {
            if (email.getText().toString().trim().isEmpty()  || password.getText().toString().trim().isEmpty()){
                wrongInputMessage("Empty field");
                return;
            }

            String inputEmail = email.getText().toString();
            String inputPassword = password.getText().toString();

            if (!userList.containsKey(inputEmail)) {
                wrongInputMessage("Email doesn't exsits");
                return;
            }

            user = userList.get(inputEmail);

            if (inputPassword.equals(user.getPassword())) {
                Intent intent = new Intent(MainActivity.this, HomePage.class);
                intent.putExtra("firstName", user.getFirstName());
                intent.putExtra("lastName", user.getLastName());
                UserManager.setCurrentUser(user);
                startActivity(intent);

            } else {
                wrongInputMessage("Wrong password");
            }
        });
    }
    private void wrongInputMessage(String message ) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }
    private void addUsers() {
        User userOne = new User("admin", "admin", "admin", "123");
        userList.put("admin", userOne);
    }

    public static boolean addNewUser(String firstName, String lastname, String email, String password) {
        if (userList.containsKey(email)) {
            return false;
        } else {
            User user = new User(firstName,lastname,email,password);
            return true;
        }
    }

}