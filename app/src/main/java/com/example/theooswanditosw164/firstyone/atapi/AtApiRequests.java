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

    public static JSONObject getAllStops(Context c){
        String url_to_use = "https://api.at.govt.nz/v2/gtfs/stops";
        return fetchJSONfromURLwithSubKey(c, url_to_use);
    }

    public static JSONObject getRoutesInformationFromStopNumber(Context c, String stop_number){
        String url_to_use = "https://api.at.govt.nz/v2/gtfs/routes/stopid/" + stop_number;
        return fetchJSONfromURLwithSubKey(c, url_to_use);
    }

    public static JSONObject getTimetableInformaitonFromStopNumber(Context c, String stop_number){
        String url_to_use = "https://api.at.govt.nz/v2/gtfs/stops/stopinfo/" + stop_number;
        return fetchJSONfromURLwithSubKey(c, url_to_use);
    }

    public static JSONObject getRouteShapeDataFromTripID(Context c, String trip_id){
        String url_to_use = "https://api.at.govt.nz/v2/gtfs/shapes/tripId/" + trip_id;
        return fetchJSONfromURLwithSubKey(c, url_to_use);
    }

    public static JSONObject getRealtimeTimetableFromStopNumber(Context c, String stop_number, String hours){
        String url_to_use = "https://api.at.govt.nz/v2/public-restricted/" +
                "departures/" + stop_number + "?" +
                "subscription-key=" + ATapiKey.getRealtimeKey() +
                "&hours=" + hours;
        return fetchJSONfromURL(c, url_to_use);
    }

    public static JSONObject getStopsByLocation(Context c, Double lat, Double lng, Double radius){
        String url_to_use = "https://api.at.govt.nz/v2/gtfs/stops/geosearch?" +
                "lat=" + lat +
                "&lng=" + lng +
                "&distance=" + radius;
        return fetchJSONfromURLwithSubKey(c, url_to_use);

    }
}
