package com.example.theooswanditosw164.firstyone.atapi;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by theooswanditosw164 on 5/05/17.
 */

public class AtApiRequests extends ATapiCall{

//    public static ArrayList<String> printAllTrips(Context c){
//        ArrayList<String> list_to_return = new ArrayList<String>();
//
//        new AllTripsWorker().execute(c);
//        try{
//            if (json_output.get("status").equals("OK")){
//                JSONArray responses = json_output.getJSONArray("response");
//
//                JSONObject obj;
//                for(int i = 0; i < responses.length(); i++){
//                    obj = responses.getJSONObject(i);
//
//
//                }
//
//            }
//
//        } catch (JSONException e){
//            e.printStackTrace();
//        }
//        return list_to_return;
//    }

    public static JSONObject getAllTrips(Context c){
        String url_to_use =  "https://api.at.govt.nz/v2/gtfs/trips";
        return fetchJSONfromURLwithSubKey(c, url_to_use);
    }

    class AllTripsWorker extends AsyncTask<Context, Void, JSONObject>{

        @Override
        protected JSONObject doInBackground(Context... params) {
            String url_to_use = "https://api.at.govt.nz/v2/gtfs/trips";
            return ATapiCall.fetchJSONfromURLwithSubKey(params[0], url_to_use);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json == null){
                System.out.println("NO");
//                Toast.makeText(getBaseContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
                return;
            }
            try{
            if (json.get("status").equals("OK")){
                JSONArray responses = json.getJSONArray("response");

                JSONObject obj;
                for(int i = 0; i < responses.length(); i++){
                    obj = responses.getJSONObject(i);


                }

            }

        } catch (JSONException e){
            e.printStackTrace();
        }

        }


    }

}
