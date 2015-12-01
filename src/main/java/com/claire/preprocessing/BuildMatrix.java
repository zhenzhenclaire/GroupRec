package com.claire.preprocessing;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This class aims to prepare dataset for mahout, formatted as <userID, hotelID, rating>
 * Created by Claire on 11/28/2015.
 */
public class BuildMatrix {
    static Logger logger = Logger.getLogger("BuildMatrix");
    public static String dataPath = "DataResource/";
    public static Set<String> blackList = new HashSet<String>();

    /**
     * Add black user into list
     * @param file black list file
     * @throws Exception
     */
    public static void readBlackList(String file) throws Exception {
        logger.info("Start reading black list.");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String tempString = null;
        blackList.clear();
        while ((tempString = reader.readLine()) != null) {
            blackList.add(tempString);
        }
        reader.close();
        logger.info("Finished reading black list.");
    }

    /**
     * Extract user, hotel, overall rating from HotelUserInfo and make original matrix
     * @param inputPath File path of HotelUserInfo file
     * @param outputPath Matrix path
     * @throws Exception
     */
    public static void makeMatrix(String inputPath, String outputPath) throws Exception {
        logger.info("Start make matrix from " + inputPath);

        File inputFile = new File(inputPath);
        File[] files = inputFile.listFiles();

        readBlackList(dataPath + "BlackList");
        logger.info(" Loaded blacklist");

        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(new File(outputPath)));
            for(File file:files) {

                if(!file.getName().startsWith("part-0000")) continue;

                logger.info("Start processing file:" + file.getName().toString());
                reader = new BufferedReader(new FileReader(file));

                String tempString = null;
                String outString = null;
                String hotelID = null;
                String userID = null;
                String overallRating = null;

                while ((tempString = reader.readLine()) != null) {
                    String[] fields = tempString.split(",");
                    hotelID = fields[0];
                    userID = fields[8];
                    if(blackList.contains(userID) || userID.trim().length() == 0)  continue;
                    overallRating = fields[11];
                    outString = hotelID + "," + userID + "," + overallRating;
                    writer.write(outString);
                    writer.newLine();
                }
                reader.close();
                logger.info("Finished processing:" + file.getName());
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static  void main(String[] args) throws Exception {
        String inputPath = dataPath + "HotelUserInfo";
        String ratingMatrixPath = dataPath + "RatingMatrix.txt";

        makeMatrix(inputPath, ratingMatrixPath);
    }

}
