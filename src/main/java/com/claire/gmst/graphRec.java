package com.claire.gmst;

import com.claire.util.*;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by claire on 1/12/16.
 */
public class graphRec {
    Group group;
    String userItemRatingPath;
    String hotelLocationPath;
    String ratingModel;
    String userReflectionTable;
    String itemReflectionTable;

    ArrayList<ItemNode> hotelNodeList;
    ArrayList<UserNode> userNodeList;

    int[][] ratingMatrix; //Preference matrix (user item rating)
    int[][] durationMatrix;

    public graphRec(Group group) {
        userItemRatingPath = Config.parsedMatrixPath;
        hotelLocationPath = Config.userHotelInfo;
        ratingModel = Config.ratingModel;
        this.group = group;
        userReflectionTable = Config.userReflectionTable;
        itemReflectionTable = Config.hotelReflectionTable;
    }

    public void findAllHotels(){
        int i = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(ratingModel));
            String line = "";
            while((line = reader.readLine()) != null){

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addUserNodes(){
        int i = 0;
        for(Person p:group.getGroup()){
            UserNode uNode = new UserNode();
            uNode.setId(i);
            uNode.setLocation(p.getLatitude() + "," + p.getLongitude());
            uNode.setName(p.getUserID());
            i++;
            userNodeList.add(uNode);
        }
    }

    public void makeGroup(){
        groupMakeJava groupMake = new groupMakeJava(group,userReflectionTable, itemReflectionTable, userItemRatingPath, hotelLocationPath);
        groupMake.makeGraph();
    }
    public static void main(String[] args){
        Config.init();

        ArrayList<Person> PersonList = new ArrayList<Person>();

        Person Person1 = new Person("13119","40.7575","-73.9700");
        PersonList.add(Person1);
        Person Person2 = new Person("27373","40.7596","-73.9847");
        PersonList.add(Person2);
        Person Person3 = new Person("31450","34.0727","-118.3729");
        PersonList.add(Person3);
        Person Person4 = new Person("3140","33.4531","-111.9863");
        PersonList.add(Person4);
        Person Person5 = new Person("17043","32.9515","-96.8286");
        PersonList.add(Person5);
        Person Person6 = new Person("37877","32.8978","-97.0398");
        PersonList.add(Person6);
        Person Person7 = new Person("11233","37.7878","-122.4087");
        PersonList.add(Person7);

        String destination = "32.7974,-96.8256";
        Group group = new Group(PersonList);

        graphRec gRec = new graphRec(group);
        gRec.makeGroup();
    }
}
