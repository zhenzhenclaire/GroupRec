package com.claire.preprocessing;

import javax.swing.text.html.parser.Entity;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Create reflect tables and change <hotelID,userID,rating> matrix to square matrix in reflected number.
 * hadoop fs -put /home/claire/IdeaProjects/GroupRec/DataResource/squareMatrix /claire/mahout_input
 * bin/mahout spectralkmeans -i /home/claire/IdeaProjects/GroupRec/DataResource/squareMatrix -o /claire/mahout_input/clusterResult -d 40025 -k 10 -x 5
 * bin/mahout spectralkmeans -i hdfs://localhost:9000/claire/mahout_input/squareMatrix -o /claire/mahout_input/clusterResult -d 40025 -k 10 -x 5
 * mahout clusterdump -i /claire/mahout_input/clusterResult/kmeans_out/clusters-2-final -o /home/claire/IdeaProjects/GroupRec/DataResource/hundredIterResult.txt -b 100 -p /claire/mahout_input/clusterResult/kmeans_out/clusteredPoints
 * Created by claire on 11/28/15.
 */
public class ReflectMatrix {
    static Logger logger = Logger.getLogger("ReflectMatrix");

    static String dataPath = "DataResource/";
    public String originalMatrix = ""; // original matrix produced by BuildMatrix.java
    public String parsedMatrix = "";   // matrix after reflection
    public String userReflectionTable = dataPath + "userReflectionTable";    // reflection table formatted as <uid, reflectedID>
    public String hotelReflectionTable = dataPath + "hotelReflectionTable";   // reflection table formatted as <hid, reflectedID>
    public String squareMatrix = "";   // square matrix

    private int numOfHotels = 0;  // number of hotels
    private int numOfUsers = 0;  // number of users


    HashMap<String,Integer> hotelMap;
    HashMap<String,Integer> userMap;
    int hotelIndex = 0;
    int userIndex = 0;

    /*
        Constructed function
     */
    public ReflectMatrix(String originalMatrix, String parsedMatrix, String squareMatrix) {
        this.parsedMatrix = parsedMatrix;
        this.squareMatrix = squareMatrix;
        this.originalMatrix = originalMatrix;
    }

    /**
     * Read original matrix and reflect it using reflection tables
     */
    public void readMatrix(){
        try {
            userMap = new HashMap<String, Integer>();
            hotelMap = new HashMap<String, Integer>();
            hotelIndex = 0;
            userIndex = 0;
            numOfHotels = 0;
            numOfUsers = 0;

            BufferedReader reader = new BufferedReader(new FileReader(originalMatrix));
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(parsedMatrix)));

            String lineContent = "";
            int hID = 0;
            int uID = 0;

            logger.info("Write to parsedMatrix.");
            while((lineContent = reader.readLine()) != null ){
                String[] contents = lineContent.split(",");
                if(contents.length != 3)    continue;
                if(contents[1].trim().length() == 0 || contents[0].trim().length() == 0)    continue;
                uID = addUser(contents[1]);
                hID = addHotel(contents[0]);
                writer.write(hID + "," + uID + "," + contents[2]);
                writer.newLine();
            }
            reader.close();
            writer.flush();
            writer.close();

            numOfHotels = hotelMap.size();
            logger.info("Num of hotels: " + numOfHotels);

            numOfUsers = userMap.size();
            logger.info("Num of users: " + numOfUsers);

            writeReflection(userMap,userReflectionTable);
            writeReflection(hotelMap,hotelReflectionTable);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeReflection(HashMap<String,Integer> map, String path){
        try {
            logger.info("Start writing reflection table.");
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            for(Map.Entry<String,Integer> entry: map.entrySet()){
                writer.write(entry.getValue() + " " + entry.getKey());
                writer.newLine();
            }
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int addUser(String userID){
        if(userMap.get(userID) != null){
            return userMap.get(userID);
        }
        else{
            userMap.put(userID,userIndex++);
            return userIndex - 1;
        }
    }

    private int addHotel(String hotelID){
        if(hotelMap.get(hotelID) != null){
            return hotelMap.get(hotelID);
        }
        else{
            hotelMap.put(hotelID,hotelIndex++);
            return hotelIndex - 1;
        }
    }

    /**
     * Build adjacency matrix
     * N = 2
     * M = 3
     * 66,2950,
       66,2951,
       66,2952,
              66,66,66
              2950,2951,2952
     * @throws FileNotFoundException
     */
    public void makeAdjacencyMatrix() throws IOException {
        logger.info("Start make adjacency matrix.");

        BufferedReader reader = new BufferedReader(new FileReader(parsedMatrix));
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(squareMatrix)));

        String tempString = "";
        while((tempString = reader.readLine()) != null){
            writer.write(tempString);
            writer.newLine();

            int uid = 0;
            int hid = 0;
            String[] strings = tempString.split(",");

            hid = Integer.parseInt(strings[0]);
            uid = Integer.parseInt(strings[1]);

//            numOfHotels = 5;
//            numOfUsers = 4;

            int newHid = uid + numOfHotels;
            int newUid = hid + numOfUsers;
//            logger.info("hid:" + newHid + ", uid:" + newUid);

            writer.write(newHid + "," + newUid + "," + strings[2]);
            writer.newLine();
        }

        writer.flush();
        writer.close();
        reader.close();
    }

    public static void main(String[] args) throws Exception {
        String inputPath = dataPath + "HotelUserInfo";

        String originalMatrix = dataPath + "originalMatrix";
        String parsedMatrix = dataPath + "parsedMatrix";
        String squareMatrix = dataPath + "squareMatrix";

        BuildMatrix buildMatrix = new BuildMatrix();
        //buildMatrix.makeMatrix(inputPath, originalMatrix);

        ReflectMatrix rm = new ReflectMatrix(originalMatrix, parsedMatrix, squareMatrix);
        rm.readMatrix();
        rm.makeAdjacencyMatrix();


        //Test
//        String parsedMatrixTest = dataPath + "parseMatrixTest";
//        String squareMatrixTest = dataPath + "squareMatrixTest";
//        ReflectMatrix rmTest = new ReflectMatrix(originalMatrix, parsedMatrixTest, squareMatrixTest);
//
//        rmTest.makeAdjacencyMatrix();


    }

}
