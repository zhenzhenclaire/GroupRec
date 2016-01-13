package com.claire.util;

/**
 * Created by Claire on 1/11/2016.
 */
public class UserNode {

    private String name = null;
    private String location = null;
    private int id = 0;

    public UserNode(String name, String location, int id) {
        this.name = name;
        this.location = location;
        this.id = id;
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
}
