package com.example.theooswanditosw164.firstyone;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.theooswanditosw164.firstyone.atapi.ATapiCall;
import com.example.theooswanditosw164.firstyone.atapi.AtApiRequests;
import com.example.theooswanditosw164.firstyone.miscmessages.ToastMessage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RouteOnMap extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private TextView title_text;
    private LocationManager location_manager;
    private String trip_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_on_map);

        String textview_stopnumber = "NA";

        Bundle intent_extras = getIntent().getExtras();
        if (intent_extras != null){
            textview_stopnumber = intent_extras.getString("StopNumber");
            trip_id = intent_extras.getString("TripID");
        }

        SupportMapFragment map_fragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.route_on_map_map);
        map_fragment.getMapAsync(this);

        moveToCurrentLocation();
        new TestRouteWorker().execute(trip_id);

        title_text = (TextView)findViewById(R.id.route_on_map_textview);
        title_text.setText("Route for stop " + textview_stopnumber);

        location_manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true); //TODO make safe

    }

    private void moveToCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        Location location = location_manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        System.out.println(location);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Toast.makeText(getBaseContext(), "lat:" + latitude + " lng:" + longitude, Toast.LENGTH_SHORT).show();

        LatLng map_centre = new LatLng(latitude, longitude);
        CameraPosition cam_position = CameraPosition.builder().target(map_centre).zoom(15).build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cam_position), 1500, null);
    }

    class TestRouteWorker extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
//            final String url_to_use = "https://api.at.govt.nz/v2/gtfs/shapes/tripId/" + params[0];

            return AtApiRequests.getRouteShapeDataFromTripID(getBaseContext(), params[0]);
//            return ATapiCall.fetchJSONfromURLwithSubKey(getBaseContext(), url_to_use);
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

                    map.addPolyline(route_line_options);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            ToastMessage.makeToast(getBaseContext(), "done");

        }
    }
}
