package com.example.theooswanditosw164.firstyone.dataclasses;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by TheoOswandi on 8/09/2017.
 */

public class MarkerClusterItem implements ClusterItem {
    private LatLng item_position;
    private String title;
    private String snippet;

    public MarkerClusterItem(String title, String snippet, LatLng latlng){
        this.item_position = latlng;
        this.title = title;
        this.snippet = snippet;
    }

    public MarkerClusterItem(LatLng latlng){
        this.item_position = latlng;
        this.title = null;
        this.snippet = null;
    }

    @Override
    public LatLng getPosition() {
        return item_position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }
}
