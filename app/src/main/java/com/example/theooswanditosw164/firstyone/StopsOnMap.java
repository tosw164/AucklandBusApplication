package com.example.theooswanditosw164.firstyone;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.theooswanditosw164.firstyone.atapi.AtApiRequests;
import com.example.theooswanditosw164.firstyone.miscmessages.ToastMessage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by TheoOswandi on 5/09/2017.
 */

public class StopsOnMap extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {
    private GoogleMap google_map;
    private LocationManager location_manager;
    private final int MY_PERMISSION_ACCESS_LOCATION = 99;

    Button button1, button2;
    FloatingActionButton fab1, fab2, fab3;

    ArrayList<Marker> list_of_markers;
    HashMap<String, Marker> map_of_markers_by_id;

    private static final String TAG = StopsOnMap.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops_on_map);

        button1 = (Button) findViewById(R.id.stopsonmap_button1);
        button1.setOnClickListener(this);
        button1.setText("but1");

        button2 = (Button) findViewById(R.id.stopsonmap_button2);
        button2.setOnClickListener(this);
        button2.setText("but2");

        fab1 = (FloatingActionButton) findViewById(R.id.stopsonmap_fab1);
        fab1.setOnClickListener(this);

        fab2 = (FloatingActionButton) findViewById(R.id.stopsonmap_fab2);
        fab2.setOnClickListener(this);

        fab3 = (FloatingActionButton) findViewById(R.id.stopsonmap_fab3);
        fab3.setOnClickListener(this);

        location_manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_LOCATION);
        }

        SupportMapFragment map_fragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        map_fragment.getMapAsync(this);


    }

    private boolean isLocationOn() {
        return location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                location_manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        google_map = googleMap;

        //Add button to go to current location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        google_map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.i(TAG, marker.getSnippet());
            }
        });

        Location my_location = location_manager.getLastKnownLocation(location_manager.getBestProvider(new Criteria(), true));

        if (isLocationOn() && my_location != null) {
            google_map.setMyLocationEnabled(true); //TODO make sure this is safe

            //https://stackoverflow.com/questions/14441653/how-can-i-let-google-maps-api-v2-go-directly-to-my-location
//            Location my_location = location_manager.getLastKnownLocation(location_manager.getBestProvider(new Criteria(), true));
            LatLng my_latlng = new LatLng(my_location.getLatitude(), my_location.getLongitude());
            google_map.moveCamera(CameraUpdateFactory.newLatLngZoom(my_latlng, 15));

            new SomeAsyncTask().execute(my_latlng);
        } else {
            //Hardcoded LatLng of Auckland from googling "Auckland latlng"
            LatLng hardcoded_latlng = new LatLng(-36.843864, 174.766438);
            new SomeAsyncTask().execute(hardcoded_latlng);
            google_map.moveCamera(CameraUpdateFactory.newLatLngZoom(hardcoded_latlng, 15));
        }

    }



    class SomeAsyncTask extends AsyncTask<LatLng, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(LatLng... params) {
            LatLng my_location = params[0];
            return AtApiRequests.getStopsByLocation(getBaseContext(), my_location.latitude, my_location.longitude, 1000.0);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            populateMapWithStops(json);
        }
    }

    private void populateMapWithStops(JSONObject json){
        list_of_markers = new ArrayList<Marker>();
        map_of_markers_by_id = new HashMap<>();
        Marker marker_to_add;

        try {
            if (json.getString("status").equals("OK")){
                JSONArray responses_array = json.getJSONArray("response");

                if (responses_array.length() == 0){
                    ToastMessage.makeToast(getBaseContext(), "Nothing returned");
                } else {
                    String short_name, stop_id;
                    Double stop_lat, stop_lng;
                    for (int i = 0; i < responses_array.length(); i++){
                        JSONObject stop_json = responses_array.getJSONObject(i);

                        short_name = stop_json.getString("stop_name");
                        stop_id = stop_json.getString("stop_id");
                        stop_lat = stop_json.getDouble("stop_lat");
                        stop_lng = stop_json.getDouble("stop_lon");

                        marker_to_add = google_map.addMarker(new MarkerOptions().position(new LatLng(stop_lat, stop_lng)).title(stop_id).snippet(short_name));
                        list_of_markers.add(marker_to_add);
                        map_of_markers_by_id.put(stop_id, marker_to_add);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").snippet("This is a snippet"));
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.stopsonmap_button1:
                Log.i(TAG, "button1");
                break;
            case R.id.stopsonmap_button2:
                Log.i(TAG, "button2");
                break;
            case R.id.stopsonmap_fab1:
                Log.i(TAG, "fab1");
                break;
            case R.id.stopsonmap_fab2:
                Log.i(TAG, "fab2");
                break;
            case R.id.stopsonmap_fab3:
                Log.i(TAG, "fab3");
                break;
            default:
                Log.i(TAG, "default");
                break;
        }
    }


}

