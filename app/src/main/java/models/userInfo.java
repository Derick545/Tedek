package models;

import android.net.Uri;

import java.util.Date;

public class userInfo {
    //Declaring variables
    private String username;
    private String name;
    private String surname;
    private String phoneNumber;
    private String gender;
    private Uri displayPic;
    private String dateOfBirth;



    public userInfo(String username, String name, String surname, String phoneNumber, String gender, Uri displayPic, String dateOfBirth) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.displayPic = displayPic;
        this.dateOfBirth = dateOfBirth;
    }

    public userInfo() {

    }
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Uri getDisplayPic() {
        return displayPic;
    }

    public void setDisplayPic(Uri displayPic) {
        this.displayPic = displayPic;
    }
}
