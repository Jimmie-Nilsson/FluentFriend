package com.example.fluentfriend;


import android.widget.Button;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;


import android.widget.TextView;


import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MatchPage extends AppCompatActivity {
    private static HashMap<User, UserLocation> activeUsers = new HashMap<>();

    private List<User> users = new ArrayList<>();
    private TextView textBoxOne;
    private LocationRequest locationRequest;
    private Button btnOne;
    private TextView textBoxTwo;
    private Button btnTwo;
    private User currentUser;
    private double longitude;
    private double latitude;
    private static FirebaseDatabase db = FirebaseDatabase.getInstance("https://fluent-friend-dad39-default-rtdb.firebaseio.com/");
    private static DatabaseReference activeUsersRef = db.getReference().child("activeusers");
    private static DatabaseReference usersRef = db.getReference().child("users");



    // Test.
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_page);
        textBoxOne = findViewById(R.id.matchPageText);
        textBoxTwo = findViewById(R.id.matchPageText2);
        btnTwo = findViewById(R.id.matchmatchPageBtnCalcDistance);
        currentUser = UserManager.getCurrentUser();
        addSomeUser();
        fetchActiveUsersAndCollectInList();
        fetchUsersAndCollectInList();
        btnTwo.setOnClickListener(view -> {
            UserLocation userLocationOne = activeUsers.get(currentUser);
            UserLocation userLocationTwo = activeUsers.get(user);
            User userOne = userLocationOne.getUser();
            User userTwo = userLocationTwo.getUser();
            double distance = userLocationOne.calcDistanceBetweenUsers(userLocationTwo.getLatitude(), userLocationTwo.getLongitude());

            //distance = Math.round(distance);
            textBoxTwo.setText("Distance between " + userOne.getFirstName() + " and " + userTwo.getFirstName() + " is " + distance + " meters");
        });

    }

    private void addSomeUser() {
        double lat = 59.403223; // Kista galleria
        double lon = 17.944535;
        user = new User("Kalle", "Berglund", "kalleb", "123");

        UserLocation u = new UserLocation(user, lat, lon);
        activeUsers.put(user, u);
    }

    protected void addUserActive(UserLocation userLocation) {
            System.err.println(userLocation.getLongitude());
            System.err.println(userLocation.getLatitude());
            activeUsersRef.child(userLocation.getUser().getEmail()).setValue(userLocation);
            activeUsers.put(userLocation.getUser(), userLocation);


    }

    private void fetchUsersAndCollectInList(){
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    UserLocation userLoc = userSnapshot.getValue(UserLocation.class);
                    if (userId != null && userLoc != null) {
                        users.add(userLoc.getUser());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }



    private void fetchActiveUsersAndCollectInList() {
        activeUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    UserLocation userLoc = userSnapshot.getValue(UserLocation.class);
                    if (userId != null && userLoc != null) {
                        activeUsers.put(userLoc.getUser(),userLoc);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    protected static void removeUserActive(User user) {
        activeUsersRef.child(user.getEmail()).removeValue();
        activeUsers.remove(user);
    }
}