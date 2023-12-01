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

    public User(String firstName, String lastName, String eMail, String password){
        this.firstName = firstName;
        this.lastName = lastName;
        this.eMail = eMail;
        this.password = password;
    }
    protected void setUserInterests ( boolean fika, boolean museum, boolean cityWalks, boolean barChill){
        setFika(fika);
        setMuseum(museum);
        setCityWalks(cityWalks);
        setBarChill(barChill);
    }

    protected void setUserBio(String inputUserBio){
            bio = inputUserBio;
        }
    protected void setFirstName(String inputFirstName){
            firstName = inputFirstName;
        }

    protected void setLastName(String inputLastName){
            lastName = inputLastName;
        }

    protected void setEmail(String inputEmail){
            eMail = inputEmail;
        }

    protected void setPassword(String inputPassword){
            password = inputPassword;
        }

    protected void setFika(boolean inputFika){
            fika = inputFika;
        }
    protected void setMuseum(boolean inputMuseum){
            museum = inputMuseum;
        }

    protected void setCityWalks(boolean inputCityWalks){
            cityWalks = inputCityWalks;
        }

    protected void setBarChill(boolean inputBarChill){
            barChill = inputBarChill;
        }
}