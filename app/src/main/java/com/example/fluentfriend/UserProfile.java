package com.example.fluentfriend;

import android.widget.CheckBox;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class UserProfile extends AppCompatActivity {

    private CheckBox cityWalksCheckBox;
    private CheckBox museumCheckBox;
    private CheckBox barCheckBox;
    private CheckBox fikaCheckBox;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        cityWalksCheckBox = findViewById(R.id.cityWalksCheckBox);
        museumCheckBox = findViewById(R.id.museumCheckBox);
        barCheckBox = findViewById(R.id.barCheckBox);
        fikaCheckBox = findViewById(R.id.fikaCheckBox);

        cityWalksCheckBox.setChecked(currentUser.isCityWalksChecked());
        museumCheckBox.setChecked(currentUser.isMuseumChecked());
        barCheckBox.setChecked(currentUser.isBarChecked());
        fikaCheckBox.setChecked(currentUser.isFikaChecked());

    }
}