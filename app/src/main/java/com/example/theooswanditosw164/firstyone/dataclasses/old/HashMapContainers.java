package com.example.theooswanditosw164.firstyone.dataclasses.old;

import android.content.Context;
import android.os.AsyncTask;

import com.example.theooswanditosw164.firstyone.atapi.ATapiCall;
import com.example.theooswanditosw164.firstyone.miscmessages.ToastMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by theooswanditosw164 on 19/03/17.
 */

public class HashMapContainers {

//FIELDS

    private static HashMapContainers instance = null;

    public HashMap<String, BusTrip> route_id_BusTrip_link;
    public HashMap<String, BusTrip> service_id_BusTrip_link;
    public HashMap<String, BusTrip> trip_id_BusTrip_link;
    public HashMap<String, BusTrip> shape_id_BusTrip_link;

    //route_id --> BusRoute
    public HashMap<String, BusRoute> route_id_BusRoute_link;

//METHODS

    public static HashMapContainers getInstance(Context c){
        if (instance == null){
            instance = new HashMapContainers(c);
        }
        return instance;
    }

    private HashMapContainers(Context c){

        //Initialise/Reset hashmap
        route_id_BusRoute_link = new HashMap<String, BusRoute>();

        route_id_BusTrip_link = new HashMap<String, BusTrip>();
        service_id_BusTrip_link = new HashMap<String, BusTrip>();
        trip_id_BusTrip_link = new HashMap<String, BusTrip>();
        shape_id_BusTrip_link = new HashMap<String, BusTrip>();

        new PopulateWorker().execute(c);

    }

    private class PopulateWorker extends AsyncTask<Context, Void, JSONObject>{

        @Override
        protected JSONObject doInBackground(Context... params) {
            populateBusRoutes(params[0]);
            populateBusTrips(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            System.out.println("FINISH");
        }
    }


    public void test(){
        for (String k: route_id_BusRoute_link.keySet()){
            System.out.println(route_id_BusRoute_link.get(k).toString());
        }
    }

    private void populateBusTrips(Context c) {

        try {
            JSONObject all_trips = ATapiCall.fetchJSONfromURLwithSubKey(c, "https://api.at.govt.nz/v2/gtfs/trips");

            if (all_trips == null){
                ToastMessage.makeToast(c, "Could not connect to server");
            }

            JSONArray responses = all_trips.getJSONArray("response");

            for (int i = 0; i < responses.length(); i++){

                JSONObject current_obj = responses.getJSONObject(i);

                String route_id = current_obj.getString("route_id");
                String service_id = current_obj.getString("service_id");
                String trip_id = current_obj.getString("trip_id");
                String shape_id = current_obj.getString("shape_id");

                BusTrip trip_object = new BusTrip(route_id, service_id, trip_id, shape_id,
                        current_obj.getString("trip_headsign"));

                route_id_BusTrip_link.put(route_id, trip_object);
                service_id_BusTrip_link.put(service_id, trip_object);
                trip_id_BusTrip_link.put(trip_id, trip_object);
                shape_id_BusTrip_link.put(shape_id, trip_object);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void populateBusRoutes(Context c) {
        try {
            JSONObject all_trips = ATapiCall.fetchJSONfromURLwithSubKey(c, "https://api.at.govt.nz/v2/gtfs/routes");

            if (all_trips == null){
                ToastMessage.makeToast(c, "Could not connect to server");
            }

            JSONArray responses = all_trips.getJSONArray("response");

            for (int i = 0; i < responses.length(); i++){

                JSONObject current_obj = responses.getJSONObject(i);

                String route_id = current_obj.getString("route_id");

                BusRoute route_object = new BusRoute(route_id,
                        current_obj.getString("route_short_name"),
                        current_obj.getString("route_long_name"));

                route_id_BusRoute_link.put(route_id, route_object);

//                String route_id = current_obj.getString("route_id");
//                String service_id = current_obj.getString("service_id");
//                String trip_id = current_obj.getString("trip_id");
//                String shape_id = current_obj.getString("shape_id");
//
//                BusTrip trip_object = new BusTrip(route_id, service_id, trip_id, shape_id,
//                        current_obj.getString("trip_headsign"));
//
//                route_id_BusTrip_link.put(route_id, trip_object);
//                service_id_BusTrip_link.put(service_id, trip_object);
//                trip_id_BusTrip_link.put(trip_id, trip_object);
//                shape_id_BusTrip_link.put(shape_id, trip_object);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
