package com.tosw164.busapp.dataclasses;

/**
 * Created by TheoOswandi on 27/06/2018.
 */

public class RealtimeBusInformationRow {
    public String short_name;
    public String destination_display;
    public String scheduled_time;
    public String expected_time;

    public RealtimeBusInformationRow(String shortname, String destinationdisplay, String scheduledtime){
        this(shortname, destinationdisplay, scheduledtime, "");
    }

    public RealtimeBusInformationRow(String shortname, String destinationdisplay, String scheduledtime, String expectedtime){
        short_name = shortname;
        destination_display = destinationdisplay;
        scheduled_time = scheduledtime;
        expected_time = expectedtime;
    }
}
