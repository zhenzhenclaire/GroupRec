import org.json.JSONObject;
import org.apache.log4j.Logger;

/**
 * Created by Claire on 11/2/2015.
 */
public class DataFormat {
    public static File inFile;
    public static File outFile;
    private static Logger logger = Logger.getLogger(DataFormat.class);

    public static String getFromJSON(JSONObject jsonObject, String keyString){
        if(jsonObject.keySet().contains(keyString)){
            return jsonObject.get(keyString).toString() + ",";
        }
        else    return "MEIYOU,";
    }

    public static void transform(String fileIn, String fileOut) {
        inFile = new File(fileIn);
        outFile = new File(fileOut);

        BufferedReader reader = null;
        BufferedWriter writer = null;

        JSONObject resultJSON = new JSONObject();

        try {
            reader = new BufferedReader(new FileReader(inFile));
            writer = new BufferedWriter(new FileWriter(outFile));

            String tempString = null;
            int count = 1;

            while ((tempString = reader.readLine()) != null) {

                JSONObject tempResult = new JSONObject(tempString);

                StringBuffer outString = new StringBuffer("");

                String hotelId = getFromJSON(tempResult, "id");
                outString.append(hotelId);

                String hotelName = getFromJSON(tempResult, "name");
                outString.append(hotelName);

//                String hotelClass = getFromJSON(tempResult, "hotel_class");
//                outString.append(hotelClass);

                String region =  getFromJSON(tempResult.getJSONObject("address"), "region");
                outString.append(region);

                String streetAd = getFromJSON(tempResult.getJSONObject("address"), "street-address");
                outString.append(streetAd);

                String postCode = getFromJSON(tempResult.getJSONObject("address"), "postal-code");
                outString.append(postCode);

                String locality = getFromJSON(tempResult.getJSONObject("address"), "locality");
                outString.append(locality);

                String output = outString.toString();
                writer.write(output.substring(0, output.length() - 1));
                writer.newLine();
            }
            reader.close();
            logger.info("Finish read");
            writer.close();
            logger.info("Finish write");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args){
        transform("offering.txt","Hotel.txt");
    }
}
