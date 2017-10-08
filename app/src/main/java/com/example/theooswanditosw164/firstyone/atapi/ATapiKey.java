package com.example.theooswanditosw164.firstyone.atapi;

/**
 * Created by TheoOswandi on 5/09/2017.
 */

public class ATapiKey {
    private static final String key = >>INSERT KEY HERE<<;
    private static final String realtime_key = >>INSERT REALTIME KEY HERE<<;

    public static String getKey(){
        return key;
    }
    public static String getRealtimeKey() { return realtime_key; }
}