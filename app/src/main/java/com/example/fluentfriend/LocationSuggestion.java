package com.example.fluentfriend;

import android.location.Location;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class LocationSuggestion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_suggestion);
        // Finns en metod "UserLocation getActiveUser(User user)" i MatchPage.
        // Kalla på den med currentUser som input så bör du får tillgång till platsen
    }

    private Location getMiddleDistanceBetweenUsers(Location loc1, Location loc2) {
        double lat = (loc1.getLatitude() + loc2.getLatitude()) / 2;
        double longitude = (loc1.getLongitude() + loc2.getLongitude()) / 2;

        Location midpoint = new Location("");
        midpoint.setLatitude(lat);
        midpoint.setLongitude(longitude);
        return midpoint;
    }
    // Example: Find cafes near the midpoint
    String type = "cafe"; // can be cafe, bar, museum etc.
    String apiKey = "YOUR_API_KEY";
/*    GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(apiKey)
            .build();
    LatLng location = new LatLng(midpoint.getLatitude(), midpoint.getLongitude());
    PlacesSearchResult[] results = PlacesApi.nearbySearchQuery(context, location)
            .radius(5000) // in meters
            .type(type)
            .await()
            .results;*/
}