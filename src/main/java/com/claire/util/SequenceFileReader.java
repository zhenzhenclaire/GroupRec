package com.claire.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.ReflectionUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * Created by claire on 11/30/15.
 */
public class SequenceFileReader {
    String sequenceFilePath;
    String textFilePath;

    public SequenceFileReader(String sequenceFilePath,String textFilePath){
        this.sequenceFilePath = sequenceFilePath;
        this.textFilePath = textFilePath;
    }
    /**
     * Convert sequence file to text file
     * @param mode  mode=true, convert local file, else convert remote file
     * @throws Exception
     */
    public void convertSeqFile(boolean mode) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.textFilePath));

        System.out.println("Readeing Sequence File");
        Configuration conf = new Configuration();
        conf.addResource(new Path("/opt/hadoop-2.6.2/etc/hadoop/core-site.xml"));
        conf.addResource(new Path("/opt/hadoop-2.6.2/etc/hadoop/hdfs-site.xml"));

        if(true == mode) {
            conf.set("fs.default.name", "file:///");
        }

        FileSystem fs = FileSystem.get(conf);
        //Path path = new Path("/claire/clusteredPoints");
        Path path = new Path(this.sequenceFilePath);
        SequenceFile.Reader reader = null;
        try {
            reader = new SequenceFile.Reader(fs, path, conf);
            Writable key = (Writable) ReflectionUtils.newInstance(reader.getKeyClass(), conf);
            Writable value = (Writable) ReflectionUtils.newInstance(reader.getValueClass(), conf);
            while (reader.next(key, value)) {
                //System.out.println(key + "  <===>  " + value.toString());
                writer.write(key.toString() + " <===> " + value.toString());
                writer.newLine();
            }
            System.out.print("Done converting.");
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(reader);
        }
    }

    public static void main(String[] args) throws Exception {
        //String remotePath = "/claire/mahout_input/clusterResult/kmeans_out/clusteredPoints/part-m-00000";
        //String remotePath = "/claire/mahout_input/clusterResult/kmeans_out/clusteredPoints/part-m-00000";
//        String remotePath = "DataResource/clustering/clusteringResult/kmeans_out/clusteredPoints/part-m-00000";
//        String localPath = "DataResource/clusteredPointsTest";
//        convertSeqFile(remotePath,localPath,true);
    }
}
