package com.example.theooswanditosw164.firstyone.dataclasses;

/**
 * Created by TheoOswandi on 13/09/2017.
 */

public class FavouriteStop {

    String stop_number;
    String custom_name;

    public FavouriteStop(){
        stop_number = null;
        custom_name = null;
    }

    public FavouriteStop(String num, String name){
        stop_number = num;
        custom_name = name;
    }

    public void setStopNumber(String stop_number) {
        this.stop_number = stop_number;
    }

    public void setCustomName(String custom_name) {
        this.custom_name = custom_name;
    }

    public String getStopNumber() {
        return stop_number;
    }

    public String getCustomName() {
        return custom_name;
    }

}
