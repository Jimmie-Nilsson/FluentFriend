package com.example.fluentfriend;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FluentFriendFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        // FCM messages here
        Log.d(TAG, "From: " + message.getFrom());
        if (message.getNotification() != null) {
            Log.d(TAG, "Notification Body: " + message.getNotification().getBody());
            //code for showing notification
        }
    }
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        sendRegistrationToServer(token);
        Log.d(TAG, "Refreshed token: " + token);
    }
    private void sendRegistrationToServer(String token) {
        // Send token to the app server
    }
    private void showNotification(String title, String body) {
        // Build and display the notification also create a notification channel
    }

}
