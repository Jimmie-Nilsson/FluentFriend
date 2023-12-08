package com.example.fluentfriend;

public class User implements java.io.Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String biography;
    private boolean fika;
    private boolean museum;
    private boolean cityWalks;
    private boolean bar;

    public User(String firstName, String lastName, String email, String password){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
    protected void setUserBiography(String newBiography){
            biography = newBiography;
        }
    protected void setFirstName(String inputFirstName){
            firstName = inputFirstName;
        }
    protected void setLastName(String inputLastName){
            lastName = inputLastName;
        }
    protected void setEmail(String inputEmail){
            email = inputEmail;
        }
    protected void setPassword(String inputPassword){
            password = inputPassword;
        }
    public String getPassword() {return this.password;}
    public String getFirstName() {return this.firstName;}
    public String getLastName(){return this.lastName;}
    public String getEmail(){return this.email;}
    public String getBiography() {
        return this.biography;
    }

    protected void setFikaChecked(boolean checkBoxChecked){
        fika = checkBoxChecked;
    }
    protected void setMuseumChecked(boolean checkBoxChecked){
        museum = checkBoxChecked;
    }
    protected void setCityWalksChecked(boolean checkBoxChecked){
        cityWalks = checkBoxChecked;
    }
    protected void setBarChecked(boolean checkBoxChecked){
        bar = checkBoxChecked;
    }

    public boolean isFikaChecked() {return this.fika;}
    public boolean isMuseumChecked() {return this.museum;}
    public boolean isBarChecked() {return this.bar;}
    public boolean isCityWalksChecked() {return this.cityWalks;}

}