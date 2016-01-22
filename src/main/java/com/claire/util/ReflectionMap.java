package com.claire.util;

import org.apache.hadoop.hdfs.DFSClient;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by claire on 1/22/16.
 */
public class ReflectionMap {
    static Logger logger = Logger.getLogger("reflectionMapping");

    static Map<Integer,String> userMapping = new HashMap<Integer, String>();
    static Map<Integer, String> itemMapping = new HashMap<Integer, String>();
    static Map<String,String> hotelLocationMap = new HashMap<String, String>();

    public ReflectionMap(){
        init();
    }

    public static void init(){
        makeUserReflection(Config.userReflectionTable);
        makeItemReflection(Config.hotelReflectionTable);
        makeHotelLoc(Config.userHotelInfo);
    }

    private static void makeUserReflection(String userReflectionTable){
        logger.info("Start making user mapping");
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
    }

    private static void makeItemReflection(String itemReflectionTable) {
        logger.info("Start making hotel mapping.");
        BufferedReader reader = null;
        String line = null;
        logger.info("Start making item mapping. Reading from" + itemReflectionTable);
        try {
            reader = new BufferedReader(new FileReader(itemReflectionTable));
            line = null;
            while ((line = reader.readLine()) != null) {
                String[] temp = line.split(" ");
                if (temp.length != 2) continue;
                itemMapping.put(Integer.parseInt(temp[0]), temp[1]);
            }
            reader.close();
            logger.info("Finish making hotel mapping.");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void makeHotelLoc(String hotelLocationPath) {
        BufferedReader reader = null;
        String line = null;

        logger.info("Start reading location of hotel.");
        File file = new File(hotelLocationPath);
        File[] files = file.listFiles();
        for (File f : files) {
            try {
                reader = new BufferedReader(new FileReader(f));
                line = null;
                while ((line = reader.readLine()) != null) {
                    String[] temp = line.split(",");
                    hotelLocationMap.put(temp[0], temp[5] + "," + temp[6]);
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

    public String findUserNameById(Integer userID){
        String name = userMapping.get(userID);
        return name;
    }

    public String findItemNameById(Integer itemID){
        String name = itemMapping.get(itemID);
        return name;
    }

    public String findLocationByID(String hotelID){
        String location = hotelLocationMap.get(hotelID);
        return location;
    }

}
