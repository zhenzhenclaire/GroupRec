package com.claire.util;

/**
 * Created by Claire on 1/11/2016.
 */
public class ItemNode {
    private String name = null;
    private String location = null;
    private int id = 0;

    public ItemNode(String location){
        this.location = location;
    }
    public ItemNode(int id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
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
        ItemNode inode= (ItemNode) st;
        if (this.getId() == inode.getId()) return true;
        else return false;
    }
}
