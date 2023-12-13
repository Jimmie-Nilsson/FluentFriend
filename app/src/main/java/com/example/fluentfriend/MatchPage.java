package com.example.fluentfriend;


import android.content.Intent;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;


import android.widget.TextView;


import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.*;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class MatchPage extends AppCompatActivity {
    private static ArrayList<UserLocation> activeUsers = new ArrayList<>();

    private HashMap<String, Double> distanceList = new HashMap<>();
    private List<User> users = new ArrayList<>();
    private TextView textBoxOne;
    private LocationRequest locationRequest;
    private Button btnAccept;
    private TextView textBox;
    private Button btnDecline;
    private User currentUser;
    private UserLocation currentUserLoc;
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

        textBox = findViewById(R.id.matchPageText);
        btnAccept = findViewById(R.id.matchmatchPageBtnAccept);
        btnDecline = findViewById(R.id.matchmatchPageBtnDecline);
        currentUser = UserManager.getCurrentUser();
        addSomeUser();
        fetchUsersAndCollectInList();
        fetchActiveUsersAndCollectInList();
        //fetchActiveUsers();
        //calcDistanceBetweenUsers();

        // Kör matchings Algorithm

        btnAccept.setOnClickListener(view -> {
            // Write code
        });

        btnDecline.setOnClickListener(view -> {
            // Write code
        });

    }

    private void addSomeUser() {
        double lat = 59.403223; // Kista galleria
        double lon = 17.944535;
        user = new User("Kalle", "Berglund", "kalleb", "123");

        UserLocation u = new UserLocation(user.getEmail(), lat, lon);
        activeUsers.add(u);
    }

    protected void addUserActive(UserLocation userLocation) {
        activeUsersRef.child(userLocation.getEmail()).setValue(userLocation);
        activeUsers.add(userLocation);
        currentUserLoc = userLocation;
    }

    private void fetchUsersAndCollectInList() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    User user = userSnapshot.getValue(User.class);
                    // Kolla denna kod Nu kompilerar den inte och fungerar inte eftersom vi andrade....

                    if (userId != null) {
                        users.add(user);
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
       for (int i = 0; i < activeUsers.size(); i++) {
           if (activeUsers.get(i).getEmail().equals(user.getEmail())) {
               return activeUsers.get(i);
           }
       }
       // ?? Kom på bättre lösning. Där denna metoden används kollar vi redan med metoden userIsActive.
       return null;
    }

    protected static boolean userIsActive(User user) {
        for (int i = 0; i < activeUsers.size(); i++) {
            if (activeUsers.get(i).getEmail().equals(user.getEmail())) {
                return true;
            }
        }
        return false;
    }

    protected void removeUserActive(User user) {
        activeUsersRef.child(user.getEmail()).removeValue();

        /* Find the right element and remove it from the list.
         * Använda ett hashSet är kanske en bättre lösing om listan,
         * blir väldigt stor. Vi kanske dock inte bryr oss.
         */
        for (int i = 0; i < activeUsers.size(); i++) {
            if (activeUsers.get(i).getEmail().equals(user.getEmail())) {
                activeUsers.remove(i);
                break;
            }
        }
    }
    private void calcDistanceBetweenUsers() {

        // Calcualte the distance between current user and all the other active users.
         for (int i = 0; i < activeUsers.size(); i++) {
            double distance = currentUserLoc.calcDistanceBetweenUsers(activeUsers.get(i).getLatitude(), activeUsers.get(i).getLongitude());
            distanceList.put(activeUsers.get(i).getEmail(), distance);
        }

         // Sort the list

    }

    private void matchingalgorithm() {

        /* Psudocode
         * if no user near = return
         * Get the users that are in less than 1km away (max 5)
         *
         * Start checks if userOne profile matches the currentuser profile
         *
         * Check the language
         * if no match there = break
         *
         * check the checkboxes.
         * if match there = add that (So we can print that they both like "fika" tex.
         *
         *
         * end of userOne profile matching. Repeat with the other 2-5 users
         *
         * Förslag att vi har typ en poäng hur mkt man matchar. kan komma på något // KB
         *
         */
    }
}