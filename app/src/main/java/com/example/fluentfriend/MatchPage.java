package com.example.fluentfriend;


import android.os.Build;
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
import java.util.Map;


public class MatchPage extends AppCompatActivity {
    private static HashMap<User, UserLocation> activeUsers = new HashMap<>();

    private HashMap<User, Double> distanceList = new HashMap<>();

    private List<User> users = new ArrayList<>();
    private TextView textBoxOne;
    private LocationRequest locationRequest;
    private Button btnOne;
    private TextView textBoxTwo;
    private Button btnTwo;
    private User currentUser;
    private double longitude;
    private double latitude;
    private FirebaseDatabase db = FirebaseDatabase.getInstance("https://fluent-friend-dad39-default-rtdb.firebaseio.com/");
    private DatabaseReference activeUsersRef = db.getReference().child("activeusers");
    private DatabaseReference usersRef = db.getReference().child("users");


    // Test.
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_page);

        textBoxTwo = findViewById(R.id.matchPageText2);
        btnTwo = findViewById(R.id.matchmatchPageBtnCalcDistance);
        currentUser = UserManager.getCurrentUser();
        addSomeUser();
        fetchUsersAndCollectInList();
        fetchActiveUsersAndCollectInList();
        //fetchActiveUsers();
        calcDistanceBetweenUsers();

        // Kör matchings Algorithm  (Ka

    }

    private void addSomeUser() {
        double lat = 59.403223; // Kista galleria
        double lon = 17.944535;
        user = new User("Kalle", "Berglund", "kalleb", "123");

        UserLocation u = new UserLocation(user, lat, lon);
        activeUsers.put(user, u);
    }

    protected void addUserActive(UserLocation userLocation) {
        activeUsersRef.child(userLocation.getUser().getEmail()).setValue(userLocation);
        activeUsers.put(userLocation.getUser(), userLocation);
    }

    private void fetchUsersAndCollectInList() {
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
                    // testing code
                    System.err.println(userLoc.getLatitude() + " test " + userLoc.getLongitude());

                    // Assuming UserLocation class has a setUser method
                    User user = userSnapshot.child("user").getValue(User.class);
                    activeUsers.put(user, userLoc);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }


//    public void fetchActiveUsers(){
//        activeUsersRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
//                UserLocation newPost = dataSnapshot.getValue(UserLocation.class);
//              if (!activeUsers.containsValue(newPost)){
//                  activeUsers.put(newPost.getUser(),newPost);
//              }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
//                UserLocation newPost = dataSnapshot.getValue(UserLocation.class);
//                if (activeUsers.containsValue(newPost)) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        activeUsers.replace(newPost.getUser(), newPost);
//                    }
//                }
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                UserLocation newPost = dataSnapshot.getValue(UserLocation.class);
//                activeUsers.remove(newPost.getUser());
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        });
//    }

    protected static UserLocation getActiveUser(User user) {
        return activeUsers.get(user);
    }

    protected static boolean userIsActive(User user) {
        return activeUsers.containsKey(user);
    }

    protected void removeUserActive(User user) {
        activeUsersRef.child(user.getEmail()).removeValue();
        activeUsers.remove(user);
    }

    private void calcDistanceBetweenUsers() {
        UserLocation userLocationOne = activeUsers.get(currentUser);
        for (UserLocation userLocation : activeUsers.values()) {
            double distance = userLocationOne.calcDistanceBetweenUsers(userLocation.getLatitude(), userLocation.getLongitude());
            distanceList.put(userLocation.getUser(), distance);
        }

        // Testing code
        for (User user : distanceList.keySet()) {

            textBoxTwo.setText(activeUsers.size() + " ");
        }

    }
}