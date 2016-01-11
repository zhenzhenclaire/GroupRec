package com.claire.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Claire on 1/11/2016.
 */
public class Graph {
    private HashSet<UserNode> userNodes;
    private HashSet<ItemNode> itemNodes;
    private HashSet<Edge> edges;

    private int[][] rating;//user item rate

    private String usermapPath = "";
    private String itemmapPath = "";
    private String useritemratePath = "";
    private String hotelLocationPath = "";
    private Map<Integer,String> userMapping = new HashMap<Integer, String>();//
    private Map<Integer,String> itemMapping = new HashMap<Integer, String>();
    private Map<Integer,String> userItems = new HashMap<Integer, String>();

    private Map<String,String> hotelLocation = new HashMap<String, String>();


    public Graph(HashSet<UserNode> userNodes,int[][] rating) {
        this.userNodes = userNodes;
        this.rating = rating;
    }

    /**
     *
     * @param userNode
     * @param itemNode
     * @return
     */
    private double getRate(UserNode userNode,ItemNode itemNode){
        double result = 0;
        int x = itemNode.getId();
        int y = userNode.getId();
        result = rating[x][y];
        return result;
    }

    private void initalize(){
        this.initBaseData();
        for (UserNode node : userNodes){
            String items = userItems.get(node.getId());
            for (String hotel : items.split(",")){
                int id = Integer.parseInt(hotel);
                String name = itemMapping.get(id);
                String location = hotelLocation.get(name);
                ItemNode hotelNode = new ItemNode(id,name,location);
                itemNodes.add(hotelNode);
                Edge edge = new Edge(node,hotelNode,this.getRate(node,hotelNode));
                edges.add(edge);
            }
        }

        for (UserNode unode : userNodes){
            for (ItemNode inode : itemNodes){
                Edge edge = new Edge(unode,inode,this.getRate(unode,inode));
                if (edges.contains(edge)) continue;
                else edges.add(edge);
            }
        }
    }

    private void initBaseData() {
        try {
            //read item mapping
            BufferedReader reader = new BufferedReader(new FileReader(usermapPath));
            String line = null;
            while((line = reader.readLine()) != null){
                String[] temp = line.split("\t");
                if (temp.length != 2)continue;
                userMapping.put(Integer.parseInt(temp[0]),temp[1]);
            }
            reader.close();

            //read user mapping
            reader = new BufferedReader(new FileReader(itemmapPath));
            line = null;
            while((line = reader.readLine()) != null){
                String[] temp = line.split("\t");
                if (temp.length != 2)continue;
                itemMapping.put(Integer.parseInt(temp[0]),temp[1]);
            }
            reader.close();

            //read user's all items
            reader = new BufferedReader(new FileReader(useritemratePath));
            line = null;
            while((line = reader.readLine()) != null){
                String[] temp = line.split(",");
                if (temp.length != 3)continue;
                if (userItems.get(Integer.parseInt(temp[1])) == null) {
                    userItems.put(Integer.parseInt(temp[1]), temp[0]);
                }else{
                    userItems.put(Integer.parseInt(temp[1]),userItems.get(Integer.parseInt(temp[1])) + "," + temp[0]);
                }
            }
            reader.close();

            //read item location
            reader = new BufferedReader(new FileReader(hotelLocationPath));
            line = null;
            while((line = reader.readLine()) != null){
                String[] temp = line.split(",");
                hotelLocation.put(temp[0],temp[5] + "," + temp[6]);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
