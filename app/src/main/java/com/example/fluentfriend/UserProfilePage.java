package com.example.fluentfriend;

import android.text.InputType;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class UserProfilePage extends AppCompatActivity {

    private User currentUser;
    private TextView displayUserName;
    private CheckBox cityWalksCheckBox;
    private CheckBox museumCheckBox;
    private CheckBox barCheckBox;
    private CheckBox fikaCheckBox;
    private CheckBox maleCheckBox;
    private CheckBox femaleCheckBox;
    private CheckBox otherCheckBox;
    private EditText editTextBiography;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //loads the current user
        currentUser = UserManager.getCurrentUser();

        //displays the current user's name
        displayUserName = findViewById(R.id.displayUserNameTextView);
        displayUserName.setText(currentUser.getFirstName() + " " + currentUser.getLastName());

        //loads the current user's biography
        editTextBiography = findViewById(R.id.bioEditText);
        buttonSave = findViewById(R.id.saveSettingsButton);

        loadBiography();

        //connects the checkboxes graphic to variables in this class
        cityWalksCheckBox = findViewById(R.id.cityWalksCheckBox);
        museumCheckBox = findViewById(R.id.museumCheckBox);
        barCheckBox = findViewById(R.id.barCheckBox);
        fikaCheckBox = findViewById(R.id.fikaCheckBox);
        otherCheckBox = findViewById(R.id.otherCheckBox);
        femaleCheckBox = findViewById(R.id.femaleCheckBox);
        maleCheckBox = findViewById(R.id.maleCheckBox);


        //load current checkbox status from the user object
        cityWalksCheckBox.setChecked(currentUser.isCityWalksChecked());
        museumCheckBox.setChecked(currentUser.isMuseumChecked());
        barCheckBox.setChecked(currentUser.isBarChecked());
        fikaCheckBox.setChecked(currentUser.isFikaChecked());

        otherCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                maleCheckBox.setChecked(false);
                femaleCheckBox.setChecked(false);
                otherCheckBox.setChecked(isChecked);
                // Set gender i User, antingen här eller om det finns en metod som sparar allt när trycker på knappen "save" knappen.
                // Finns ingen gender var i User klassen ännu :)
            }
        });

        femaleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                otherCheckBox.setChecked(false);
                maleCheckBox.setChecked(false);
                femaleCheckBox.setChecked(isChecked);
                // Set gender i User, antingen här eller om det finns en metod som sparar allt när trycker på knappen "save" knappen.
            }
        });

        maleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                femaleCheckBox.setChecked(false);
                otherCheckBox.setChecked(false);
                maleCheckBox.setChecked(isChecked);
                // Set gender i User, antingen här eller om det finns en metod som sparar allt när trycker på knappen "save" knappen.
            }
        });

        //if user changed status of this checkBox
        cityWalksCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentUser.setCityWalksChecked(isChecked);
            }
        });
        museumCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentUser.setMuseumChecked(isChecked);
            }
        });

        barCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentUser.setBarChecked(isChecked);
            }
        });

        fikaCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentUser.setFikaChecked(isChecked); //vad gör denna?
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                saveBiography();
                saveCheckBoxes(); //behövs denna?
            }
        });

    }
    private void loadBiography() {
        String biography = currentUser.getBiography();
        editTextBiography.setText(biography);
    }

    private void saveBiography(){
        String newBiography = editTextBiography.getText().toString();
        currentUser.setUserBiography(newBiography);
    }

    private void saveCheckBoxes() {


    }
}