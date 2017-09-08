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
import android.widget.LinearLayout;

import com.example.theooswanditosw164.firstyone.atapi.AtApiRequests;
import com.example.theooswanditosw164.firstyone.dataclasses.BusStop;
import com.example.theooswanditosw164.firstyone.dataclasses.SqliteTransportDatabase;
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
    LinearLayout fab_container1, fab_container2, fab_container3;
    FloatingActionButton main_fab, menu_fab1, menu_fab2, menufab3;

    ArrayList<Marker> list_of_markers;
    HashMap<String, Marker> map_of_markers_by_id;

    private boolean fab_menu_open;

    private static final String TAG = StopsOnMap.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops_on_map);

        setupComponents();

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

    /**
     * Sets up components used in this activity
     */
    private void setupComponents(){
        button1 = (Button) findViewById(R.id.stopsonmap_button1);
        button1.setOnClickListener(this);
        button1.setText("but1");

        button2 = (Button) findViewById(R.id.stopsonmap_button2);
        button2.setOnClickListener(this);
        button2.setText("but2");

        //Logic for fab menu
//        https://stackoverflow.com/questions/30699302/android-design-support-library-fab-menu

        //Initialise main button and menu buttons and register listeners for each
        main_fab = (FloatingActionButton) findViewById(R.id.stopsonmap_mainFAB);
        main_fab.setOnClickListener(this);

        menu_fab1 = (FloatingActionButton) findViewById(R.id.stopsonmap_FABmenu1);
        menu_fab1.setOnClickListener(this);

        menu_fab2 = (FloatingActionButton) findViewById(R.id.stopsonmap_FABmenu2);
        menu_fab2.setOnClickListener(this);

        menufab3 = (FloatingActionButton) findViewById(R.id.stopsonmap_FABmenu3);
        menufab3.setOnClickListener(this);

        //Initialise FAB menu item containers and sets them to invisible/GONE
        fab_container2 = (LinearLayout) findViewById(R.id.stopsonmap_FABContainer1);
        fab_container2.setVisibility(View.GONE);

        fab_container1 = (LinearLayout) findViewById(R.id.stopsonmap_FABContainer2);
        fab_container1.setVisibility(View.GONE);

        fab_container3 = (LinearLayout) findViewById(R.id.stopsonmap_FABContainer3);
        fab_container3.setVisibility(View.GONE);

        fab_menu_open = false;

    }

    /**
     * Checks if either GPS or Network enabled so can poll location
     * @return
     */
    private boolean isLocationOn() {
        return location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                location_manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        google_map = googleMap;
        google_map.getUiSettings().setMapToolbarEnabled(false);

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
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void populateDB(){
        new getStopsWorker().execute();
    }

    class getStopsWorker extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... params) {
            return AtApiRequests.getAllStops(getBaseContext());
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            printAllStops(json);
        }
    }

    private void printAllStops(JSONObject json){
        try {
            if (json.getString("status").equals("OK")){
                JSONArray responses_array = json.getJSONArray("response");

                if (responses_array.length() == 0){
                    ToastMessage.makeToast(getBaseContext(), "Nothing returned");
                } else {
                    String short_name, stop_id;
                    Double stop_lat, stop_lng;

                    SqliteTransportDatabase db = new SqliteTransportDatabase(getBaseContext());
                    db.printColumnNames();
                    for (int i = 0; i < responses_array.length(); i++){
                        JSONObject stop_json = responses_array.getJSONObject(i);

                        short_name = stop_json.getString("stop_name");
                        stop_id = stop_json.getString("stop_id");
                        stop_lat = stop_json.getDouble("stop_lat");
                        stop_lng = stop_json.getDouble("stop_lon");

                        db.createStop(stop_id, short_name, stop_lat, stop_lng);
                    }

                    Log.i(TAG, "finished adding");
                    for (BusStop b: db.getAllStops()){
                        System.out.println(b.toString());
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void printFromDB(){
        SqliteTransportDatabase db = new SqliteTransportDatabase(getBaseContext());
        for (BusStop b: db.getAllStops()){
            System.out.println(b.toString());
        }

        System.out.println(db.countStops() + "DOne");
    }

    private void upgradeDB(){
        SqliteTransportDatabase db = new SqliteTransportDatabase(getBaseContext());
        db.forceUpgrade();
//        Log.i(TAG, "rows: " + db.countStops());
        System.out.println("DOne");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.stopsonmap_button1:
                Log.i(TAG, "button1");
                populateDB();
                break;
            case R.id.stopsonmap_button2:
                Log.i(TAG, "button2");
                printFromDB();
                break;
            case R.id.stopsonmap_FABmenu1:
                Log.i(TAG, "menu_fab2");
                break;
            case R.id.stopsonmap_FABmenu2:
                Log.i(TAG, "menu_fab1");
                break;
            case R.id.stopsonmap_FABmenu3:
                Log.i(TAG, "menu fab 3");
                upgradeDB();
                break;
            case R.id.stopsonmap_mainFAB:
                Log.i(TAG, "MainFab");
                if (!fab_menu_open){
                    //open fab menu
                    animateFloatingActionMenuOpen();
                } else {
                    //close fab menu
                    animateFloatingActionMenuClose();
                }
                break;
            default:
                Log.i(TAG, "default");
                break;
        }
    }

    /**
     * Animates opening of floating action menu
     */
    private void animateFloatingActionMenuOpen(){
        fab_menu_open = true;   //set flag

        //Sets all the containers to visible from previous GONE state
        fab_container1.setVisibility(View.VISIBLE);
        fab_container2.setVisibility(View.VISIBLE);
        fab_container3.setVisibility(View.VISIBLE);

        //Rotate main button to make + into a X
        main_fab.animate().rotationBy(45);

        //Initialise offset values
        float base_translate = getResources().getDimension(R.dimen.fab_menu_translate_base);
        float translate_unit = getResources().getDimension(R.dimen.fab_menu_translate_unit);

        //Animate floating action menu buttons to respective positions based on constants above
        fab_container1.animate().translationY(-1 * (base_translate + translate_unit));
        fab_container2.animate().translationY(-1 * (base_translate + 2*translate_unit));
        fab_container3.animate().translationY(-1 * (base_translate + 3* translate_unit));
    }

    /**
     * Animate the closing of action menu
     */
    private void animateFloatingActionMenuClose(){
        fab_menu_open = false;      //set flag
        main_fab.animate().rotationBy(-45); //Make Icon + again from x

        //Return containers to behind main action button
        fab_container1.animate().translationY(0);
        fab_container2.animate().translationY(0);
        fab_container3.animate().translationY(0);

        //Hide containers to prevent accidental press and label from showing.
        fab_container1.setVisibility(View.GONE);
        fab_container2.setVisibility(View.GONE);
        fab_container3.setVisibility(View.GONE);
    }


}

