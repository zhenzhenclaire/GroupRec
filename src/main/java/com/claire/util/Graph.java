package com.claire.util;

import breeze.signal.OptWindowFunction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Claire on 1/11/2016.
 */
public class Graph {
    HashSet<UserNode> userNodes;
    HashSet<ItemNode> itemNodes;
    HashSet<Edge> edges;



    public Graph(){
        userNodes = new HashSet<UserNode>();
        itemNodes = new HashSet<ItemNode>();
        edges = new HashSet<Edge>();
    }
    public Graph(HashSet<UserNode> userNodes){
        userNodes = new HashSet<UserNode>();
        itemNodes = new HashSet<ItemNode>();
        edges = new HashSet<Edge>();
        this.userNodes = userNodes;
    }

//    public Graph(HashSet<UserNode> userNodes, int[][] weights) {
//        userNodes = new HashSet<UserNode>();
//        itemNodes = new HashSet<ItemNode>();
//        this.userNodes = userNodes;
//        this.weights = weights;
//    }

    public HashSet<UserNode> getUserNodes() {
        return userNodes;
    }

    public void setUserNodes(HashSet<UserNode> userNodes) {
        this.userNodes = userNodes;
    }

    public HashSet<ItemNode> getItemNodes() {
        return itemNodes;
    }

    public void setItemNodes(HashSet<ItemNode> itemNodes) {
        this.itemNodes = itemNodes;
    }

    public HashSet<Edge> getEdges() {
        return edges;
    }

    public void setEdges(HashSet<Edge> edges) {
        this.edges = edges;
    }




}
