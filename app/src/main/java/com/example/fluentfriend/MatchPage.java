package com.example.fluentfriend;


import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;


import android.widget.TextView;


import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class MatchPage extends AppCompatActivity {
    private static ArrayList<UserLocation> activeUsers = new ArrayList<>();

    private HashMap<String, Double> distanceList = new HashMap<>();
    private List<User> users = new ArrayList<>();


    private LocationRequest locationRequest;
    private Button btnAccept;
    private Button btnDecline;

    private Button btnReturn;
    private TextView textProfile;
    private TextView textBoxHeader;
    private User currentUser;
    private UserLocation currentUserLoc;

    private FirebaseDatabase db = FirebaseDatabase.getInstance("https://fluent-friend-dad39-default-rtdb.firebaseio.com/");
    private DatabaseReference activeUsersRef = db.getReference().child("activeusers");
    private DatabaseReference usersRef = db.getReference().child("users");


    // Test.
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_page);

        textBoxHeader = findViewById(R.id.matchPageTextTop);
        textProfile = findViewById(R.id.matchPageTextShowProfile);
        btnAccept = findViewById(R.id.matchmatchPageBtnAccept);
        btnDecline = findViewById(R.id.matchmatchPageBtnDecline);
        btnReturn = findViewById(R.id.matchPageReturnBack);
        currentUser = UserManager.getCurrentUser();

        // If no match, show this button and disabel the other buttons.
        btnReturn.setClickable(false);
        btnReturn.setVisibility(View.INVISIBLE);





        fetchUsersAndCollectInList();
        fetchActiveUsersAndCollectInList();


        // Kör matchings Algorithm

        btnAccept.setOnClickListener(view -> {
            // Open new frame where we show cafe suggestions etc,
            // The next page need the location, need to solve that.
            Intent i = new Intent(this, LocationSuggestion.class);
            startActivity(i);
        });

        btnDecline.setOnClickListener(view -> {
            // Write code
        });

        btnReturn.setOnClickListener(view -> {
            finish();
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
                    if (!activeUsers.contains(userLoc)) {
                        activeUsers.add(userLoc);
                        Log.d("ActiveUsers", "Added User: " + userLoc.getEmail() + userLoc.getLongitude() + " " + userLoc.getLatitude());
                        Log.d("ActiveUsers", "Added User: " + activeUsers.contains(userLoc));
                    }
                    // Log for debugging


                }
                Log.d("ActiveUsers","Active users count: " + activeUsers.size()); // Log for debugging
                textProfile.setText(activeUsers.size() + " users");
                for (UserLocation l : activeUsers){
                    if (l.getEmail().equals(UserManager.getCurrentUser().getEmail())){
                        addUserActive(l);
                        currentUserLoc = l;
                        break;
                    }
                }
                currentUserLoc = getActiveUser(UserManager.getCurrentUser());
                calcDistanceBetweenUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

    }






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