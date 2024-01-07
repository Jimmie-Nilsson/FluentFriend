package com.example.fluentfriend;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.*;

public class MatchPage extends AppCompatActivity {
    private static ArrayList<UserLocation> activeUsers = new ArrayList<>();
    private TreeMap<Double, ArrayList<String>> distanceList = new TreeMap<>();
    private List<User> users = new ArrayList<>();
    private TreeMap<Double, User> similarityScore = new TreeMap<>(Comparator.reverseOrder());
    private HashMap<User, String> userInfo = new HashMap<>();
    private Button btnAccept;
    private Button btnDecline;
    private Button btnReturn;
    private TextView textProfile;
    private TextView textBoxHeader;
    private TextView textName;
    private ScrollView textScroll;
    private CircleImageView profilePicture;
    private User currentUser;
    private UserLocation currentUserLoc;
    private FirebaseDatabase db = FirebaseDatabase.getInstance("https://fluent-friend-dad39-default-rtdb.firebaseio.com/");
    private DatabaseReference activeUsersRef = db.getReference().child("activeusers");
    private DatabaseReference usersRef = db.getReference().child("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_page);
        textBoxHeader = findViewById(R.id.matchPageTextTop);
        textProfile = findViewById(R.id.matchPageTextShowProfile);
        btnAccept = findViewById(R.id.matchmatchPageBtnAccept);
        btnDecline = findViewById(R.id.matchmatchPageBtnDecline);
        btnReturn = findViewById(R.id.matchPageReturnBack);
        textName = findViewById(R.id.matchPageNameText);
        profilePicture = findViewById(R.id.matchPageImageView);
        textScroll = findViewById(R.id.scrollView5);

        // loads the current user
        currentUser = UserManager.getCurrentUser();

        // Set return button invisble.
        btnReturn.setClickable(false);
        btnReturn.setVisibility(View.INVISIBLE);

        // Fetch and update the list over active user. Also starts the Matchingalgorithm.
        fetchUsersAndCollectInList();
        fetchActiveUsersAndCollectInList();

        btnAccept.setOnClickListener(view -> {
            // Open new frame where we show cafe suggestions etc,
            Intent i = new Intent(this, LocationSuggestion.class);
            i.putExtra("matchedUser", similarityScore.get(similarityScore.firstKey()));
            startActivity(i);
        });

        btnDecline.setOnClickListener(view -> {
            similarityScore.remove(similarityScore.firstKey());

            if (similarityScore.isEmpty()) {
                setFrameForNoMatches("No more matches :(", "Comeback later and test your luck.\n\nHave a nice day!");
            } else {
                showUser();
            }
        });

        btnReturn.setOnClickListener(view -> {
            finish(); // Return back to HomePage
        });
    }

    private void showUser() {
        textScroll.scrollTo(0, 0);
        double d = similarityScore.firstKey();
        User u = similarityScore.get(d);
        String s = userInfo.get(u);

        String imageURL = u.getImageURL();
        Picasso.get().load(imageURL).placeholder(R.drawable.default_profile_picture).error(R.drawable.default_profile_picture).into(profilePicture);

        textName.setText(u.getFirstName() + " " + u.getLastName());
        textBoxHeader.setText("Match!!!");
        textProfile.setText(s);
    }

    protected void addUserActive(UserLocation userLocation) {
        activeUsersRef.child(userLocation.getEmail()).setValue(userLocation);
        activeUsers.add(userLocation);
        currentUserLoc = userLocation;
    }

    private void fetchUsersAndCollectInList() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    User user = userSnapshot.getValue(User.class);
                    if (userId != null) {
                        users.add(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Log.d("Database", "ERROR FROM DATABASE: " + databaseError.toString()); // logging for debugging
            }
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
                    }
                }

                textProfile.setText(activeUsers.size() + " users");

                for (UserLocation l : activeUsers) {
                    if (l.getEmail().equals(UserManager.getCurrentUser().getEmail())) {
                        addUserActive(l);
                        currentUserLoc = l;
                        break;
                    }
                }
                currentUserLoc = getActiveUser(UserManager.getCurrentUser());
                calcDistanceBetweenUsers();
                matchingalgorithm();

                // If don't find any matches
                if (similarityScore.isEmpty()) {
                    setFrameForNoMatches("No match found :(", "Currently no other users nearby you. Please try again later!");
                } else {
                    showUser();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Log.d("Database", "ERROR FROM DATABASE: " + databaseError.toString()); // logging for debugging
            }
        });
    }

    protected static UserLocation getActiveUser(User user) {
        for (int i = 0; i < activeUsers.size(); i++) {
            if (activeUsers.get(i).getEmail().equals(user.getEmail())) {
                return activeUsers.get(i);
            }
        }
        return null; // If the user is not active. We return null.
    }

    protected void removeUserActive(User user) {
        activeUsersRef.child(user.getEmail()).removeValue();

        for (int i = 0; i < activeUsers.size(); i++) {
            if (activeUsers.get(i).getEmail().equals(user.getEmail())) {
                activeUsers.remove(i);
                break;
            }
        }
    }

    private void calcDistanceBetweenUsers() {
        // Calculate the distance between current user and all the other active users.
        for (int i = 0; i < activeUsers.size(); i++) {
            if (!activeUsers.get(i).getEmail().equals(currentUser.getEmail())) {
                double distance = currentUserLoc.calcDistanceBetweenUsers(activeUsers.get(i).getLatitude(), activeUsers.get(i).getLongitude());
                if (!distanceList.containsKey(distance)) {
                    distanceList.put(distance, new ArrayList<>());
                }
                distanceList.get(distance).add(activeUsers.get(i).getEmail());
            }
        }
    }

    private void matchingalgorithm() {
        // Define how far out users can match
        final double maxDistance = 2000; // Meters

        // Define weights (summing to 1.0)
        final double weightBio = 0.0;
        final double weightLanguage = 0.7;
        final double weightCheckbox = 0.3;

        // The number we add if we found a similarity
        final double numberLanguage = 2;
        final double numberCheckBox = 1;

        // Text to show user if no user is near or no match found.
        String textHeader = "No match found";
        String textProfile = "Currently no other users nearby you. Please try again later!";

        // If no user is nearby.
        if (distanceList.isEmpty() || distanceList.firstKey() > maxDistance) {
            setFrameForNoMatches(textHeader, textProfile);
            return;
        }

        for (Double d : distanceList.keySet()) {

            // Check if the other user is to far away
            if (d > maxDistance) {
                break;
            }

            for (String s : distanceList.get(d)) {
                User otherUser = MainActivity.getUser(s);
                StringBuilder sb = new StringBuilder();

                // Similarity score for each category
                double bioSimilarity;
                double languageSimilarity = 0;
                double checkboxSimilarity = 0;
                double combinedSimilarityScore;

                // Checks during the algorithm
                boolean checkOne = false;
                boolean checkTwo = false;
                boolean checkThree = false;

                // Check for similarity in languages. Get users data for language
                List<String> otherUserSpeaks = otherUser.getLanguagesSpeak();
                List<String> otherUserWantsToLearn = otherUser.getLanguagesToLearn();
                List<String> currentUserSpeaks = currentUser.getLanguagesSpeak();
                List<String> currentUserWantsToLearn = currentUser.getLanguagesToLearn();

                // Check if otheruser speaks any language that current user wants to learn.
                for (int i = 0; i < otherUserSpeaks.size(); i++) {

                    if (currentUserWantsToLearn.contains(otherUserSpeaks.get(i))) {
                        sb.append(otherUser.getFirstName());
                        sb.append(" speaks " + otherUserSpeaks.get(i) + " and it's on your list of language to learn!\n\n");
                        languageSimilarity += numberLanguage;
                        checkOne = true;
                    }
                }

                // Check if otheruser wants to learn any of the language currentUser speak.
                for (int k = 0; k < otherUserWantsToLearn.size(); k++) {
                    if (currentUserSpeaks.contains(otherUserWantsToLearn.get(k))) {
                        sb.append(otherUser.getFirstName());
                        sb.append(" wants to learn " + otherUserWantsToLearn.get(k) + " and that's in your speaking list!\n\n");
                        languageSimilarity += numberLanguage;
                        checkTwo = true;
                    }
                }

                // If we didn't find any common language for the currentuser and the otheruser, we break and test with someone else
                if (checkOne == false || checkTwo == false) {
                    break;
                }

                // Check for common checkbox interest.
                if (currentUser.isFikaChecked() && otherUser.isFikaChecked()) {
                    sb.append("Fika is a common interest!\n\\n");
                    checkboxSimilarity += numberCheckBox;
                    checkThree = true;
                }
                if (currentUser.isBarChecked() && otherUser.isBarChecked()) {
                    sb.append("Bar is a common interest!\n\n");
                    checkboxSimilarity += numberCheckBox;
                    checkThree = true;
                }
                if (currentUser.isCityWalksChecked() && otherUser.isCityWalksChecked()) {
                    sb.append("City walks is a common interest!\n\n");
                    checkboxSimilarity += numberCheckBox;
                    checkThree = true;
                }
                if (currentUser.isMuseumChecked() && otherUser.isMuseumChecked()) {
                    sb.append("Museum is a common interest!\n\n");
                    checkboxSimilarity += numberCheckBox;
                    checkThree = true;
                }

                // if they don't have any common interest
                if (!checkThree) {
                    sb.append("No common interest...");
                }

                // For the future, maybe add a method for biosimilarity

                // Calculate the similarity score with weights adjusted
                combinedSimilarityScore = (checkboxSimilarity * weightCheckbox) + (languageSimilarity * weightLanguage);

                // Save info
                similarityScore.put(combinedSimilarityScore, otherUser);
                userInfo.put(otherUser, sb.toString());


            } // End s loop
        } // End d loop

    } // End of matchingalgorithm

    private void setFrameForNoMatches(String header, String txtProfile) {
        textName.setText(null);
        textBoxHeader.setText(header);
        textProfile.setText(txtProfile);
        btnAccept.setClickable(false);
        btnAccept.setVisibility(View.INVISIBLE);
        btnDecline.setClickable(false);
        btnDecline.setVisibility(View.INVISIBLE);
        btnReturn.setVisibility(View.VISIBLE);
        btnReturn.setClickable(true);
    }
}

