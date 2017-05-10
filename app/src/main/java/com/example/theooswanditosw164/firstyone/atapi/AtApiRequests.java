package com.example.theooswanditosw164.firstyone.atapi;

import android.content.Context;
import org.json.JSONObject;

/**
 * Created by theooswanditosw164 on 5/05/17.
 */

public class AtApiRequests extends ATapiCall{
    public static JSONObject getAllTrips(Context c){
        String url_to_use =  "https://api.at.govt.nz/v2/gtfs/trips";
        return fetchJSONfromURLwithSubKey(c, url_to_use);
    }

    public static JSONObject getRoutesInformationFromStopNumber(Context c, int stop_number){
        String url_to_use = "https://api.at.govt.nz/v2/gtfs/routes/stopid/" + stop_number;
        return fetchJSONfromURLwithSubKey(c, url_to_use);
    }

    public static JSONObject getTimetableInformaitonFromStopNumber(Context c, int stop_number){
        String url_to_use = "https://api.at.govt.nz/v2/gtfs/stops/stopinfo/" + stop_number;
        return fetchJSONfromURLwithSubKey(c, url_to_use);
    }

    public static JSONObject getRouteLocationDataFromTripID(Context c, String trip_id){
        String url_to_use = "https://api.at.govt.nz/v2/gtfs/shapes/tripId/" + trip_id;
        return fetchJSONfromURLwithSubKey(c, url_to_use);
    }
}
