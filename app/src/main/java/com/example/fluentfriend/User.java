package com.example.fluentfriend;

public class User {
    private String firstName;
    private String lastName;
    private String eMail;
    private String password;
    private String bio;
    private boolean fika;
    private boolean museum;
    private boolean cityWalks;
    private boolean barChill;

    public void registerNewUser(String firstname, String lastName, String eMail, String password) {
        setFirstName(firstname);
        setLastName(lastName);
        setEmail(eMail);
        setPassword(password);
    }

    public void setUserInterests(boolean fika, boolean museum, boolean cityWalks, boolean barChill){
        setFika(fika);
        setMuseum(museum);
        setCityWalks(cityWalks);
        setBarChill(barChill);
    }

    public void setUserBio(String inputUserBio){
        bio = inputUserBio;
    }
    public void setFirstName(String inputFirstName) {
        firstName = inputFirstName;
    }

    public void setLastName(String inputLastName) {
        lastName = inputLastName;
    }

    public void setEmail(String inputEmail) {
        eMail = inputEmail;
    }

    public void setPassword(String inputPassword){
        password = inputPassword;
    }

    public void setFika(boolean inputFika){
        fika = inputFika;
    }
    public void setMuseum(boolean inputMuseum){
        museum = inputMuseum;
    }

    public void setCityWalks(boolean inputCityWalks) {
        cityWalks = inputCityWalks;
    }

    public void setBarChill(boolean inputBarChill) {
        barChill = inputBarChill;
    }




}


