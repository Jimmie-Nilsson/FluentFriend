package com.example.fluentfriend;

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
}