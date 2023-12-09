package com.example.fluentfriend;


import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;




import android.os.Bundle;


import android.widget.TextView;


import com.google.android.gms.location.LocationRequest;


import java.util.HashMap;


public class MatchPage extends AppCompatActivity {
    private static HashMap<User, UserLocation> activeUsers = new HashMap<>();
    private TextView textBoxOne;
    private LocationRequest locationRequest;
    private Button btnOne;
    private TextView textBoxTwo;
    private Button btnTwo;
    private User currentUser;
    private double longitude;
    private double latitude;


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


        btnTwo.setOnClickListener(view -> {
            UserLocation userLocationOne = activeUsers.get(currentUser);
            UserLocation userLocationTwo = activeUsers.get(user);
            User userOne = userLocationOne.getUser();
            User userTwo = userLocationTwo.getUser();
            double distance = userLocationOne.calcDistanceBetweenUsers(userLocationTwo.getLatitude(), userLocationTwo.getLongitude());

            //distance = Math.round(distance);
            textBoxTwo.setText("Distance between " + userOne.getFirstName() +" and " + userTwo.getFirstName() +" is " + distance + " meters");
        });

    }

    private void addSomeUser() {
        double lat = 59.403223; // Kista galleria
        double lon = 17.944535;
        user = new User("Kalle", "Berglund", "kalleb", "123");

        UserLocation u = new UserLocation(user, lat, lon);
        activeUsers.put(user, u);
    }

    protected static void addUserActive(UserLocation userLocation){
        activeUsers.put(userLocation.getUser(), userLocation);
    }
    protected static void removeUserActive(User user){
        activeUsers.remove(user);
    }
}