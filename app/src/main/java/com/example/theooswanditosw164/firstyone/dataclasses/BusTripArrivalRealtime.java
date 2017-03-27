package com.example.theooswanditosw164.firstyone.dataclasses;

/**
 * Created by theooswanditosw164 on 27/03/17.
 */

public class BusTripArrivalRealtime {
    private String scheduled_arrival_time;
    private String estimated_arrival_time;
    private String route_short_name;
    private String destination_display;

    public BusTripArrivalRealtime(String sched_arrtime,
                                  String dest_disp,
                                  String shortname,
                                  String est_arrtime){
        this.scheduled_arrival_time = sched_arrtime;
        this.estimated_arrival_time = est_arrtime;
        this.route_short_name = shortname;
        this.destination_display = dest_disp;
    }

    public void setScheduledArrivalTime(String scheduled_arrival_time) {
        this.scheduled_arrival_time = scheduled_arrival_time;
    }

    public void setEstimatedArrivalTime(String estimated_arrival_time) {
        this.estimated_arrival_time = estimated_arrival_time;
    }

    public void setRouteShortName(String route_short_name) {
        this.route_short_name = route_short_name;
    }

    public void setDestinationDisplay(String destination_display) {
        this.destination_display = destination_display;
    }
}
