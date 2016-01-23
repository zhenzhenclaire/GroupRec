package com.claire.gmst;

import com.claire.util.MSTPair;
import com.claire.util.UserNode;

import java.util.ArrayList;

/**
 * Created by Claire on 1/23/2016.
 */
/*
 * 最小生成树prim算法，加入最小邻接边生成最小生成树。
 * 首先构造一个零图，选择一个初始点加入到集合中，
 * 然后分别从原来顶点的集合中抽取一个顶点，
 * 选择的标准是构造成的树的权值最小，
 * 循序渐进最终生成一棵最小生成树
 */
public class Prim {
 /*
  * m:定义为无法到达的距离
  * weight:邻接矩阵表,weight表示权值
  * verNum:顶点的个数
  * lowerW:到新集合的最小权值
  * edge:存储到新集合的边
  * checked:判定顶点是否被抽取的集合
  */
    Double[][] weight;
    int verNum;
    Double []lowerW;
    int []edge;
    boolean []checked;

    public Prim(Double[][] weight){
        this.weight = weight;
        this.verNum=weight.length;
        this.lowerW=new Double[verNum];
        this.edge=new int[verNum];
        this.checked=new boolean[verNum];
    }

    public ArrayList<MSTPair> prim(){
        ArrayList<MSTPair> pairList = new ArrayList<MSTPair>();
        checked[1]=true;            //抽取第一个顶点
        int n = verNum-1;
        Double[][] w = weight;

        for(int i=1;i<=n;i++){          //初始化顶点集合
            lowerW[i]=w[1][i];
            edge[i]=1;
            checked[i]=false;
        }

        for(int i=1;i<=n;i++){
            Double min=Double.MAX_VALUE;
            int j=1;
            for(int k=2;k<=n;k++){        //判定是否抽取该顶点
                if(lowerW[k]<min&&(!checked[k])){
                    min=lowerW[k];
                    j=k;
                }
            }

            if(i<n){
                MSTPair pair;
                if(j > edge[j]){
                    pair = new MSTPair(new UserNode(edge[j]),new UserNode(j));
                }
                else {
                    pair = new MSTPair(new UserNode(j),new UserNode(edge[j]));
                }
                pairList.add(pair);
            }

            checked[j]=true;           //将顶点加入到新集合中

            for(int k=2;k<=n;k++){        //根据新加入的顶点，求得最小的权值
                if((w[j][k]<lowerW[k])&&(!checked[k])){
                    lowerW[k]=weight[j][k];
                    edge[k]=j;
                }
            }
        }
        return pairList;
    }

//    public static void main(String[] args) {
//        // TODO Auto-generated method stub
//        Prim p=new Prim();
//        p.prim(verNum-1,weight);
//    }
}
