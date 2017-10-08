package com.tosw164.busapp.atapi;

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