package com.example.theooswanditosw164.firstyone.dataclasses;

import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Created by theooswanditosw164 on 19/03/17.
 */

public class MapContainers {

    private static MapContainers instance = null;

    //route_id --> BusRoute
    public HashMap<String, BusRoute> route_id_BusRoute_link;

    //trip_id --> route_id
    public HashMap<String, String> trip_id_route_id_link;


    private MapContainers(){

    }

    public static MapContainers getInstance(){
        if (instance == null){
            instance = new MapContainers();
        }
        return instance;
    }
}
