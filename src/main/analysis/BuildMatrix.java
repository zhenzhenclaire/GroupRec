import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This class aims to prepare dataset for mahout, formatted as <userID, hotelID, rating>
 * Created by Claire on 11/28/2015.
 */
public class BuildMatrix {
    static Logger logger = Logger.getLogger("DistanceComputing");
    public static String dataPath = "DataResource/";
    public static Set<String> blackList = new HashSet<String>();

    public static void readBlackList(String file) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String tempString = null;
        blackList.clear();
        while ((tempString = reader.readLine()) != null) {
            blackList.add(tempString);
        }
        reader.close();
    }

    public static void makeMatrix(String inputPath, String outputPath) throws Exception {
        File inputFile = new File(inputPath);
        File[] files = inputFile.listFiles();

        readBlackList(dataPath + "BlackList");

        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(new File(outputPath)));
            for(File file:files) {
                if(!file.getName().startsWith("part-0000")) continue;

                reader = new BufferedReader(new FileReader(file));

                String tempString = null;
                String outString = null;
                String hotelID = null;
                String userID = null;
                String overallRating = null;

                while ((tempString = reader.readLine()) != null) {
                    String[] fields = tempString.split(",");
                    hotelID = fields[0];
                    userID = fields[8];
                    if(blackList.contains(userID))  continue;
                    overallRating = fields[11];
                    outString = hotelID + "," + userID + "," + overallRating;
                    writer.write(outString);
                    writer.newLine();
                }
                reader.close();
                logger.info("Finished:" + file.getName());
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static  void main(String[] args) throws Exception {
        makeMatrix(dataPath + "HotelUserInfo", dataPath + "RatingMatrix.txt");
    }

}
