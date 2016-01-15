package com.claire.analysis;

import com.claire.util.Config;
import com.claire.util.SequenceFileReader;
import org.apache.commons.el.IntegerLiteral;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.clustering.spectral.kmeans.SpectralKMeansDriver;
import org.apache.mahout.utils.SequenceFileDumper;

import java.io.*;
import java.util.logging.Logger;

/**
 * http://blog.csdn.net/xyilu/article/details/9883701
 */
public class SpectralClustering {
    static Logger logger = Logger.getLogger("clustering");
    String input;
    String output;
    public String consoleOutput = Config.dataPath + "/clustering/consoleOutputFromMahout";
    int numDims;
    int clusters;
    int maxIterations;
    String mahoutOutput;

    public SpectralClustering(String input, String output,int numDims, int clusters, int maxIterations) {
        this.input = input;
        this.output = output;
        this.numDims = numDims;
        this.clusters = clusters;
        this.maxIterations = maxIterations;
        this.mahoutOutput = Config.dataPath + "clustering/mahoutResult/";
    }

    public void clustering(String[] args) throws Exception {
        logger.info("Start clustering");
        ToolRunner.run(new SpectralKMeansDriver(),args);
        logger.info("Finished clustering");
    }

    public void clustering() throws Exception {
        String[] args = new String[10];
        args[0] = "-i";
        args[1] = input;
        args[2] = "-o";
        //args[3] = Config.dataPath + "mahout";
        args[3] = mahoutOutput;
        args[4] = "-d";
        args[5] = numDims + "";
        args[6] = "-k";
        args[7] = clusters + "";
        args[8] = "-x";
        args[9] = maxIterations + "";
        clustering(args);
    }

    public void extractClass(){
        String sequencePath = mahoutOutput + "/kmeans_out/clusteredPoints/part-m-00000";
        String textPath = Config.dataPath + "clustering/clusteredPoints";
        SequenceFileReader sr = new SequenceFileReader(sequencePath, textPath);
        try {
            sr.convertSeqFile(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        File file = new File(textPath);
        BufferedReader reader = null;
        BufferedWriter writer = null;
        int lineNum = 0;
        try {
            logger.info("Start read file:" + textPath);
            reader = new BufferedReader(new FileReader(file));
            writer = new BufferedWriter(new FileWriter(new File(output)));
            String tempString = null;

            while ((tempString = reader.readLine()) != null) {
                //logger.info(tempString);
                if(tempString.contains("<===>")){
                    String[] contents = tempString.split("<===>");
                    int id = lineNum;
                    String type = contents[0].trim();

                    if(id < Config.numOfHotels){
                        writer.write("hotel:" + id + ":" + type);
                    }
                    else{
                        writer.write("user:" + (id - Config.numOfHotels) + ":" + type);
                    }

                    writer.newLine();
                    lineNum++;
                    }
                }
            } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            reader.close();
            writer.flush();
            writer.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        logger.info("Done processing.");
    }

    public void rewriteFromTargetString(String targetString){
        logger.info("start rewriting");
        File file = new File(consoleOutput);
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            logger.info("Start read file:" + consoleOutput);
            reader = new BufferedReader(new FileReader(file));
            writer = new BufferedWriter(new FileWriter(new File(output)));
            String tempString = null;
            Boolean flag = false;
            while ((tempString = reader.readLine()) != null) {
                //logger.info(tempString);
                if(flag || tempString.contains(targetString)){
                    if(tempString.contains("SpectralKMeansDriver:")){
                        String[] contents = tempString.split("SpectralKMeansDriver:");
                        writer.write(contents[1]);
                        writer.newLine();
                        flag = true;
                    }
                }
                else{
                    continue;
                }
            }
            reader.close();
            writer.flush();
            writer.close();
            logger.info("Done rewriting.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String input = Config.dataPath + "clustering/squareMatrix";
        String output = Config.dataPath + "clustering/clusteringResult";

        int numDims = 40025;
        int clusters = 20;
        int maxIterations = 100;

        SpectralClustering sc = new SpectralClustering(input,output,numDims,clusters,maxIterations);

        //RedirectConsole
        PrintStream ps=new PrintStream(new FileOutputStream(sc.consoleOutput));
        System.setOut(ps);

        sc.clustering();

        String targetString = "INFO SpectralKMeansDriver: 0: 8";
        sc.rewriteFromTargetString(targetString);

        System.setOut(System.out);
    }

}
