package com.claire.util;

import jdk.internal.util.xml.impl.Input;

import java.io.*;
import java.util.Properties;

/**
 * Created by claire on 12/9/15.
 */
public class Config {
    public static String dataPath;
    public static String clusteringPath;
    public static String userHotelInfo;
    public static String originalMatrixPath;
    public static String parsedMatrixPath;
    public static String squareMatrixPath;
    public static String clusteringResult;
    public static String userReflectionTable;
    public static String hotelReflectionTable;
    public static int numOfUsers;
    public static int numOfHotels;

    public static String filePath = "conf/config.properties";

    public static void writeToProperty(){
        Properties properties = new Properties();
        try {
            OutputStream out = new BufferedOutputStream(new FileOutputStream(filePath));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void init(){
        Properties properties = new Properties();

        try {
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataPath = properties.getProperty("dataPath");
        clusteringPath = dataPath + properties.getProperty("clusteringPath");
        userHotelInfo = dataPath + properties.getProperty("userHotelInfo");
        originalMatrixPath = clusteringPath + properties.getProperty("originalMatrixPath");
        parsedMatrixPath = clusteringPath + properties.getProperty("parsedMatrixPath");
        squareMatrixPath = clusteringPath + properties.getProperty("squareMatrixPath");
        clusteringResult = clusteringPath + properties.getProperty("clusteringResult");
        userReflectionTable = dataPath + properties.getProperty("userReflectionTable");
        hotelReflectionTable = dataPath + properties.getProperty("hotelReflectionTable");
    }


//    public Config(){
//        CreateFileUtil.createDir(clusteringPath);
//    }

}
