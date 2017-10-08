package com.tosw164.busapp.dataclasses.old;

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

    public String getScheduledArrivalTime() {
        return scheduled_arrival_time;
    }

    public String getEstimatedArrivalTime() {
        return estimated_arrival_time;
    }

    public String getRouteShortName() {
        return route_short_name;
    }

    public String getDestinationDisplay() {
        return destination_display;
    }
}
