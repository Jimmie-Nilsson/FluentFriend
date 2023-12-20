package com.example.fluentfriend;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.*;
import java.util.stream.Collectors;

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
    Locale swedenLocale = new Locale("sv", "SE");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_suggestion);

        //gets the two User objects for the two users that have matched /G
        this.user1 = UserManager.getCurrentUser();
        Intent intent = getIntent();
        this.user2 = (User) intent.getSerializableExtra("matchedUser");

        //saves the UserLocation object for the two users that have matched (they are in the DB active list) /G
        user1Location = getActiveUser(user1);
        user2Location = getActiveUser(user2);

        //calculates the middle point between the two users based on their respective locations /G
        midpoint = getMiddleDistanceBetweenUsers();

        // Initialize the Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), API_KEY);
        }
        placesClient = Places.createClient(this);

        findCommonInterests();
        displayCommonInterests();

        resultView = findViewById(R.id.resultTextView);

        //button that runs the method for finding nearby places /G
        Button findNearbyPlacesButton = findViewById(R.id.find_nearby_places);
        findNearbyPlacesButton.setOnClickListener(view -> {
            fetchNearbyPlaces();
        });

        //button that leaves the app and opens Google Maps with a search query based on location and interests /G
        Button openGoogleMapsButton = findViewById(R.id.open_google_maps);
        openGoogleMapsButton.setOnClickListener(view -> openGoogleMaps(midpoint));
    }

    //these methods check what interests the users have in common /G
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

    //displays the common interests in the textView /G
    private void displayCommonInterests() {
        String interestsText = "";
        if (!commonInterests.isEmpty()) {
            // Joins common interests in a single string separated by commas
            interestsText += String.join(", ", commonInterests);
        } else {
            // Handles no common interests
            interestsText = "You have no common interests :(";
        }
        TextView textViewCommonInterests = findViewById(R.id.CommonInterestsTextView); // Replace with your actual TextView ID
        textViewCommonInterests.setText(interestsText);

    }

    //finds the common interests of both users and adds them to a list of Strings that can be shown to the user,
    //as well as to a querybuilder that is formatted for Google search queries to be used by the Google Maps query /G
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

    //finds the lat/long middle point between the two users, returns as Location object /G
    private Location getMiddleDistanceBetweenUsers() {
        double lat = (user1Location.getLatitude() + user2Location.getLatitude()) / 2;
        double longitude = (user1Location.getLongitude() + user2Location.getLongitude()) / 2;
        Location midpoint = new Location("");
        midpoint.setLatitude(lat);
        midpoint.setLongitude(longitude);
        return midpoint;
    }

    //When Google Maps button is pressed, Google Maps opens with a search for the common interests /G
    private void openGoogleMaps(Location location) {
        //sets the query depending on the common interests
        String query = queryBuilder.toString();

        // Create a Uri from an intent string. Use the result to create an Intent.
        //write easier-to-understand comment what this does /G
        Uri gmmIntentUri = Uri.parse("geo:" + location.getLatitude() + "," + location.getLongitude() + "?q=" + Uri.encode(query));

        // Create an Intent to open Google Maps at the specified location
        //same here /G
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        // Attempt to start an activity that can handle the Intent
        //also here /G
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Display error text if Google Maps is not installed
            Toast.makeText(this, "Google Maps is not installed.", Toast.LENGTH_LONG).show();
        }
    }

    private void fetchNearbyPlaces() {
        //for debugging
        Log.d(TAG, "Your common interests are: ");
        for (String commonInterest : commonInterestsAPIFormat) {
            Log.d(TAG, commonInterest);
        }

        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.TYPES,
                Place.Field.LAT_LNG,
                Place.Field.RATING,
                Place.Field.PRICE_LEVEL);

        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        //user must allow
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // ActivityCompat#requestPermissions logic here...
            return;
        }

        //debugging
        Log.d(TAG, "Making Places API call...");

        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);

        placeResponse.addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Log.d(TAG, "Places API call was successful.");
                FindCurrentPlaceResponse response = task.getResult();

                //debugging
                Log.d(TAG,"Places returned by the API call:");
                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    Place place = placeLikelihood.getPlace();
                    Log.d(TAG, place.getName() + " " + getDistanceFromMidpoint(Objects.requireNonNull(place.getLatLng()))+ ", Types: " + place.getPlaceTypes());
                }
                Log.d(TAG,"List is now being filtered");
                List<Place> nearbyPlaces = response.getPlaceLikelihoods().stream()
                        .map(PlaceLikelihood::getPlace)
                        .filter(place -> place.getLatLng() != null)
                        .filter(place -> place.getPlaceTypes() != null && place.getPlaceTypes().stream()
                                .anyMatch(typeString -> commonInterestsAPIFormat.contains(typeString.toLowerCase())))
                        .collect(Collectors.toList());

                //debugging checks if any places were added to the list
                if (!nearbyPlaces.isEmpty()) {
                    //Shows what places were added after filtering
                    for (Place place : nearbyPlaces) {
                        Log.d(TAG, "These are the places left after filtering:");
                        Log.d(TAG, place.getName() + " at " + getDistanceFromMidpoint(place.getLatLng())
                                + " meters from the midpoint");
                    }

                    Log.d(TAG, "List is now being sorted based on distance from midpoint...");
                    nearbyPlaces.sort(Comparator.comparing(place -> getDistanceFromMidpoint(place.getLatLng())));

                    Place firstPlace = nearbyPlaces.get(0);
                    String firstPlaceDistance = formatDistance(getDistanceFromMidpoint(firstPlace.getLatLng()));

                    Log.d(TAG, "The list has been sorted and the closest place is: ");
                    Log.d(TAG, firstPlace.getName() + " at distance: " + firstPlaceDistance + " from midpoint.");

                    // Check if the first place has a non-null ID
                    if (firstPlace.getId() != null) {
                        List<Place.Field> detailFields = Collections.singletonList(Place.Field.OPENING_HOURS);
                        FetchPlaceRequest detailRequest = FetchPlaceRequest.newInstance(firstPlace.getId(), detailFields);

                        // Fetch the details for the first place
                        placesClient.fetchPlace(detailRequest).addOnSuccessListener(responseTwo -> {
                            Place placeDetails = responseTwo.getPlace(); // Detailed place object for the first place
                            String openingHoursText = "Opening hours not available";

                            if (placeDetails.getOpeningHours() != null) {
                                List<String> weekdayTexts = placeDetails.getOpeningHours().getWeekdayText();
                                if (weekdayTexts != null && !weekdayTexts.isEmpty()) {
                                    openingHoursText = TextUtils.join("\n", weekdayTexts);
                                }
                            }

                            // Update the UI with the opening hours of the first place
                            final String placeInfo = firstPlace.getName() + ", " + firstPlaceDistance + " away :\n" + openingHoursText + "\n\n";
                            runOnUiThread(() -> resultView.setText(placeInfo));
                        }).addOnFailureListener(exception -> {
                            Log.e(TAG, "Error fetching place details: " + exception.getMessage());
                        });
                    } else {
                        Log.d(TAG, "First place has a null ID");
                        runOnUiThread(() -> resultView.setText("First place has a null ID"));
                    }
                } else {
                    Log.d(TAG, "No nearby places found");
                    runOnUiThread(() -> resultView.setText("No nearby places found"));
                    }
            } else {
                //handle cases where task is not successful
            }
        });
    }
    private float getDistanceFromMidpoint(LatLng placeLatLng) {
        Location placeLocation = new Location(""); // provider is not needed here
        placeLocation.setLatitude(placeLatLng.latitude);
        placeLocation.setLongitude(placeLatLng.longitude);
        return getDistance(midpoint, placeLocation);
    }
    private float getDistance(Location originLocation, Location targetLocation) {
        float[] results = new float[1];
        Location.distanceBetween(originLocation.getLatitude(), originLocation.getLongitude(),
                targetLocation.getLatitude(), targetLocation.getLongitude(),
                results);
        return results[0]; // distance in meters
    }

    //method for formatting distance printout if needed
    private String formatDistance(float distance) {
        if (distance < 1000) {
            return String.format(swedenLocale, "%d meter", (int) distance);
        } else {
            return String.format(swedenLocale, "%.2f km", distance / 1000);
        }
    }
}