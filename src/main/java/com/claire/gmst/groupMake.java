package com.claire.gmst;

import com.claire.util.AdjGraphic;
import com.claire.util.Group;
import com.claire.util.Person;

import java.util.ArrayList;

/**
 * Created by claire on 1/11/16.
 */
public class groupMake {
    public Group group;
    public AdjGraphic graph;

    public groupMake(Group group){
        this.group = group;
    }

    public ArrayList<String> findCorrespondingItems(String userID){
        ArrayList<String> itemList = new ArrayList<String>();

        return itemList;
    }

    public boolean isUserItemConnected(String userID, String itemID){

        return false;
    }

    public void makeGraph(){
        ArrayList<String> itemList = null;

        for(Person p: group.getGroup()){
            String userID = p.getUserID();
            itemList = findCorrespondingItems(userID);

        }
    }


    AdjGraphic graph = new AdjGraphic(group.getGroup().size());

    Object[] vertices = new Object[]{new Character('A'),new Character('B'),new Character('C'),new Character('D'),new Character('E')};
    Weight[] weights = new Weight[]{new Weight(0,1,10),new Weight(0,4,20),new Weight(2,1,40),new Weight(1,3,30),new Weight(3,2,50)};
    public





}
