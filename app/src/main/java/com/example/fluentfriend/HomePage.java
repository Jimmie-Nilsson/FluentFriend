package com.example.fluentfriend;

import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.*;

import java.util.ArrayList;


public class HomePage extends AppCompatActivity {
    private static ArrayList<UserLocation> activeUsers = new ArrayList<>();
    private Button btnProfile;
    private Button btnMessage;
    private Button btnMatch;
    private TextView welcomeText;
    private SwitchCompat activeSwitch;
    private ProgressDialog dialog;
    private User currentUser;
    private double latitude;
    private double longitude;
    private FirebaseDatabase db = FirebaseDatabase.getInstance("https://fluent-friend-dad39-default-rtdb.firebaseio.com/");
    private DatabaseReference activeUsersRef = db.getReference().child("activeusers");
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        btnProfile = (Button) findViewById(R.id.homepage_btn_profile);
        welcomeText = (TextView) findViewById(R.id.homepage_welcomeText);
        btnMatch = (Button) findViewById(R.id.matchPageReturnBack);
        btnMessage = (Button) findViewById(R.id.homepage_messagesbtn);
        activeSwitch = (SwitchCompat) findViewById(R.id.homepage_activeSwitch);

        // Get current user and set welcome text.
        currentUser = UserManager.getCurrentUser();
        welcomeText.setText(String.format("Welcome %s %s", currentUser.getFirstName(), currentUser.getLastName()));

        // Get location settings
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        // Load location and show a loading wheel for the user.
        getCurrentLocation();
        dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading Location");
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        // Fetch all the active users.
        fetchActiveUsersAndCollectInList();

        // If switch is on the user is in at activelist of users and can get matched with other users.
        activeSwitch.setOnClickListener(view -> {
            if (activeSwitch.isChecked()) {
                UserLocation userLoc = new UserLocation(UserManager.getCurrentUser().getEmail(), latitude, longitude);
                MatchPage page = new MatchPage();
                page.addUserActive(userLoc);
            } else if (!activeSwitch.isChecked()) {
                new MatchPage().removeUserActive(UserManager.getCurrentUser());
            }
        });

        btnProfile.setOnClickListener(view -> {
            Intent intentTwo = new Intent(HomePage.this, UserProfilePage.class);
            startActivity(intentTwo);
        });

        btnMatch.setOnClickListener(view -> {
            if (activeSwitch.isChecked()) {
                Intent intentTwo = new Intent(HomePage.this, MatchPage.class);
                startActivity(intentTwo);
            } else {
                Toast.makeText(this, "Must be active", Toast.LENGTH_SHORT).show();
            }
        });

        btnMessage.setOnClickListener(view -> {
            Toast.makeText(HomePage.this, "Not implemented", Toast.LENGTH_SHORT).show();
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
                // set active switch to ON if user is active
                activeSwitch.setChecked(userIsActive(UserManager.getCurrentUser()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
    protected static boolean userIsActive(User user) {
        for (int i = 0; i < activeUsers.size(); i++) {
            if (activeUsers.get(i).getEmail().equals(user.getEmail())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {
                    getCurrentLocation();

                } else {
                    turnOnGPS();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentLocation();
            }
        }
    }

    private void getCurrentLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(HomePage.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(HomePage.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(HomePage.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                                        int index = locationResult.getLocations().size() - 1;
                                        latitude = locationResult.getLocations().get(index).getLatitude();
                                        longitude = locationResult.getLocations().get(index).getLongitude();
                                        dialog.dismiss();

                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void turnOnGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(HomePage.this, "GPS is already turned on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(HomePage.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }
}


