package com.claire.util;

import com.claire.util.Person;

import java.util.ArrayList;

/**
 * Created by Claire on 11/26/2015.
 */
public class Group {
    ArrayList<Person> group;
    public String destinationAd;

    public Group(ArrayList<Person> group){
        this.group = group;
    }

    //This method is used for bindGPS info and make a string for Google API.
    public String makeOriginURL(){

        String originsString = "";

        if(group.size() == 0){
            return originsString;
        }
        else{
            originsString = group.get(0).latitude + "," + group.get(0).longitude;

            if(group.size() == 1) {return originsString;}
            else{
                for(int i = 1;i < group.size();i++){
                    Person user = group.get(i);
                    originsString +=  "|" + user.latitude + "," + user.longitude;
                }
                return originsString;
            }
        }

    }

    public String getDestinationAd() {
        return destinationAd;
    }

    public void setDestinationAd(String destinationAd) {
        this.destinationAd = destinationAd;
    }

    public ArrayList<Person> getGroup() {
        return group;
    }

    public void setGroup(ArrayList<Person> group) {
        this.group = group;
    }
}
