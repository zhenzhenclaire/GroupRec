package com.claire.util;

/**
 * Created by Claire on 1/11/2016.
 */
public class Edge {
    UserNode unode;
    ItemNode inode;
    double weight;

    public Edge(UserNode unode, ItemNode inode, double weight) {
        this.unode = unode;
        this.inode = inode;
        this.weight = weight;
    }

    public UserNode getUnode() {
        return unode;
    }

    public void setUnode(UserNode unode) {
        this.unode = unode;
    }

    public ItemNode getInode() {
        return inode;
    }

    public void setInode(ItemNode inode) {
        this.inode = inode;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
