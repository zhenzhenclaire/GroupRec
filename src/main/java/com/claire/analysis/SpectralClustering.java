package com.claire.analysis;

import com.claire.util.Config;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.clustering.spectral.kmeans.SpectralKMeansDriver;
import org.apache.mahout.utils.SequenceFileDumper;

/**
 * http://blog.csdn.net/xyilu/article/details/9883701
 */
public class SpectralClustering {

    static String input;
    static String seqOutput;
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
        args[3] = seqOutput;
        args[4] = "-d";
        args[5] = numDims + "";
        args[6] = "-k";
        args[7] = clusters + "";
        args[8] = "-x";
        args[9] = maxIterations + "";
        clustering(args);
    }

    public static void readSequenceFile(String[] args) throws Exception {
        new SequenceFileDumper().run(args);
    }

    public static void readSequenceFile() throws Exception {
        String[] args = new String[5];
        args[0] = "";
        args[1] = "-s";
        args[2] = seqOutput;
        args[3] = "-o";
        args[4] = output;
        readSequenceFile(args);
    }
    public static  void main(String[] args) throws Exception {

        input = Config.dataPath + "parsedMatrix";
        seqOutput = Config.dataPath + "result";
        output = Config.dataPath + "finalResult";
        numDims = 39633;
        clusters = 10;
        maxIterations = 1;
//        clustering();
        readSequenceFile();
    }

}
