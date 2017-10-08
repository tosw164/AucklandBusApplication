package com.example.theooswanditosw164.firstyone.dataclasses.old;

import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

/**
 * Created by theooswanditosw164 on 19/03/17.
 */

public class BusTrip {

    private String route_id, service_id, trip_id, shape_id;
    private String trip_headsign;

    public BusTrip(String route, String service, String trip,
                   String shape, String headsign){
        this.route_id = route;
        this.service_id = service;
        this.trip_id = trip;
        this.shape_id = shape;
        this.trip_headsign = headsign;
    }

    public String getRoute_id() {
        return route_id;
    }

    public String getService_id() {
        return service_id;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public String getShape_id() {
        return shape_id;
    }

    public String getTrip_headsign() {
        return trip_headsign;
    }

    @Override
    public String toString() {
        return (trip_headsign + " " + route_id + " " + trip_id);
    }
}
