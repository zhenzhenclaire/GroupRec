package com.claire.util;

/**
 * Created by Claire on 1/11/2016.
 */


public class UserNode {
    public enum Mode{
        DRIVING("driving"), WALKING("walking"), BICYCLING("bicycling");
        private String value;
        Mode(String value){
            this.value = value;
        }
        public String value(){
            return this.value;
        }
    }

    private Mode travelMode;
    private String name = null;
    private String location = null;
    private int id = 0;

    public UserNode(){
    }
    public UserNode(String location){
        this.location = location;
    }

    public UserNode(String name, String location, int id, Mode travelMode) {
        this.name = name;
        this.location = location;
        this.id = id;
        this.travelMode = travelMode;
    }

    public Mode getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(Mode travelMode) {
        this.travelMode = travelMode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int hashCode(){
        return new Integer(this.getId()).hashCode();
    }

    @Override
    public boolean equals(Object st)
    {
        UserNode unode= (UserNode) st;
        if (this.getId() == unode.getId()) return true;
        else return false;
    }
}
