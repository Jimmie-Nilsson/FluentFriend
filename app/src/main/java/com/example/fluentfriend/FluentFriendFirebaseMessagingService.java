package com.example.fluentfriend;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;


//PLACEHOLDER CODE, NOT IMPLEMENTED, FOR SCALABILITY
public class FluentFriendFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Log the message source for debugging
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null) {
            String messageBody = remoteMessage.getNotification().getBody();
            String messageTitle = remoteMessage.getNotification().getTitle();

            //Log notification content for debugging
            Log.d(TAG, "Notification Title: " + messageTitle);
            Log.d(TAG, "Notification Body: " + messageBody);

            //Display notification
            showNotification(messageTitle, messageBody);
        }

        // Check if the message contains data payload and handle it
        if (remoteMessage.getData().size() > 0) {
            // Handle data payload
            // For example, you might want to log the data
            Log.d(TAG, "Data Payload: " + remoteMessage.getData().toString());
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        sendRegistrationToServer(token);
        Log.d(TAG, "Refreshed token: " + token);
    }

    private void sendRegistrationToServer(final String token) {
        // Create a new thread for network operations
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Here we just use a placeholder URL for demonstration purposes
                String serverUrl = "https://yourserver.com/registerToken";
                HttpURLConnection urlConnection = null;

                try {
                    URL url = new URL(serverUrl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);

                    // Set the content type as JSON, assuming that's what your server expects
                    urlConnection.setRequestProperty("Content-Type", "application/json");

                    // Create the data JSON with the token
                    JSONObject tokenJson = new JSONObject();
                    tokenJson.put("token", token);

                    // Write the token JSON data to the output stream
                    OutputStream os = urlConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(tokenJson.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    // Check the server response code to see if the token was sent successfully
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // The token was sent successfully
                        Log.d(TAG, "Token sent successfully to server");
                    } else {
                        // The server responded with an error
                        Log.d(TAG, "Server responded with HTTP status code: " + responseCode);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error sending token to server", e);
                    // Handle the exception (e.g., retry logic)
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        }).start();
    }

    private boolean sendTokenToServer(String token) {
        // Implementation of your network call to send the token to your server
        // This should handle all network operations and return true if the operation was successful
        return false; // Replace with actual network call logic
    }

    private void saveTokenLocally(String token) {
        // Save the token to shared preferences or a local database
    }

    private boolean isNetworkAvailable() {
        // Implement network check logic
        return false; // Replace with actual network check logic
    }

    private void markTokenAsRegistered(String token) {
        // Update your local storage to mark the token as registered
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "your_channel_id";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("FCM Notifications");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher) // set your app icon here
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(/*notification id*/ new Random().nextInt(), notificationBuilder.build());
    }

}


