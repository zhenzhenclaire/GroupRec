package com.claire.util;

/**
 * Created by Claire on 11/26/2015.
 */
public class Person {
    String latitude = "";
    String longitude = "";
    String userID = "";
    UserNode.Mode travelMode;

    public Person(String userID, String lat, String lon,UserNode.Mode mode){
        this.userID = userID;
        this.latitude = lat;
        this.longitude = lon;
        this.travelMode= mode;
    }

    public UserNode.Mode getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(UserNode.Mode travelMode) {
        this.travelMode = travelMode;
    }

    public Person(String userID, String lat, String lon){
        this.userID = userID;
        this.latitude = lat;
        this.longitude = lon;
        this.travelMode = UserNode.Mode.DRIVING;
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
