package com.claire.gmst;

import com.claire.util.*;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Claire on 1/23/2016.
 */
public class InducedGraph {
    Graph graph;
    ArrayList<Edge> edgeList;
    ArrayList<ItemBridge> ibList;

    public InducedGraph(Graph graph) {
        this.graph = graph;
        edgeList = new ArrayList<Edge>(graph.getEdges());
        ibList = new ArrayList<ItemBridge>();
    }

    //1. Give usernode graphId
    private void giveUserGraphId(){
        Set<UserNode> userNodeSet = graph.getUserNodes();
        int index = 1;
        for(UserNode unode:userNodeSet){
            unode.setGraphId(index);
            index++;
        }
    }

    //2. Calculate distance of two usernodes
    private ItemBridge getMinDistanceOfUsers(UserNode u1, UserNode u2){
        ItemBridge ib = new ItemBridge(u1,u2);

        double minDistance = Double.MAX_VALUE;
        for(ItemNode i:graph.getItemNodes()){
            Edge e1 = new Edge(u1,i);
            Edge e2 = new Edge(u2,i);
            int i1 = edgeList.indexOf(e1);
            int i2 = edgeList.indexOf(e2);
            double dis = edgeList.get(i1).getCombinedWeight() + edgeList.get(i2).getCombinedWeight();
            if(dis  < minDistance){
                minDistance = dis;
                ib.setiNode(i);
                ib.setSumWeight(minDistance);
            }
        }
        return ib;
    }

    //3. Generate G'
    private void generateIBList(){
        giveUserGraphId();
        ibList.clear();
        ArrayList<UserNode> uNodes = new ArrayList<UserNode>(graph.getUserNodes());

        for(int i = 0;i < uNodes.size();i++){
            for(int j = i + 1;j < uNodes.size();j++){
                ibList.add(getMinDistanceOfUsers(uNodes.get(i),uNodes.get(j)));
            }
        }
    }

    //4. Generate G' matrix
    private Double[][] generateMatrix(){
        int dim = graph.getUserNodes().size() + 1;
        Double[][] gMatrix = new Double[dim][dim];
        for(int i = 0;i < dim;i++){
            for(int j = 0;j < dim;j++){
                if (i != 0 && i== j)
                    gMatrix[i][j] = Double.MAX_VALUE;
                else
                    gMatrix[i][j] = 0.0;
            }
        }

        for(ItemBridge ib : ibList){
            gMatrix[ib.getuFrom().getGraphId()][ib.getuTo().getGraphId()] = ib.getSumWeight();
            gMatrix[ib.getuTo().getGraphId()][ib.getuFrom().getGraphId()] = ib.getSumWeight();
        }
        return gMatrix;
    }

    public Double[][] generateG(){
        generateIBList();
        return generateMatrix();
    }

    public ArrayList<ItemBridge> transformBack(ArrayList<MSTPair> pairList){
        ArrayList<ItemBridge> resultList = new ArrayList<ItemBridge>();
        for(MSTPair p:pairList){
            ItemBridge bridge = new ItemBridge(p.getU1(),p.getU2());
            int index = ibList.indexOf(bridge);
            resultList.add(ibList.get(index));
        }
        return  resultList;
    }
}
