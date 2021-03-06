package com.tosw164.busapp.atapi;

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by theooswanditosw164 on 19/03/17.
 */

public abstract class ATapiCall {
    public static JSONObject fetchJSONfromURLwithSubKey(Context contxt, String url_input){
        HttpURLConnection url_connection = null;
        try{
            URL url = new URL(url_input);
            url_connection = (HttpURLConnection)url.openConnection();
            url_connection.setRequestProperty("Ocp-Apim-Subscription-Key", ATapiKey.getKey());

            // Request not successful
            if (url_connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Request Failed. HTTP Error Code: " + url_connection.getResponseCode());
            }

            // Read response
            BufferedReader br = new BufferedReader(new InputStreamReader(url_connection.getInputStream()));

            StringBuffer jsonString = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
                System.out.println("LINE:" + line);
            }
            br.close();
            url_connection.disconnect();

            JSONObject json = new JSONObject(jsonString.toString());

            return json;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject fetchJSONfromURL(Context contxt, String url_input){
        HttpURLConnection url_connection = null;
        try{
            URL url = new URL(url_input);
            url_connection = (HttpURLConnection)url.openConnection();

            // Request not successful
            if (url_connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Request Failed. HTTP Error Code: " + url_connection.getResponseCode());
            }

            // Read response
            BufferedReader br = new BufferedReader(new InputStreamReader(url_connection.getInputStream()));

            StringBuffer jsonString = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
                System.out.println("LINE:" + line);
            }
            br.close();
            url_connection.disconnect();

            JSONObject json = new JSONObject(jsonString.toString());

            return json;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static JSONObject fetchJSON
}
