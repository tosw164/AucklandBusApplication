package com.example.theooswanditosw164.firstyone.dataclasses;

/**
 * Created by TheoOswandi on 8/09/2017.
 */

public class BusStop {
    private String stop_id, short_name;
    private Double lat, lng;

    public BusStop(){
        this.stop_id = null;
        this.short_name = null;
        this.lat = null;
        this.lng = null;
    }

    public BusStop(String id, String name, Double lat, Double lng){
        this.stop_id = id;
        this.short_name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public String getStopId() {
        return stop_id;
    }
    public String getShortName() {
        return short_name;
    }
    public Double getLat() { return lat; }
    public Double getLng() { return lng; }


    public void setStopId(String stop_id) {
        this.stop_id = stop_id;
    }

    public void setShortName(String short_name) {
        this.short_name = short_name;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    //    @Override
//    public String toString() {
//        return (short_name + " " + route_id + " " + long_name);
//    }
}
