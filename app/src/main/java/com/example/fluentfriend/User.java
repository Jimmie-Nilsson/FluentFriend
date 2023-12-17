package com.example.fluentfriend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User implements java.io.Serializable {
    private List<String> languagesToLearn = new ArrayList<>();
    private List<String> languagesSpeaks = new ArrayList<>();
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String biography = "";
    private String gender = "";
    private boolean fika;
    private boolean museum;
    private boolean cityWalks;
    private boolean bar;
    private String imageURL;

    public User() {}
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public User getUser(String email){
        if (email.equals(this.email)){
            return this;
        }
        return null;
    }
    public void setLanguagesToLearn(List<String> language) {
        languagesToLearn = language;
    }
    public void setLanguagesSpeak(List<String> language) {
        languagesSpeaks = language;
    }
    public List<String> getLanguagesSpeak() { return languagesSpeaks;}
    public List<String> getLanguagesToLearn() { return languagesToLearn;}
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