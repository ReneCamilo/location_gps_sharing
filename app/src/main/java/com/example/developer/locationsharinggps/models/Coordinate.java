package com.example.developer.locationsharinggps.models;

/**
 * Created by developer on 8/1/17.
 */

public class Coordinate {
    private String latitude;
    private String longitude;
    private String phoneNumber;

    public Coordinate(String mLatitude, String mLongitude, String mPhoneNumber) {
        this.latitude = mLatitude;
        this.longitude = mLongitude;
        this.phoneNumber = mPhoneNumber;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
