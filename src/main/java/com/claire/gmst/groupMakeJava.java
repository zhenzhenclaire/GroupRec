package com.claire.gmst;

import com.claire.preprocessing.DurationComputing;
import com.claire.util.*;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.yarn.util.SystemClock;

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
    ReflectionMap reflection;
    private String userItemRatingPath;
    private Map<Integer,String> userItems = new HashMap<Integer, String>();

    public groupMakeJava(){}

    public groupMakeJava(Group group) {
        this.group = group;
        g = new Graph();
        userItemRatingPath = Config.parsedMatrixPath;
        reflection = new ReflectionMap();
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
            weights = new double[Config.numOfUsers][Config.numOfHotels];

            initializeWeightsByRating();

            missingValueSupplement();

            // Create user nodes and add them to graph
            HashSet<UserNode> userNodeSet = new HashSet<UserNode>();

            for(Person p : group.getGroup()){
                int id = Integer.parseInt(p.getUserID());
                String name =  reflection.findUserNameById(id);
                String location = p.getLatitude() + "," + p.getLongitude();

                UserNode unode = new UserNode(name,location,id,p.getTravelMode());
                System.out.println(p.getTravelMode().value());

                //logger.info("user " + unode.getId() + ",location" + unode.getLocation() + ",name" + unode.getName());
                userNodeSet.add(unode);
                //logger.info(userNodeSet.size() + "");
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

    /**
     * Make graph
     */
    public void makeGraph(){
        logger.info("Start making graph.");
        this.initBaseData();

        for (UserNode node : g.getUserNodes()){
            String items = userItems.get(node.getId());
            logger.info(items);

            for (String hotel : items.split(",")){
                int id = Integer.parseInt(hotel);
                String name = reflection.findItemNameById(id);
                String location = reflection.findLocationByID(name);
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

        System.out.println("edges.size--------------------------" + edges.size());

        // Fill up the graph
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

//        edges = g.getEdges();
//        for (Edge edge : edges){
//            System.out.println(edge.getUnode().getName() + "---->" + edge.getInode().getName() + ":" + edge.getWeight());
//        }

        //System.out.println("users:" + g.getUserNodes().size() + "items:" + g.getItemNodes().size() + "edges:" + edges.size());

        logger.info("Start calculating duration.");
        initializeWeightsByDistance();
        logger.info("Done calculating duration");
    }

    /**
     * Implement missing value from co-clustering
     */
    private void missingValueSupplement(){
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
            System.out.println("Run time------------" + (endTime - startTime));
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

    private void initializeWeightsByDistance(){
        ThreadPoolExecutor executor = new ThreadPoolExecutor(300,600,200, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(10000),new ThreadPoolExecutor.CallerRunsPolicy() );

        for(final Edge edge: g.getEdges()){
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    DurationComputing durationComp = new DurationComputing();
                    int duration = 0;
                    duration = durationComp.getDurationToOneDestination(edge);
                    edge.setDuration(duration);
                }
            });
        }
        while(executor.getActiveCount() != 0){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        executor.shutdownNow();
        System.out.println("----------------");
        for (Edge edge : g.getEdges()){
            System.out.println(edge.getUnode().getLocation() + "---->" + edge.getInode().getLocation() + ":" + edge.getDuration()/3600);
        }

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

//    public static void main(String[] args){
//        groupMakeJava gm = new groupMakeJava();
//        UserNode unode = new UserNode("33.4531,-111.9863");
//        ItemNode inode = new ItemNode("47.4161949287676,-122.297822639463");
//        Edge edge = new Edge(unode,inode);
//
//        int re = gm.getDurationToOneDestination(edge);
//        System.out.println("^^^^^^^^^^" + re);
//    }
    /*
    37.7878,-122.4087---->37.7820712239237,-122.404752615726
33.4531,-111.9863---->34.0606777704788,-118.241058532845
37.7878,-122.4087---->47.6205226842016,-122.359717853256
40.7575,-73.9700---->40.7561041105555,-73.9709545342324
32.9515,-96.8286---->47.6076324465426,-122.332449700123
32.9515,-96.8286---->37.7820712239237,-122.404752615726
33.4531,-111.9863---->37.7861862879899,-122.411544630287
33.4531,-111.9863---->40.7612125910001,-73.9917933418917
32.9515,-96.8286---->47.6205226842016,-122.359717853256
40.7575,-73.9700---->47.4161949287676,-122.297822639463
34.0727,-118.3729---->37.7916261549677,-122.409607988594
37.7878,-122.4087---->47.6581135708081,-122.317373576405
33.4531,-111.9863---->47.6178554955421,-122.329883692207
40.7575,-73.9700---->34.0525511445873,-118.250452873686
40.7596,-73.9847---->37.7851825079676,-122.405671279144
33.4531,-111.9863---->37.7914046819271,-122.410188832968
32.9515,-96.8286---->47.6069464466069,-122.334162306443
32.8978,-97.0398---->40.7612125910001,-73.9917933418917
32.9515,-96.8286---->34.0606777704788,-118.241058532845
33.4531,-111.9863---->42.3495174782587,-71.0795062999039
40.7575,-73.9700---->32.7798929476977,-96.7998399204279
40.7596,-73.9847---->37.7820712239237,-122.404752615726
34.0727,-118.3729---->40.7561041105555,-73.9709545342324
33.4531,-111.9863---->37.7916261549677,-122.409607988594
Jan 23, 2016 5:45:42 PM com.claire.gmst.groupMakeJava makeGraph
INFO: Done calculating duration
40.7596,-73.9847---->34.1031355493014,-118.340597996934
33.4531,-111.9863---->40.7561041105555,-73.9709545342324
40.7596,-73.9847---->37.7914046819271,-122.410188832968
32.8978,-97.0398---->32.7798929476977,-96.7998399204279
40.7575,-73.9700---->47.6076324465426,-122.332449700123
32.8978,-97.0398---->34.0496734424843,-118.240236993802
40.7596,-73.9847---->37.7861862879899,-122.411544630287
40.7596,-73.9847---->47.6069464466069,-122.334162306443
40.7596,-73.9847---->40.7612125910001,-73.9917933418917
40.7575,-73.9700---->34.0496734424843,-118.240236993802
37.7878,-122.4087---->40.7612125910001,-73.9917933418917
40.7575,-73.9700---->37.7851825079676,-122.405671279144
32.8978,-97.0398---->34.1029312634910,-118.339168413719
40.7575,-73.9700---->47.6178554955421,-122.329883692207
34.0727,-118.3729---->47.6123491387548,-122.333257850956
33.4531,-111.9863---->47.6581135708081,-122.317373576405
40.7575,-73.9700---->47.6069464466069,-122.334162306443
32.9515,-96.8286---->47.4161949287676,-122.297822639463
40.7596,-73.9847---->34.0525511445873,-118.250452873686
40.7575,-73.9700---->42.3495174782587,-71.0795062999039
40.7575,-73.9700---->37.7861862879899,-122.411544630287
32.8978,-97.0398---->34.0606777704788,-118.241058532845
33.4531,-111.9863---->34.0525511445873,-118.250452873686
34.0727,-118.3729---->47.6205226842016,-122.359717853256
34.0727,-118.3729---->34.0606777704788,-118.241058532845
34.0727,-118.3729---->47.6069464466069,-122.334162306443
34.0727,-118.3729---->37.7820712239237,-122.404752615726
34.0727,-118.3729---->34.0525511445873,-118.250452873686
33.4531,-111.9863---->47.4161949287676,-122.297822639463
     */
}