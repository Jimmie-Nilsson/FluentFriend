package com.example.fluentfriend;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;
import java.util.HashMap;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static HashMap<String, User> userList = new HashMap<>();
    private Button btnLogIn;
    private Button btnCreateAccount;
    private TextView email;
    private TextView password;
    private User user;
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://fluent-friend-dad39-default-rtdb.firebaseio.com/");
    private DatabaseReference userRef = database.getReference().child("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCreateAccount = (Button) findViewById(R.id.saveSettingsButton);
        btnLogIn = (Button) findViewById(R.id.main_btnLogIn);
        email = (TextView) findViewById(R.id.main_emailField);
        password = (TextView) findViewById(R.id.main_passwordField);

        // Get users som the DB
        FirebaseApp.initializeApp(this);
        fetchUsersAndCollectInList();

        btnCreateAccount.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignUp.class);
            startActivity(intent);
        });

        btnLogIn.setOnClickListener(view -> {
            // Check so the text fields is filled out
            if (email.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty()) {
                wrongInputMessage("Empty field");
                return;
            }

            // Get the input from the input fields
            String inputEmail = email.getText().toString();
            String inputPassword = password.getText().toString();

            if (!userList.containsKey(inputEmail)) {
                wrongInputMessage("Email doesn't exsits");
                return;
            }

            user = userList.get(inputEmail);

            if (inputPassword.equals(user.getPassword())) {
                Intent intent = new Intent(MainActivity.this, HomePage.class);
                UserManager.setCurrentUser(user);
                startActivity(intent);
            } else {
                wrongInputMessage("Wrong password");
            }
        });
    }

    private void wrongInputMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void fetchUsersAndCollectInList() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    User user = userSnapshot.getValue(User.class);
                    if (userId != null && user != null) {
                        userList.put(userId, user);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
    protected static Set<String> getRegisteredUsers(){
        return userList.keySet();
    }

    protected static User getUser(String email){
        for (User user : userList.values()){
            if (user.getEmail().equalsIgnoreCase(email)){
                return user;
            }
        }
        return null;
    }
}