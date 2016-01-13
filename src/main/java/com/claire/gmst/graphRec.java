package com.claire.gmst;

import com.claire.util.Config;
import com.claire.util.Group;
import com.claire.util.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by claire on 1/12/16.
 */
public class graphRec {
    Group group;
    String userReflectionTable = Config.dataPath + "userReflectionTable";
    String itemReflectionTable = Config.dataPath + "hotelReflectionTable";
    String userItemRatingPath = Config.clusteringPath + "parsedMatrix";
    String hotelLocationPath = Config.dataPath + "HotelUserInfo/part-00000";
    Map<Integer,String> userMapping = new HashMap<Integer, String>();//
    Map<Integer,String> itemMapping = new HashMap<Integer, String>();
    Map<Integer,String> userItems = new HashMap<Integer, String>();

    Map<String,String> hotelLocation = new HashMap<String, String>();

    int[][] ratingMatrix; //Preference matrix (user item rating)
    int[][] durationMatrix;

    public graphRec(Group group) {
        this.group = group;
    }

    public void makeGroup(){
        groupMake gMake = new groupMake(group,userReflectionTable,itemReflectionTable,userItemRatingPath,hotelLocationPath);
        gMake.makeGraph();
    }

    public static void main(String[] args){
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
