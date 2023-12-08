package com.example.fluentfriend;

import android.content.DialogInterface;
import android.text.InputType;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private Spinner languagesToLearnSpinner;
    private Spinner languagesISpeakSPinner;
    private boolean[] checkedItems;

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
        buttonSave = findViewById(R.id.saveSettingsButton); //vad gör denna /G
        loadBiography();

        //finds spinner view
        languagesToLearnSpinner = findViewById(R.id.languagesToLearnSpinner);
        //creates arrayadapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, LanguageManager.AVAILABLE_LANGUAGES);
        //specifies layout to use when list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //apply adapter to spinner
        languagesToLearnSpinner.setAdapter(adapter);
        //listener for when an item is selected
        languagesToLearnSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               //get select item
               String selectedLanguage = (String) parent.getItemAtPosition(position);
               //add code to handle select item /G
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

    private void showLanguageList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose languages");

        //create array with as many slots as available languages
        checkedItems = new boolean[LanguageManager.AVAILABLE_LANGUAGES.size()];
        Arrays.fill(checkedItems, false);

        builder.setMultiChoiceItems(LanguageManager.AVAILABLE_LANGUAGES.toArray(new String[0]), checkedItems,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        //updates the checkedItems array
                        checkedItems[which] = isChecked;
                    }
                });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleLanguageSelection();
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void handleLanguageSelection() {
        List<String> selectedLanguages = new ArrayList<>();
        for (int i = 0; i< checkedItems.length; i++) {
            if (checkedItems[i]) {
                //if the language is selected, add it to the list
                selectedLanguages.add(LanguageManager.AVAILABLE_LANGUAGES.get(i));
            }
        }

        saveSelectedLanguages(selectedLanguages);
    }

    private void saveSelectedLanguages(List<String> languages) {
        //save to database
        //save to user profile
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