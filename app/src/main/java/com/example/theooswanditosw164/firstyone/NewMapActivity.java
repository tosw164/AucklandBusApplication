package com.example.theooswanditosw164.firstyone;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.theooswanditosw164.firstyone.atapi.ATapiCall;
import com.example.theooswanditosw164.firstyone.miscmessages.ToastMessage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class NewMapActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {
    private GoogleMap map;
    Button addline_button, toast_button, current_location_button, test_route_button;
    Random rnd;
    LocationManager location_manager;
    Polyline route_line;

    private static final int MY_PERMISSION_ACCESS_LOCATION = 0;
    private static Marker CURRENT_LOCATION_MARKER = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        rnd = new Random();

        addline_button = (Button) findViewById(R.id.randomline_button);
        addline_button.setOnClickListener(this);

        toast_button = (Button) findViewById(R.id.toast_button);
        toast_button.setOnClickListener(this);

        current_location_button = (Button) findViewById(R.id.current_location_button);
        current_location_button.setOnClickListener(this);

        test_route_button = (Button)findViewById(R.id.attest_test_route_button);
        test_route_button.setOnClickListener(this);

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
            return;
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").snippet("This is a snippet"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        map.setMyLocationEnabled(true);

    }

    /**
     * Creates a marker at current GPS location, deletes old one if there is one
     * @param latlng coordinate for new marker
     */
    private void createLocationMarker(LatLng latlng){
        if (CURRENT_LOCATION_MARKER != null) {
            CURRENT_LOCATION_MARKER.remove();
        }
        map.addMarker(new MarkerOptions().position(latlng).title("Current Location"));
    }

    /**
     * Method that creates a line on map based from 4 given coordinates (2 latlng pairs)
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     */
    private void addLine(double lat1, double lng1, double lat2, double lng2) {
        map.addPolyline(new PolylineOptions().geodesic(true)
                .add(new LatLng(lat1, lng1))
                .add(new LatLng(lat2, lng2)));
    }

    /**
     * @return TRUE if both network and GPS enabled
     */
    private boolean isLocationOn() {
        return location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                location_manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void moveToCurrentLocation() {
//        Toast.makeText(getApplicationContext(), "HelloWorld", Toast.LENGTH_LONG).show();

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
        Location location = location_manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        System.out.println(location);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Toast.makeText(getBaseContext(), "lat:" + latitude + " lng:" + longitude, Toast.LENGTH_SHORT).show();

        LatLng map_centre = new LatLng(latitude, longitude);
        CameraPosition cam_position = CameraPosition.builder().target(map_centre).zoom(15).build();
        createLocationMarker(map_centre);

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cam_position), 1500, null);


    }

    class TestRouteWorker extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            final String url_to_use = "https://api.at.govt.nz/v2/gtfs/shapes/tripId/12850045812-20170314155338_v52.16";

            return ATapiCall.fetchJSONfromURL(getBaseContext(), url_to_use);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json == null){
                Toast.makeText(getBaseContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
                return;
            }

            PolylineOptions route_line_options = new PolylineOptions();

//TODO do one polyline - Sam
            try{
                if (json.get("status").equals("OK")){
                    JSONArray responses_array = json.getJSONArray("response");

                    for (int i = 0; i< responses_array.length(); i++ ){
                        JSONObject current_obj = responses_array.getJSONObject(i);

                        double lat_value = current_obj.getDouble("shape_pt_lat");
                        double lng_value = current_obj.getDouble("shape_pt_lon");

                        route_line_options.add(new LatLng(lat_value, lng_value));

                    }

                    route_line = map.addPolyline(route_line_options);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            ToastMessage.makeToast(getBaseContext(), "done");

        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.randomline_button:
                int lat_min = -90, lng_min = -180;
                int lat1 = (rnd.nextInt() % 180) - lat_min;
                int lat2 = (rnd.nextInt() % 180) - lat_min;

                int lng1 = (rnd.nextInt() % 360) - lng_min;
                int lng2 = (rnd.nextInt() % 360) - lng_min;

                addLine(lat1, lng1, lat2, lng2);
                break;

            case R.id.toast_button:
                if (isLocationOn()) {
                    Toast.makeText(getBaseContext(), "Location On", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getBaseContext(), "Location Off", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.current_location_button:
//                map.getMyLocation();
                moveToCurrentLocation();
                break;

            case R.id.attest_test_route_button:
                new TestRouteWorker().execute();
                System.out.println("testroute");
                break;
        }

    }



}
