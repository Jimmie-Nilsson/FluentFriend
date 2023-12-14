package com.example.fluentfriend;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_suggestion);
        this.user1 = UserManager.getCurrentUser();
        Intent intent = getIntent();
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
        fetchNearbyPlaces();
    }
    private Location getMiddleDistanceBetweenUsers(Location loc1, Location loc2) {
        double lat = (loc1.getLatitude() + loc2.getLatitude()) / 2;
        double longitude = (loc1.getLongitude() + loc2.getLongitude()) / 2;
        Location midpoint = new Location("");
        midpoint.setLatitude(lat);
        midpoint.setLongitude(longitude);
        return midpoint;
    }

    private void fetchNearbyPlaces() {
        new Thread(() -> {
            try {
                LatLng location = new LatLng(midpoint.getLatitude(), midpoint.getLongitude());
                PlacesSearchResult[] results = PlacesApi.nearbySearchQuery(context, location)
                        .radius(1000) // in meters
                        .type(PlaceType.CAFE)
                        .await()
                        .results;

                // Now you need to handle the results on the UI thread
                runOnUiThread(() -> {
                    // Update your UI with the results here
                });
            } catch (Exception e) {
                e.printStackTrace();
                // Handle the error appropriately
            }
        }).start();
    }
}