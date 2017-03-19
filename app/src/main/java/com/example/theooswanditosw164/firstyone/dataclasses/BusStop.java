package com.example.theooswanditosw164.firstyone.dataclasses;

import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

/**
 * Created by theooswanditosw164 on 19/03/17.
 */

public class BusStop {
    private int stop_number;
    private LatLng location;
    public Map<String, BusRoute> routes_list;

    public BusStop(int num, LatLng latlng){
        this.stop_number = num;
        this.location = latlng;
    }

    public int getStop_number(){
        return stop_number;
    }

    public LatLng getLocation(){
        return location;
    }

}
