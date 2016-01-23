package com.claire.util;

/**
 * Created by Claire on 1/11/2016.
 */
public class Edge {
    UserNode unode;
    ItemNode inode;
    double weight;
    double duration;
    double normalizedWeight;
    double normalizedDuration;
    double combinedWeight;

    public Edge(UserNode unode, ItemNode inode) {
        this.unode = unode;
        this.inode = inode;
    }

    public Edge(UserNode unode, ItemNode inode, double weight) {
        this.unode = unode;
        this.inode = inode;
        this.weight = weight;
    }

    public double getNormalizedDuration() {
        return normalizedDuration;
    }

    public void setNormalizedDuration(double normalizedDuration) {
        this.normalizedDuration = normalizedDuration;
    }

    public double getNormalizedWeight() {
        return normalizedWeight;
    }

    public void setNormalizedWeight(double normalizedWeight) {
        this.normalizedWeight = normalizedWeight;
    }

    public double getCombinedWeight() {
        return combinedWeight;
    }

    public void setCombinedWeight(double combinedWeight) {
        this.combinedWeight = combinedWeight;
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

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public int hashCode(){
        return (unode.getId() + ":" + inode.getId()).hashCode();
    }

    @Override
    public boolean equals(Object st)
    {
        Edge edge= (Edge) st;
        if (unode.getId() == edge.getUnode().getId() && inode.getId() == edge.getInode().getId()) return true;
        else return false;
    }
}
