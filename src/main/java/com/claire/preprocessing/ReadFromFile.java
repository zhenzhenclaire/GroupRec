package com.claire.preprocessing;

import com.claire.util.Config;

import java.io.*;
import java.util.logging.Logger;

/**
 * Util class to deal with files.
 * Created by Claire on 11/10/2015.
 */
public class ReadFromFile {
    static Logger logger = Logger.getLogger("ReadFromFile");

    public void rewriteFromTargetString(String inputFile, String outputFile, String targetString){
        File file = new File(inputFile);
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            logger.info("Start read file:" + inputFile);
            reader = new BufferedReader(new FileReader(file));
            writer = new BufferedWriter(new FileWriter(new File(outputFile)));
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
            logger.info("Done processing.");
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

    public void readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            while ((tempString = reader.readLine()) != null && line < 10) {
                //int num = tempString.split(",").length;

                System.out.println("line " + line + ": " + tempString);
                line++;
            }
            reader.close();
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

    public static void main(String[] args){
        //String dataPath = "DataResource/HotelUserInfo/part-00000";
        //String dataPath = "DataResource/CD-00001-00000002.dat";
        ReadFromFile readFromFile = new ReadFromFile();
        Config.init();
        //readFromFile.readFileByLines(Config.dataPath + "/");
//
//        String inputPath = Config.dataPath + "twentyClusters/thousand20.txt";
//        String outputPath = Config.dataPath + "twentyClusters/thousand20Class.txt";
//        String targetString = "INFO SpectralKMeansDriver: 0: 8";

        //readFromFile.rewriteFromTargetString(inputPath,outputPath,targetString);
        readFromFile.readFileByLines(Config.dataPath + "HotelUserInfo/part-00000");
    }
}


