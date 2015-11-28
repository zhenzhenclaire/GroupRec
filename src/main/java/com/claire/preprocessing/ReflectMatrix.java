package com.claire.preprocessing;

import javax.swing.text.html.parser.Entity;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by claire on 11/28/15.
 */
public class ReflectMatrix {
    String originalMatrix = "";
    String parsedMatrix = "";
    String userReflectionTable = "";
    String hotelReflectionTable = "";
    HashMap<String,Integer> hotelMap;
    HashMap<String,Integer> userMap;
    int hotelIndex = 0;
    int userIndex = 0;

    public ReflectMatrix(String parsedMatrix, String originalMatrix, String userReflectionTable, String hotelReflectionTable) {
        this.parsedMatrix = parsedMatrix;
        this.originalMatrix = originalMatrix;
        this.userReflectionTable = userReflectionTable;
        this.hotelReflectionTable = hotelReflectionTable;
    }

    public void readMatrix(){
        try {
            userMap = new HashMap<String, Integer>();
            hotelMap = new HashMap<String, Integer>();
            hotelIndex = 0;
            userIndex = 0;

            BufferedReader reader = new BufferedReader(new FileReader(originalMatrix));
            BufferedWriter writer = new BufferedWriter(new FileWriter(parsedMatrix));

            String lineContent = "";
            int hID = 0;
            int uID = 0;

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

            writeReflection(userMap,userReflectionTable);
            writeReflection(hotelMap,hotelReflectionTable);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeReflection(HashMap<String,Integer> map, String path){
        try {
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

    public int addUser(String userID){
        if(userMap.get(userID) != null){
            return userMap.get(userID);
        }
        else{
            userMap.put(userID,userIndex++);
            return userIndex - 1;
        }
    }

    public int addHotel(String hotelID){
        if(hotelMap.get(hotelID) != null){
            return hotelMap.get(hotelID);
        }
        else{
            hotelMap.put(hotelID,hotelIndex++);
            return hotelIndex - 1;
        }
    }

    public static void main(String[] args){
        String dataResource = "DataResource/";
        ReflectMatrix rm = new ReflectMatrix(dataResource + "parsedMatrix", dataResource + "RatingMatrix.txt", dataResource + "userReflection.txt", dataResource + "hotelReflection.txt");
        rm.readMatrix();
    }

}
