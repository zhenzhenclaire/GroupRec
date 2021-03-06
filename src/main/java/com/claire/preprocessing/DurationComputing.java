package com.claire.preprocessing;

import com.claire.util.Edge;
import com.claire.util.Group;
import com.claire.util.Person;
import com.claire.util.UserNode;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
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

    public int getDurationToOneDestination(Edge edge){

        int duration = 0;

        String orinalAd = edge.getUnode().getLocation();
        String destAd = edge.getInode().getLocation();
        UserNode.Mode mode = edge.getUnode().getTravelMode();
        try {
            duration = getDurationOrDistance(GoogleMapCall(orinalAd, destAd, mode), "duration");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("User:" + edge.getUnode().getName() + " Hotel:" + edge.getInode().getName() + " duration:" + duration);
        return duration;
    }
    /**
     * Get distance or duration from JSON from GoogleMap API (a group result)
     * @param resultJSON
     * @param type  distance or duration
     * @return  distance(duration) list of the group
     */
    public ArrayList<String> getDurationOrDistanceOfGroup(JSONObject resultJSON, String type){
        ArrayList<String> durationList = new ArrayList<String>();
        ArrayList<String> distanceList = new ArrayList<String>();
        JSONArray ja = resultJSON.getJSONArray("rows");

        for(int i = 0;i < ja.length();i++){
            JSONArray elements = ja.getJSONObject(i).getJSONArray("elements");

            String distance = elements.getJSONObject(0).getJSONObject("distance").getString("text");

            logger.info("distance:" + distance);

            distanceList.add(distance);

            String duration = elements.getJSONObject(0).getJSONObject("duration").getString("text");

            logger.info("duration" + duration);

            durationList.add(duration);

        }

        if(type.equals("distance")){
            return distanceList;
        }
        else{
            return durationList;
        }
    }

    /**
     * Get back distance or duration result with two single addresses
     * @param resultJSON
     * @param type
     * @return duration or distance result
     */
    public int getDurationOrDistance(JSONObject resultJSON, String type){
        int duration = 0;
        int distance = 0;

        JSONArray ja = resultJSON.getJSONArray("rows");

        for(int i = 0;i < ja.length();i++){
            JSONArray elements = ja.getJSONObject(i).getJSONArray("elements");

            distance = elements.getJSONObject(0).getJSONObject("distance").getInt("value");

            logger.info("distance:" + distance);

            duration = elements.getJSONObject(0).getJSONObject("duration").getInt("value");

            logger.info("duration" + duration);
        }

        if(type.equals("distance")){
            return distance;
        }
        else{
            return duration;
        }
    }


    /**
     * GoogleMap API call method
     * @param group
     * @return  JSONObject of GoogleMap API
     * @throws Exception
     */
    public  JSONObject GoogleMapCall(Group group, String destinationAd) throws Exception {
        //URL to get the distance
        //https://maps.googleapis.com/maps/api/distancematrix/output?parameters

        JSONObject resultJSON = new JSONObject();

        if (group == null) {
            return resultJSON;
        }

        String origin = group.makeOriginURL();
        origin = java.net.URLEncoder.encode(origin, "utf-8");

        String destination = destinationAd;
        destination = java.net.URLEncoder.encode(destination, "utf-8");

        String baseURL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=oriGPS&destinations=desGPS&language=en";
        String URL = baseURL.replaceAll("oriGPS", origin);
        URL = URL.replaceAll("desGPS", destination);

        logger.info("URL:" + URL);

        // Setup the HttpClient
        HttpClient httpclient = new DefaultHttpClient();

        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,600000);
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 600000);

        //URL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=40.7575,-73.9700&destinations=32.7974,-96.8256";

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

                resultJSON = new JSONObject(json);

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
        return resultJSON;
    }

    public  JSONObject GoogleMapCall(String originAd, String destinationAd, UserNode.Mode travelMode) throws Exception {
        //URL to get the distance
        //https://maps.googleapis.com/maps/api/distancematrix/output?parameters

        JSONObject resultJSON = new JSONObject();

        String origin = java.net.URLEncoder.encode(originAd, "utf-8");

        String destination = destinationAd;
        destination = java.net.URLEncoder.encode(destination, "utf-8");

        String tmode = travelMode.value();
        tmode = java.net.URLEncoder.encode(tmode, "utf-8");

        String baseURL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=oriGPS&destinations=desGPS&mode=tmode&language=en";
        String URL = baseURL.replaceAll("oriGPS", origin);
        URL = URL.replaceAll("desGPS", destination);
        URL = URL.replaceAll("tmode", tmode);

        logger.info("URL:" + URL);

        // Setup the HttpClient
        HttpClient httpclient = new DefaultHttpClient();

        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,600000);
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 600000);

        //URL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=40.7575,-73.9700&destinations=32.7974,-96.8256";

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

                resultJSON = new JSONObject(json);

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
        return resultJSON;
    }


    /**
     * Utility method to pass HttpResponse to get the response body
     *
     * @param response
     * @return the response body
     */
    public String returnResponseBody(HttpResponse response) {
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
        ArrayList<Person> PersonList = new ArrayList<Person>();

        Person Person1 = new Person("40.7575","-73.9700","");
        PersonList.add(Person1);
        Person Person2 = new Person("40.7596","-73.9847","");
        PersonList.add(Person2);
        Person Person3 = new Person("34.0727","-118.3729","");
        PersonList.add(Person3);
//        Person Person4 = new Person("33.4531","-111.9863");
//        PersonList.add(Person4);
//        Person Person5 = new Person("32.9515","-96.8286");
//        PersonList.add(Person5);
//        Person Person6 = new Person("32.8978","-97.0398");
//        PersonList.add(Person6);
//        Person Person7 = new Person("37.7878","-122.4087");
//        PersonList.add(Person7);

        String destination = "32.7974,-96.8256";
        Group group = new Group(PersonList);
        DurationComputing dComp = new DurationComputing();
        try {
            ArrayList<String> durationList = dComp.getDurationOrDistanceOfGroup(dComp.GoogleMapCall(group,destination),"duration");
            for(int i = 0;i < durationList.size();i++){
                logger.info("Person" + i + ":" + durationList.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
////https://maps.googleapis.com/maps/api/distancematrix/json?origins=40.7575,-73.9700|40.7596,-73.9847|34.0727,-118.3729&destinations=32.7974,-96.8256