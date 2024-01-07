package com.example.fluentfriend;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.fluentfriend.MatchPage.getActiveUser;

public class LocationSuggestion extends AppCompatActivity {

    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String TAG = "NearbyPlacesDebug";
    private PlacesClient placesClient;
    private Location midpoint;
    private User user1;
    private User user2;
    private UserLocation user1Location;
    private UserLocation user2Location;
    private TextView resultView;
    private final List<String> commonInterests = new ArrayList<>();
    private final List<String> commonInterestsAPIFormat = new ArrayList<>();
    private final StringBuilder queryBuilder = new StringBuilder();
    private static final int SEARCH_RADIUS_IN_METERS = 1000;
    Locale swedenLocale = new Locale("sv", "SE");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_suggestion);

        this.user1 = UserManager.getCurrentUser();
        Intent intent = getIntent();
        this.user2 = (User) intent.getSerializableExtra("matchedUser");

        user1Location = getActiveUser(user1);
        user2Location = getActiveUser(user2);

        midpoint = getMidPointBetweenUsers();

        // Initialize the Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), API_KEY);
        }
            placesClient = Places.createClient(this);

        findCommonInterests();
        displayCommonInterests();

        resultView = findViewById(R.id.resultTextView);

        Button findNearbyPlacesButton = findViewById(R.id.find_nearby_places);
        findNearbyPlacesButton.setOnClickListener(view -> {
            fetchNearbyPlaces();
        });

        Button openGoogleMapsButton = findViewById(R.id.open_google_maps);
        openGoogleMapsButton.setOnClickListener(view -> openGoogleMaps(midpoint));
    }

    //these methods check what interests the users have in common
    private boolean doBothLikeFika() {
        return user1.isFikaChecked() && user2.isFikaChecked();
    }

    private boolean doBothLikeMuseum() {
        return user1.isMuseumChecked() && user2.isMuseumChecked();
    }

    private boolean doBothLikeBar() {
        return user1.isBarChecked() && user2.isBarChecked();
    }

    private boolean doBothLikeCityWalk() {
        return user1.isCityWalksChecked() && user2.isCityWalksChecked();
    }

    //displays the common interests in the textView
    private void displayCommonInterests() {
        String interestsText = "";
        if (!commonInterests.isEmpty()) {
            // Joins common interests in a single string separated by commas
            interestsText += String.join(", ", commonInterests);
        } else {
            // Handles no common interests
            interestsText = "You have no common interests :(";
        }
        TextView textViewCommonInterests = findViewById(R.id.CommonInterestsTextView);
        textViewCommonInterests.setText(interestsText);

    }
    private void findCommonInterests() {
        if (doBothLikeFika()) {
            commonInterests.add("Fika");
            commonInterestsAPIFormat.add("cafe".toLowerCase());
            queryBuilder.append("Cafes");
        }
        if (doBothLikeMuseum()) {
            commonInterests.add("Museums");
            commonInterestsAPIFormat.add("museum".toLowerCase());
            if (queryBuilder.length() > 0) queryBuilder.append(" or ");
            queryBuilder.append("Museums");
        }
        if (doBothLikeBar()) {
            commonInterests.add("Bars");
            commonInterestsAPIFormat.add("bar".toLowerCase());
            if (queryBuilder.length() > 0) queryBuilder.append(" or ");
            queryBuilder.append("Bars");
        }
        if (doBothLikeCityWalk()) {
            commonInterests.add("City Walks");
        }
    }

    //finds the lat/long middle point between the two users, returns as Location object
    private Location getMidPointBetweenUsers() {
        double lat = (user1Location.getLatitude() + user2Location.getLatitude()) / 2;
        double longitude = (user1Location.getLongitude() + user2Location.getLongitude()) / 2;
        Location midpoint = new Location("");
        midpoint.setLatitude(lat);
        midpoint.setLongitude(longitude);
        return midpoint;
    }

    //When Google Maps button is pressed, Google Maps opens with a search for the common interests
    private void openGoogleMaps(Location location) {
        //sets the query depending on the common interests
        String query = queryBuilder.toString();

        // Create a Uri from an intent string. Use the result to create an Intent.
        Uri gmmIntentUri = Uri.parse("geo:" + location.getLatitude() + "," + location.getLongitude() + "?q=" + Uri.encode(query));

        // Create an Intent to open Google Maps at the specified location
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        // Attempt to start an activity that can handle the Intent
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Display error text if Google Maps is not installed
            Toast.makeText(this, "Google Maps is not installed.", Toast.LENGTH_LONG).show();
        }
    }

    private void fetchNearbyPlaces() {
        Log.d(TAG, "Button has been pressed");
        double latitude = midpoint.getLatitude();
        double longitude = midpoint.getLongitude();

        String location = latitude + "," + longitude;
        Log.d(TAG, "Common interests for API call are: " + commonInterestsAPIFormat);

        for (String type : commonInterestsAPIFormat) {
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                    "?location=" + location +
                    "&radius=" + SEARCH_RADIUS_IN_METERS +
                    "&type=" + type +
                    "&key=" + API_KEY;
            new PlacesFetchTask().execute(url);
        }
    }

    private class PlacesFetchTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.d(TAG, "Now in doInBackGround");
            try {

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                inputStream.close();
                connection.disconnect();

                return result.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "Now in onPostExecute");
            Log.d("API Response", result != null ? result : "Response was null");
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray results = jsonObject.getJSONArray("results");
                    StringBuilder placesBuilder = new StringBuilder();

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject place = results.getJSONObject(i);
                        String name = place.getString("name");

                        JSONArray typesJsonArray = place.getJSONArray("types");
                        String placeType = null;
                        for (int j = 0; j < typesJsonArray.length(); j++) {
                            String type = typesJsonArray.getString(j);
                            if (commonInterestsAPIFormat.contains(type)) {
                                placeType = type;
                                break;
                            }
                        }

                        JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
                        double placeLat = location.getDouble("lat");
                        double placeLng = location.getDouble("lng");
                        float distanceFromFixedPoint = getDistanceFromMidpointToLocation(placeLat, placeLng);
                        float distanceFromUserToLocation = getDistanceBetweenTwoPoints(user1Location.getLatitude(),
                                user1Location.getLongitude(), placeLat, placeLng);

                        String isOpenNowText = "Open status not available";
                        if (place.has("opening_hours")) {
                            JSONObject openingHours = place.getJSONObject("opening_hours");
                            boolean isOpenNow = openingHours.optBoolean("open_now", false);
                            isOpenNowText = isOpenNow ? "It's open! :)" : "It's closed :(";
                        }

                        placesBuilder.append("Name: ").append(name)
                                .append(placeType != null ? "\nType: " + placeType.toUpperCase() : "")
                                .append("\nDistance from middle of users: ").append(formatDistance(distanceFromFixedPoint))
                                .append("\nDistance from you: ").append(formatDistance(distanceFromUserToLocation))
                                .append("\n").append("Status: ").append(isOpenNowText).append("\n\n");
                    }

                    // Update the UI with the fetched details
                    resultView.setText(placesBuilder.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "JSONException occured");
                }
            } else {
                Log.d(TAG, "The result is null");
            }
        }
    }

    private float getDistanceFromMidpointToLocation(double lat, double lng) {
        return getDistanceBetweenTwoPoints(midpoint.getLatitude(), midpoint.getLongitude(), lat, lng);
    }
    private float getDistanceBetweenTwoPoints(double lat1, double lng1, double lat2, double lng2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lng1, lat2, lng2, results);
        return results[0]; // distance in meters
    }

    private String formatDistance(float distance) {
        if (distance < 1000) {
            return String.format(swedenLocale, "%d meters", (int) distance);
        } else {
            return String.format(swedenLocale, "%.2f kilometers", distance / 1000);
        }
    }
}