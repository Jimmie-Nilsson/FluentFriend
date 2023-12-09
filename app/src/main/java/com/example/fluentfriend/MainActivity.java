package com.example.fluentfriend;

import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.*;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static HashMap<String, User> userList = new HashMap<>();
    private Button btnLogIn;
    private Button btnCreateAccount;
    private TextView email;
    private TextView password;
    private User user;
    FirebaseDatabase db = FirebaseDatabase.getInstance("https://fluent-friend-dad39-default-rtdb.firebaseio.com/");
    DatabaseReference userRef = db.getReference().child("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //addUsers(); // Lägg till användare

        fetchUsersAndCollectInList();

        btnCreateAccount = (Button) findViewById(R.id.saveSettingsButton);
        btnLogIn = (Button) findViewById(R.id.main_btnLogIn);
        email = (TextView) findViewById(R.id.main_emailField);
        password = (TextView) findViewById(R.id.main_passwordField);

        btnCreateAccount.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignUp.class);
            startActivity(intent);
        });

        btnLogIn.setOnClickListener(view -> {
            if (email.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty()) {
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

    private void wrongInputMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void addUsers() {
        User userOne = new User("admin", "admin", "admin", "123");
        User userTwo = new User("Bosse", "Nillson", "bosse", "123");
        userList.put("admin", userOne);
        userList.put("bosse", userTwo);
    }

    public static boolean addNewUser(String firstName, String lastname, String email, String password) {
        if (userList.containsKey(email)) {
            return false;
        } else {
            User user = new User(firstName, lastname, email, password);
            return true;
        }
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



}