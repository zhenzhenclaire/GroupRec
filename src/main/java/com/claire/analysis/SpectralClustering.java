package com.claire.analysis;

import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.clustering.spectral.kmeans.SpectralKMeansDriver;


public class SpectralClustering {

    static String input ;
    static String output;
    static int numDims;
    static int clusters;
    static int maxIterations;

    public static void clustering(String[] args) throws Exception {
        ToolRunner.run(new SpectralKMeansDriver(),args);
    }

    public static void clustering() throws Exception {
        String[] args = new String[10];
        args[0] = "-i";
        args[1] = input;
        args[2] = "-o";
        args[3] = output;
        args[4] = "-d";
        args[5] = numDims + "";
        args[6] = "-k";
        args[7] = clusters + "";
        args[8] = "-x";
        args[9] = maxIterations + "";
        clustering(args);
    }

    public static  void main(String[] args) throws Exception {
        String dataResource = "DataResource/";
        input = dataResource + "parsedMatrix";
        output = dataResource + "result";
        numDims = 39633;
        clusters = 10;
        maxIterations = 1;
        clustering();


    }
}
