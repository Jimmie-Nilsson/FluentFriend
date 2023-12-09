package com.example.fluentfriend;

public class UserLocation {
    private User user;
    private double longitude;
    private double latitude;

    public UserLocation(){

    }
    public UserLocation(User user, double latitude, double longitude) {
        this.user = user;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public User getUser() {return user;}

    public double getLongitude() {return longitude;}

    public double getLatitude() {return latitude;}
    public double calcDistanceBetweenUsers( double lat2, double lon2) {
        final double EARTH_RADIUS = 6371000; // meters

        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(this.latitude);
        double lon1Rad = Math.toRadians(this.longitude);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Calculate differences
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Haversine formula
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate distance
        return EARTH_RADIUS * c;
    }
}

/* Haversine formula. The Haversine formula calculates the shortest distance over the earth's surface,
giving an "as-the-crow-flies" distance between the points
(ignoring any hills, valleys, or other potential obstacles).
 */

