package com.tosw164.busapp.dataclasses.old;

/**
 * Created by theooswanditosw164 on 19/03/17.
 */

public class TripArrivalTime {

    private String trip_id;
    private int stop_number;
    private String arrival_time;

    public TripArrivalTime(String id, int stop, String arrival){
        this.trip_id = id;
        this.stop_number = stop;
        this.arrival_time = arrival;
    }

    public int getStopNumber() {
        return stop_number;
    }

    public String getArrival_time() {
        return arrival_time;
    }
}
