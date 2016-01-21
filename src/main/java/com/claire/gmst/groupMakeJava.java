package com.claire.gmst;

import com.claire.util.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by claire on 1/11/16.
 */
public class groupMakeJava {
    static Logger logger = Logger.getLogger("groupMakeJava");
    Group group;
    Graph g;
    double[][] weights;

    private String userReflectionTable = "";
    private String itemReflectionTable = "";
    private String userItemRatingPath = "";
    private String hotelLocationPath = "";
    private Map<Integer,String> userMapping = new HashMap<Integer, String>();//
    private Map<Integer,String> itemMapping = new HashMap<Integer, String>();
    private Map<Integer,String> userItems = new HashMap<Integer, String>();

    private Map<String,String> hotelLocation = new HashMap<String, String>();


    public groupMakeJava(Group group, String userReflectionTable, String itemReflectionTable, String userItemRatingPath, String hotelLocationPath) {
        this.group = group;
        this.userReflectionTable = userReflectionTable;
        this.itemReflectionTable = itemReflectionTable;
        this.userItemRatingPath = userItemRatingPath;
        this.hotelLocationPath = hotelLocationPath;
        g = new Graph();
    }

    /**
     * Make user mapping and hotel mapping and location mapping
     */
    private  void makeMapping(){
        //Make user mapping
        BufferedReader reader = null;
        String line = null;
        logger.info("Start making user mapping. Reading from" + userReflectionTable);
        try {

            reader = new BufferedReader(new FileReader(userReflectionTable));

            while((line = reader.readLine()) != null){
                String[] temp = line.split(" ");
                if (temp.length != 2) continue;
                else{
                    userMapping.put(Integer.parseInt(temp[0]),temp[1]);
                }
            }
            reader.close();
            logger.info("Finish making user mapping.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        logger.info("Start making hotel mapping.");
        //read user mapping
        try {
            reader = new BufferedReader(new FileReader(itemReflectionTable));
            line = null;
            while((line = reader.readLine()) != null){
                String[] temp = line.split(" ");
                if (temp.length != 2)continue;
                itemMapping.put(Integer.parseInt(temp[0]),temp[1]);
            }
            reader.close();
            logger.info("Finish making hotel mapping.");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //read item location
        logger.info("Start reading location of hotel.");
        File file = new File(hotelLocationPath);
        File[] files = file.listFiles();
        for(File f:files){
            try {
                reader = new BufferedReader(new FileReader(f));
                line = null;
                while((line = reader.readLine()) != null){
                    String[] temp = line.split(",");
                    hotelLocation.put(temp[0],temp[5] + "," + temp[6]);
                }
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("Finished reading location of hotel.");

    }

    private void initializeWeightsByRating(){
        BufferedReader reader = null;
        String line = null;

        logger.info("Start reading rating of user and hotel.");
        try {
            reader = new BufferedReader(new FileReader(userItemRatingPath));

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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Finished reading rating of user and hotel.");
    }



    private void initBaseData() {
        logger.info("Start initing base data.");
        try {
            makeMapping();

            weights = new double[userMapping.size()][itemMapping.size()];

            initializeWeightsByRating();

            missingValueSupplement();

            // Create user nodes and add them to graph
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

    /**
     * Make original graph
     */
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


    /**
     * Implement missing value from co-clustering
     */
    public void missingValueSupplement(){
        logger.info("Start calculating missing value.");
        final Map<Integer,Integer> userClass = new HashMap<Integer, Integer>();
        final Map<Integer,Integer> itemClass = new HashMap<Integer, Integer>();

        final Map<Integer,HashSet<Integer>> classUsers = new HashMap<Integer, HashSet<Integer>>();
        final Map<Integer,HashSet<Integer>> classItems = new HashMap<Integer, HashSet<Integer>>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(Config.clusteringResult));
            String line = "";
            while((line = reader.readLine()) != null){
                String[] tmp = line.split(":");
                if (tmp.length != 3) continue;
                int id = Integer.parseInt(tmp[1]);
                int classfi = Integer.parseInt(tmp[2]);
                if (tmp[0].equals("hotel")){
                    itemClass.put(id,classfi);
                    if (classItems.get(classfi) == null){
                        HashSet<Integer> items = new HashSet<Integer>();
                        items.add(id);
                        classItems.put(classfi,items);
                    }else{
                        HashSet<Integer> items = classItems.get(classfi);
                        items.add(id);
                        classItems.put(classfi,items);
                    }
                }else if (tmp[0].equals("user")){
                    userClass.put(id,classfi);
                    if (classUsers.get(classfi) == null){
                        HashSet<Integer> items = new HashSet<Integer>();
                        items.add(id);
                        classUsers.put(classfi,items);
                    }else{
                        HashSet<Integer> items = classUsers.get(classfi);
                        items.add(id);
                        classUsers.put(classfi,items);
                    }
                }
            }
            reader.close();

            // Add weights(calculated from co-clustering) parts to original weights matrix

            final BufferedWriter writer = new BufferedWriter(new FileWriter(Config.ratingModel));

            ThreadPoolExecutor executor = new ThreadPoolExecutor(300,600,200, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(10000),new ThreadPoolExecutor.CallerRunsPolicy() );

            ArrayList<Person> groupp = group.getGroup();
            Set<Integer> userids = new HashSet<Integer>();
            for (Person p : groupp){
                userids.add(Integer.parseInt(p.getUserID()));
            }

            logger.info("Started adding weights to weight matrix.");
            int x = weights.length;
            int y = weights[0].length;
            final double[][] weightstmp = new double[x][y];
            for (int i = 0; i < x;i++){
                weightstmp[i] = weights[i].clone();
            }
            Long startTime = System.currentTimeMillis();
            for (int i = 0;i < x ; i++){
                if (!userids.contains(i))continue;
                for (int j = 0;j < y;j++){
                    if (weights[i][j] != 0) continue;
                    else{
//                        Long startTime = System.currentTimeMillis();
                        final int finalI = i;
                        final int finalJ = j;
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                weights[finalI][finalJ] = getMissWeight(finalI, finalJ,weightstmp,userClass,itemClass,classUsers,classItems);
                                synchronized (writer) {
                                    try {
                                        writer.write(finalI + "," + finalJ + ":" + weights[finalI][finalJ]);
                                        writer.newLine();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
//                        weights[i][j] = getMissWeight(i,j,weightstmp,userClass,itemClass,classUsers,classItems);
//                        Long endTime = System.currentTimeMillis();
//                        System.out.println("------------" + (endTime - startTime));
                    }
//                    writer.write(i + "," + j + ":" + weights[i][j]);
//                    writer.newLine();
                    //System.out.println(i + "," + j + ":" + weights[i][j]);
                }
            }
            Long endTime = System.currentTimeMillis();
            System.out.println("------------" + (endTime - startTime));
            while(executor.getActiveCount() != 0){
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            executor.shutdownNow();
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("Finished filling up massing value to matrix.");
    }

    /**
     * Cget miss value avg(avg(uclass) + avg(iclass))
     * @param i uid
     * @param j itemid
     * @param weightstmp old weights
     * @param userClass
     * @param itemClass
     * @param classUsers
     * @param classItems
     * @return
     */
    private double getMissWeight(int i, int j, double[][] weightstmp, Map<Integer, Integer> userClass, Map<Integer, Integer> itemClass, Map<Integer, HashSet<Integer>> classUsers, Map<Integer, HashSet<Integer>> classItems) {
        int uclass = userClass.get(i);
        HashSet<Integer> users = classUsers.get(uclass);
        double uscore = 0;
        int ucount = 0;
        for (Integer user : users){
            if (weightstmp[user][j] != 0){
                uscore += weightstmp[user][j];
                ucount ++;
            }
        }

        int iclass = itemClass.get(j);
        HashSet<Integer> items = classItems.get(iclass);
        double iscore = 0;
        int icount = 0;
        for (Integer item : items){
            if (weightstmp[i][item] != 0){
                iscore += weightstmp[i][item];
                icount++;
            }
        }
        if (ucount != 0 && icount != 0){
            return (uscore/ucount + iscore/icount)/2;
        }else if (ucount != 0 && icount == 0){
            return uscore/ucount;
        }else if (ucount == 0 && icount != 0){
            return iscore/icount;
        }else{
            return 0;
        }
    }
}