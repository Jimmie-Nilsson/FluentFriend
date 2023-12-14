package com.example.fluentfriend;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;

import static com.example.fluentfriend.MatchPage.getActiveUser;

public class LocationSuggestion extends AppCompatActivity {

    private String apiKey = BuildConfig.API_KEY;
    private GeoApiContext context;
    private Location midpoint;
    private User user1;
    private User user2;
    private UserLocation user1Location;
    private UserLocation user2Location;
    private double user1lat;
    private double user1long;
    private double user2lat;
    private double user2long;
    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_suggestion);
        this.user1 = UserManager.getCurrentUser();
        Intent intent = getIntent();
        resultView = findViewById(R.id.resultTextView);
        this.user2 = MainActivity.getUser(intent.getStringExtra("email"));
        user1Location = getActiveUser(user1);
        user2Location = getActiveUser(user2);
        this.user1lat = user1Location.getLatitude();
        this.user1long = user1Location.getLongitude();
        this.user2lat = user2Location.getLatitude();
        this.user2long = user2Location.getLongitude();
        Location userLocation1 = new Location("");
        Location userLocation2 = new Location("");
        userLocation1.setLatitude(user1lat);
        userLocation1.setLongitude(user1long);
        userLocation2.setLatitude(user2lat);
        userLocation2.setLongitude(user2long);


        // Initialize midpoint here or from another method after you get user locations
        midpoint = getMiddleDistanceBetweenUsers(userLocation1, userLocation2);
        context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();

        Button findNearbyPlacesButton = findViewById(R.id.find_nearby_places);
        findNearbyPlacesButton.setOnClickListener(view -> {

            fetchNearbyPlaces();

        });

        Button openGoogleMapsButton = findViewById(R.id.open_google_maps); // replace with your actual button ID
        openGoogleMapsButton.setOnClickListener(view -> openGoogleMaps(midpoint));
    }
    private Location getMiddleDistanceBetweenUsers(Location loc1, Location loc2) {
        double lat = (loc1.getLatitude() + loc2.getLatitude()) / 2;
        double longitude = (loc1.getLongitude() + loc2.getLongitude()) / 2;
        Location midpoint = new Location("");
        midpoint.setLatitude(lat);
        midpoint.setLongitude(longitude);
        return midpoint;
    }
    private void openGoogleMaps(Location location) {
        // Create a Uri from an intent string. Use the result to create an Intent.
        Uri gmmIntentUri = Uri.parse("geo:" + location.getLatitude() + "," + location.getLongitude() + "?q=cafes");

        // Create an Intent to open Google Maps at the specified location
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        // Attempt to start an activity that can handle the Intent
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Show an error if Google Maps is not installed
            Toast.makeText(this, "Google Maps is not installed.", Toast.LENGTH_LONG).show();
        }
    }

    private void fetchNearbyPlaces() {
        new Thread(() -> {
            try {
                Log.d("Before UI thread","STARTING THREAD");
                LatLng location = new LatLng(midpoint.getLatitude(), midpoint.getLongitude());
                PlacesSearchResult[] results = PlacesApi.nearbySearchQuery(context, location)
                        .radius(1000) // in meters
                        .type(PlaceType.CAFE)
                        .await()
                        .results;
                // Now you need to handle the results on the UI thread
                Log.d("Before UI thread","WE ARE BEFORE UI THREAD");
                runOnUiThread(() -> {
                    if (results.length > 0) {
                        Log.d("GotResults","Results" + results[0].name);
                        // Update your UI with the results here
                        // e.g., display the name of the first result in a TextView
                        resultView.setText(results[0].name);
                        StringBuilder suggestion = new StringBuilder();
                        for (int i = 0; i < results.length; i++){
                            suggestion.append(results[i].name);
                            suggestion.append("Opening hours:").append(results[i].openingHours);
                            suggestion.append("\n");
                        }
                        resultView.setText(suggestion);

                    } else {
                        Log.d("No Results","NO RESULTS");
                        // Show a message if no results were found
                        Toast.makeText(this, "No nearby cafes found.", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                // Handle the error appropriately
            }
        }).start();
    }

}