package com.claire.gmst;

import com.claire.util.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by claire on 1/11/16.
 */
public class groupMake {
    static Logger logger = Logger.getLogger("groupMake");
    Group group;
    Graph g;
    int[][] weights;

    private String userReflectionTable = "";
    private String itemReflectionTable = "";
    private String userItemRatingPath = "";
    private String hotelLocationPath = "";
    private Map<Integer,String> userMapping = new HashMap<Integer, String>();//
    private Map<Integer,String> itemMapping = new HashMap<Integer, String>();
    private Map<Integer,String> userItems = new HashMap<Integer, String>();

    private Map<String,String> hotelLocation = new HashMap<String, String>();


    public groupMake(Group group, String userReflectionTable, String itemReflectionTable, String userItemRatingPath, String hotelLocationPath) {
        this.group = group;
        this.userReflectionTable = userReflectionTable;
        this.itemReflectionTable = itemReflectionTable;
        this.userItemRatingPath = userItemRatingPath;
        this.hotelLocationPath = hotelLocationPath;
        g = new Graph();
    }

    private void initBaseData() {
        logger.info("Start initing base data.");
        try {
            logger.info("Start making user mapping. Reading from" + userReflectionTable);
            BufferedReader reader = new BufferedReader(new FileReader(userReflectionTable));

            String line = null;
            while((line = reader.readLine()) != null){
                String[] temp = line.split(" ");
                if (temp.length != 2) continue;
                else{
                    userMapping.put(Integer.parseInt(temp[0]),temp[1]);
                }

            }
            reader.close();
            logger.info("Finish making user mapping.");

            logger.info("Start making hotel mapping.");
            //read user mapping
            reader = new BufferedReader(new FileReader(itemReflectionTable));
            line = null;
            while((line = reader.readLine()) != null){
                String[] temp = line.split(" ");
                if (temp.length != 2)continue;
                itemMapping.put(Integer.parseInt(temp[0]),temp[1]);
            }
            reader.close();
            logger.info("Finish making hotel mapping.");

            weights = new int[userMapping.size()][itemMapping.size()];

            logger.info("Start reading rating of user and hotel.");
            reader = new BufferedReader(new FileReader(userItemRatingPath));
            line = null;
            while((line = reader.readLine()) != null){
                String[] temp = line.split(",");
                if (temp.length != 3)continue;

                int userId = Integer.parseInt(temp[1]);
                int hotelId = Integer.parseInt(temp[0]);
                int rating = Integer.parseInt(temp[2]);

                weights[userId][hotelId] = rating;

                if (userItems.get(userId) == null) {
                    userItems.put(userId, hotelId + "");
                }else{
                    userItems.put(userId,userItems.get(userId) + "," + hotelId);
                }
            }
            reader.close();
            logger.info("Finished reading rating of user and hotel.");

            //read item location
            logger.info("Start reading location of hotel.");
            reader = new BufferedReader(new FileReader(hotelLocationPath));
            line = null;
            while((line = reader.readLine()) != null){
                String[] temp = line.split(",");
                hotelLocation.put(temp[0],temp[5] + "," + temp[6]);
            }
            reader.close();
            logger.info("Finished reading location of hotel.");

            HashSet<UserNode> userNodeSet = new HashSet<UserNode>();
            for(Person p : group.getGroup()){
                int id = Integer.parseInt(p.getUserID());
                String name = userMapping.get(id);
                String location = p.getLatitude() + "," + p.getLongitude();

                UserNode unode = new UserNode(name,location,id);
                logger.info("user " + unode.getId() + ",location" + unode.getLocation() + ",name" + unode.getName());
                userNodeSet.add(unode);
                logger.info(userNodeSet.size() + "");
            }
            g.setUserNodes(userNodeSet);

        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("Finished init base data.");
    }


    /**
     * Get weight of userNode and itemNode
     * @param userNode
     * @param itemNode
     * @return
     */
    public double getWeights(UserNode userNode,ItemNode itemNode){
        double result = 0;
        int x = userNode.getId();
        int y = itemNode.getId();
        result = weights[x][y];
        return result;
    }

    public boolean isUserItemConnected(UserNode userNode, ItemNode itemNode){
        if(weights[itemNode.getId()][userNode.getId()] != 0)
            return true;
        else
            return false;
    }


    public void makeGraph(){
        logger.info("Start making graph.");
        this.initBaseData();

        for (UserNode node : g.getUserNodes()){
            String items = userItems.get(node.getId());
            logger.info(items);


            for (String hotel : items.split(",")){
                int id = Integer.parseInt(hotel);
                String name = itemMapping.get(id);
                String location = hotelLocation.get(name);
                ItemNode hotelNode = new ItemNode(id,name,location);
                g.getItemNodes().add(hotelNode);
                Edge edge = new Edge(node,hotelNode,getWeights(node,hotelNode));
                g.getEdges().add(edge);
            }
        }

        Set<Edge> edges = g.getEdges();
        for (Edge edge : edges){
            System.out.println(edge.getUnode().getId() + "---->" + edge.getInode().getId() + ":" + edge.getWeight());
        }

        System.out.println("----------------------------------------");
        // Add weight(rating part) to edges
        for (UserNode unode : g.getUserNodes()){
            //logger.info("user " + unode.getId() + ",location" + unode.getLocation() + ",name" + unode.getName());

            for (ItemNode inode : g.getItemNodes()){
                //logger.info("item " + inode.getId() + ",loc" + inode.getLocation() + ",name" + inode.getName());

                Edge edge = new Edge(unode,inode,this.getWeights(unode,inode));
                //logger.info("user " + unode.getId() + ",item" + inode.getId() + ",weight" + getWeights(unode,inode));

                if (g.getEdges().contains(edge)) continue;
                else g.getEdges().add(edge);
            }
        }
        logger.info("Finished build original graph.");

        edges = g.getEdges();
        for (Edge edge : edges){
            System.out.println(edge.getUnode().getId() + "---->" + edge.getInode().getId() + ":" + edge.getWeight());
        }
    }
}
