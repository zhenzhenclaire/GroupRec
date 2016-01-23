package com.claire.util;

/**
 * Created by Claire on 1/23/2016.
 */
public class ItemBridge {
    UserNode uFrom;
    UserNode uTo;
    ItemNode iNode;
    Double sumWeight;

    public ItemBridge(UserNode uFrom, UserNode uTo){
        this.uFrom = uFrom;
        this.uTo = uTo;
    }

    public ItemBridge(ItemNode iNode, Double sumWeight) {
        this.iNode = iNode;
        this.sumWeight = sumWeight;
    }

    public UserNode getuFrom() {
        return uFrom;
    }

    public void setuFrom(UserNode uFrom) {
        this.uFrom = uFrom;
    }

    public UserNode getuTo() {
        return uTo;
    }

    public void setuTo(UserNode uTo) {
        this.uTo = uTo;
    }

    public ItemNode getiNode() {
        return iNode;
    }

    public void setiNode(ItemNode iNode) {
        this.iNode = iNode;
    }

    public Double getSumWeight() {
        return sumWeight;
    }

    public void setSumWeight(Double sumWeight) {
        this.sumWeight = sumWeight;
    }

    public int hashCode(){
        return (this.uFrom.getGraphId() + ":" + this.uTo.getGraphId()).hashCode();
    }

    @Override
    public boolean equals(Object st)
    {
        ItemBridge ibridge= (ItemBridge) st;
        if (this.getuFrom().getGraphId() == ibridge.uFrom.getGraphId() && this.getuTo().getGraphId() == ibridge.uTo.getGraphId()) return true;
        else return false;
    }
}
