package com.example.theooswanditosw164.firstyone.dataclasses;

/**
 * Created by theooswanditosw164 on 19/03/17.
 */

public class BusRoute {

    private String short_name, long_name, route_id;


    public BusRoute(String id, String s_name, String l_name){
        this.route_id = id;
        this.short_name = s_name;
        this.long_name = l_name;
    }

    public String getShort_name() {
        return short_name;
    }

    public String getLong_name() {
        return long_name;
    }

    public String getRoute_id() { return route_id; }

    @Override
    public String toString() {
        return (short_name + " " + route_id + " " + long_name);
    }
}
