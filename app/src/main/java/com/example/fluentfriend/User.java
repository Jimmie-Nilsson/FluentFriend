package com.example.fluentfriend;

import java.util.ArrayList;

public class User implements java.io.Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String biography;
    private String gender;
    private boolean fika;
    private boolean museum;
    private boolean cityWalks;
    private boolean bar;

    public User() {}
    public User(String firstName, String lastName, String email, String password){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;

    }
    public User(String firstName, String lastName, String email, String password, String bio, boolean fika, boolean museum, boolean cityWalks, boolean bar, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.biography = bio;
        this.fika = fika;
        this.museum = museum;
        this.cityWalks = cityWalks;
        this.bar = bar;
        this.gender = gender;
    }
    public void setUserBiography(String newBiography){biography = newBiography;}
    public void setFirstName(String inputFirstName){firstName = inputFirstName;}
    public void setLastName(String inputLastName){lastName = inputLastName;}
    public void setEmail(String inputEmail){email = inputEmail;}
    public void setPassword(String inputPassword){password = inputPassword;}
    public void setGender(String gender){this.gender = gender;}
    public String getPassword() {return this.password;}
    public String getFirstName() {return this.firstName;}
    public String getLastName(){return this.lastName;}
    public String getEmail(){return this.email;}
    public String getGender(){return this.gender;}
    public String getBiography() {return this.biography;}
    public void setFikaChecked(boolean checkBoxChecked){fika = checkBoxChecked;}
    public void setMuseumChecked(boolean checkBoxChecked){museum = checkBoxChecked;}
    public void setCityWalksChecked(boolean checkBoxChecked){cityWalks = checkBoxChecked;}
    public void setBarChecked(boolean checkBoxChecked){
        bar = checkBoxChecked;
    }
    public boolean isFikaChecked() {return this.fika;}
    public boolean isMuseumChecked() {return this.museum;}
    public boolean isBarChecked() {return this.bar;}
    public boolean isCityWalksChecked() {return this.cityWalks;}
}