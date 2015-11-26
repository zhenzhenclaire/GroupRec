import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;

import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by Claire on 11/26/2015.
 */
public class DurationComputing {
    static Logger logger = Logger.getLogger("DistanceComputing");

    public static ArrayList<String> getDuration(String origin, String destination) throws Exception {
        //URL to get the distance
        //https://maps.googleapis.com/maps/api/distancematrix/output?parameters

       ArrayList<String> groupDuration = new ArrayList<String>();

        if(origin == null || "".equals(origin) || destination == null || "".equals(destination)){
            return groupDuration;
        }

        String baseURL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=oriGPS&destinations=desGPS";
        String URL = baseURL.replaceAll("oriGPS", origin);
        URL = URL.replaceAll("desGPS", destination);

        logger.info("URL:" + URL);

        // Setup the HttpClient
        HttpClient httpclient = new DefaultHttpClient();

        URL = "www.baidu.com";
        // Setup the HTTP GET method
        HttpGet rootServiceDoc = new HttpGet(URL);

        HttpResponse response;

        try {
            // Execute the request
            Thread.sleep(250);
            response = httpclient.execute(rootServiceDoc);
            System.out.println(">> HTTP Status code:" + response.getStatusLine());

            if (response.getStatusLine().getStatusCode() == 200) {

                String json = returnResponseBody(response);

                logger.info("json from response: " + json);

                JSONObject tempResult = new JSONObject(json);

                JSONArray ja = tempResult.getJSONArray("rows");

                for(int i = 0;i < ja.length();i++){
                    JSONArray elements = ja.getJSONObject(i).getJSONArray("elements");
                    String distance = elements.getJSONObject(0).getJSONObject("distance").getString("text");
                    String duration = elements.getJSONObject(0).getJSONObject("duration").getString("text");
                    logger.info("distance:" + distance);
                    logger.info("duration" + duration);
                    groupDuration.add(duration);
                }

            } else {
                // Release allocated resources
                response.getEntity().consumeContent();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Shutdown the HTTP connection
            httpclient.getConnectionManager().shutdown();
        }

        return groupDuration;
    }

    /**
     * Utility method to pass HttpResponse to get the response body
     *
     * @param response
     * @return the response body
     */
    public static String returnResponseBody(HttpResponse response) {
        String result = "";
        HttpEntity entity = response.getEntity();
        if (entity == null) result = "";
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(entity.getContent()));
            String line = reader.readLine();
            String res = "";
            while (line != null) {
                res = res + line;
                line = reader.readLine();

            }
            result = res;

            reader.close();


        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args){
        ArrayList<User> userList = new ArrayList<User>();

        User user1 = new User("40.7575","-73.9700");
        userList.add(user1);
        User user2 = new User("40.7596","-73.9847");
        userList.add(user2);
        User user3 = new User("34.0727","-118.3729");
        userList.add(user3);
//        User user4 = new User("33.4531","-111.9863");
//        userList.add(user4);
//        User user5 = new User("32.9515","-96.8286");
//        userList.add(user5);
//        User user6 = new User("32.8978","-97.0398");
//        userList.add(user6);
//        User user7 = new User("37.7878","-122.4087");
//        userList.add(user7);

        Group group = new Group(userList);
        String originsString = group.makeOriginURL();
        logger.info("originString:" + originsString);

        String destination = "32.7974,-96.8256";

        try {
            ArrayList<String> durationList = getDuration(originsString, destination);
            for(int i = 0;i < durationList.size();i++){
                logger.info("user" + i + ":" + durationList.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
