package com.example.fluentfriend;

// singleton pattern so that the user is accessible across the entire application
public class UserManager {
    private static User currentUser; //få denna att settas vid user registration / när man loggar in
    public static User getCurrentUser() {
        return currentUser;
    }

}
