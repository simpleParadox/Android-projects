package com.abomicode.welp_safetyandsecurity;

/**
 * Created by Rohan on 6/15/2017.
 */

public class UserDetails {

    String fullName;
    String email;
    String password;
    String phone;
    double latitude;
    double longitude;


    public UserDetails(){
        //Default constructor for dataSnapshot.getValue(UserDetails.class)
    }


    public UserDetails(String fullName, String email, String password, String phone) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;

        latitude = 0;
        longitude = 0;
    }
    public String getFullName(){
        return fullName;
    }
    public String getEmail(){
        return email;
    }
    public String getPassword(){
        return password;
    }
    public String getPhone(){
        return phone;
    }
    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }
}
