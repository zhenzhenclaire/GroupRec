package com.claire.coclustering;

import com.claire.analysis.SpectralClustering;
import com.claire.preprocessing.BuildMatrix;
import com.claire.preprocessing.ReflectMatrix;
import com.claire.util.Config;
import com.claire.util.CreateFileUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import static com.claire.preprocessing.BuildMatrix.makeMatrix;

/**
 * Created by claire on 1/6/16.
 */
public class clustering {
    static Logger logger = Logger.getLogger("clustering");

    String userHotelInfo;
    String originalMatrixPath;
    String parsedMatrixPath;
    String squareMatrixPath;
    String clusteringResult;
    String userReflectionTable;
    String hotelReflectionTable;


    public clustering(){
        userHotelInfo = Config.userHotelInfo;
        originalMatrixPath = Config.originalMatrixPath;
        parsedMatrixPath = Config.parsedMatrixPath;
        squareMatrixPath = Config.squareMatrixPath;
        clusteringResult = Config.clusteringResult;
        userReflectionTable = Config.userReflectionTable;
        hotelReflectionTable = Config.hotelReflectionTable;
    }
    public void prepareClustering(){
        try {
            logger.info("Start make matrix");
            makeMatrix(userHotelInfo, originalMatrixPath);
            logger.info("Done make matrix");
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("Start reflect matrix");
        BuildMatrix buildMatrix = new BuildMatrix();

        ReflectMatrix rm = new ReflectMatrix(originalMatrixPath, parsedMatrixPath, squareMatrixPath, userReflectionTable, hotelReflectionTable);
        rm.readMatrix();

        try {
            rm.makeAdjacencyMatrix();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Done reflect matrix");
    }

    public void coClustering(int numDims, int clusters, int maxIterations){
        SpectralClustering sc = new SpectralClustering(squareMatrixPath,clusteringResult,numDims,clusters,maxIterations);

//        //RedirectConsole
//        PrintStream ps= null;
//        try {
//            ps = new PrintStream(new FileOutputStream(sc.consoleOutput));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        System.setOut(ps);

        try {
            sc.clustering();
            sc.extractClass();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Redirect back to console
//        System.setOut(System.out);
    }

    public static void main(String args[]){
        Config.init();
        clustering cl = new clustering();
        cl.prepareClustering();
        cl.coClustering(40025,20,10000);
    }

}
