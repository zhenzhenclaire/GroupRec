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


    public void makeGroup(){
        GroupMake groupMake = new GroupMake(group);
        groupMake.makeGraph();
        InducedGraph iG = new InducedGraph(groupMake.g);
        Double[][] iGMatrix = iG.generateG();
        Prim p = new Prim(iGMatrix);
        ArrayList<MSTPair> pairList = p.prim();
        ArrayList<ItemBridge> resultList = iG.transformBack(pairList);
        System.out.println("result:---------------");
        for(ItemBridge ib:resultList){
            System.out.println(ib.getiNode().getName() + ":" + ib.getSumWeight());
        }
    }
    public static void main(String[] args){
        Config.init();

        ArrayList<Person> PersonList = new ArrayList<Person>();

        Person Person1 = new Person("13119","40.7575","-73.9700", UserNode.Mode.BICYCLING);
        PersonList.add(Person1);
        Person Person2 = new Person("27373","40.7596","-73.9847", UserNode.Mode.BICYCLING);
        PersonList.add(Person2);
        Person Person3 = new Person("31450","34.0727","-118.3729", UserNode.Mode.DRIVING );
        PersonList.add(Person3);
        Person Person4 = new Person("3140","33.4531","-111.9863", UserNode.Mode.WALKING);
        PersonList.add(Person4);
        Person Person5 = new Person("17043","32.9515","-96.8286", UserNode.Mode.WALKING);
        PersonList.add(Person5);
//        Person Person6 = new Person("37877","32.8978","-97.0398", UserNode.Mode.BICYCLING);
//        PersonList.add(Person6);
//        Person Person7 = new Person("11233","37.7878","-122.4087", UserNode.Mode.DRIVING);
//        PersonList.add(Person7);

        Group group = new Group(PersonList);

        graphRec gRec = new graphRec(group);
        gRec.makeGroup();
    }
}
