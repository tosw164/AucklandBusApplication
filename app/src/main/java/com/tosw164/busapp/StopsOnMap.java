package com.tosw164.busapp;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tosw164.busapp.atapi.AtApiDatabaseRequests;
import com.tosw164.busapp.dataclasses.ActivitySwitchContainer;
import com.tosw164.busapp.dataclasses.BusStop;
import com.tosw164.busapp.dataclasses.SqliteTransportDatabase;
import com.tosw164.busapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by TheoOswandi on 5/09/2017.
 */

public class StopsOnMap extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private GoogleMap google_map;
    private LocationManager location_manager;
    private final int MY_PERMISSION_ACCESS_LOCATION = 99;

    Button button1;
    LinearLayout fab_container1, fab_container2, fab_container3;
    FloatingActionButton main_fab, menu_fab1, menu_fab2, menufab3;
    Toolbar toolbar;

    List<BusStop> all_stops;
    ConcurrentHashMap<String, Marker> all_markers;

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

        //Change the Mylocation button icon in map view
        ImageView location_button = (ImageView) map_fragment.getView().findViewById(2);
        location_button.setImageResource(R.drawable.ic_gps_fixed_black_48dp);

        map_fragment.getMapAsync(this);
    }

    /**
     * Sets up components used in this activity
     */
    private void setupComponents() {
        button1 = (Button) findViewById(R.id.stopsonmap_button1);
        button1.setOnClickListener(this);
        button1.setText("Repopulate Stop Table");

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

        toolbar = (Toolbar) findViewById(R.id.stopsonmap_toolbar);
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

        //Adds all stops from DB to list and initialise hashmap for displaying markers
        initialiseStops();

        //Logic and listener for when user moves camera position
        google_map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.i(TAG, "idle");
                checkZoomAndAddMarkers();
            }
        });

        //Logic for when user presses info window after selecting marker
        google_map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.i(TAG, marker.getTitle() + " " + marker.getSnippet());
                HashMap<String, String> stopnumber = new HashMap<String, String>();
                stopnumber.put("stop_number", marker.getTitle());
                ChangeActivity.launchIntent(new ActivitySwitchContainer(stopnumber, getBaseContext(), "RealtimeBoardStop"));
            }
        });

        //Gets last known location
        Location my_location = getMyLocation();

        //Moves camera based on if location is enabled and something is cached or not
        if (isLocationOn()) {
            google_map.setMyLocationEnabled(true); //TODO make sure this is safe
        }

        if (my_location != null){ //location in cache
            //https://stackoverflow.com/questions/14441653/how-can-i-let-google-maps-api-v2-go-directly-to-my-location
            LatLng my_latlng = new LatLng(my_location.getLatitude(), my_location.getLongitude());
            google_map.moveCamera(CameraUpdateFactory.newLatLngZoom(my_latlng, 17));
        }else {
            //Hardcoded LatLng of Auckland from googling "Auckland latlng"
            LatLng hardcoded_latlng = new LatLng(-36.843864, 174.766438);
            google_map.moveCamera(CameraUpdateFactory.newLatLngZoom(hardcoded_latlng, 17));
        }
    }

    private Location getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        return location_manager.getLastKnownLocation(location_manager.getBestProvider(new Criteria(), true));
    }

    /**
     * Method that hides/shows markers based on current map zoom level
     */
    private void checkZoomAndAddMarkers(){
        if (google_map.getCameraPosition().zoom > 15){
            addMarkersToMap(all_stops);
        } else {
            //Clear google map
            for (String id: all_markers.keySet()){
                all_markers.get(id).remove();
                all_markers.remove(id);
            }
        }
    }

    /**
     * Method that displays markers in current map view and hides those that aren't.
     * Based on: https://discgolfsoftware.wordpress.com/2012/12/06/hiding-and-showing-on-screen-markers-with-google-maps-android-api-v2/#comment-3
     * @param stops
     */
    private void addMarkersToMap(List<BusStop> stops){
        if (google_map != null){
            LatLngBounds camera_bounds = google_map.getProjection().getVisibleRegion().latLngBounds;

            for (BusStop stop: stops){
                //If stop in  current screen
                if (camera_bounds.contains(new LatLng(stop.getLat(), stop.getLng()))){

                    //If stop not in hashmap, add it and place marker
                    if (!all_markers.containsKey(stop.getStopId())){
                        all_markers.put(stop.getStopId(), google_map.addMarker(new MarkerOptions().position(new LatLng(stop.getLat(), stop.getLng())).title(stop.getStopId()).snippet(stop.getShortName())));
                    }
                } else { //If stop not in range

                    //If stop was in map, remove it
                    if (all_markers.containsKey(stop.getStopId())){
                        all_markers.get(stop.getStopId()).remove();
                        all_markers.remove(stop.getStopId());
                    }
                }
            }
        }
    }

    private void initialiseStops(){
        SqliteTransportDatabase db = new SqliteTransportDatabase(getBaseContext());
        all_stops = db.getAllStops();
        db.close();

        all_markers = new ConcurrentHashMap<String, Marker>();
    }

    private void realtimeStopLogic(){
        ChangeActivity.launchIntent(new ActivitySwitchContainer(null, getBaseContext(), "RealtimeTimetable"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.stopsonmap_button1:
                Log.i(TAG, "button1");
                AtApiDatabaseRequests.populateDB(getBaseContext());
                break;
            case R.id.stopsonmap_FABmenu1:
                Log.i(TAG, "FAVOURITES");
                floatingButtonFavouriteFunctionality();
                break;
            case R.id.stopsonmap_FABmenu2:
                Log.i(TAG, "ENTERSTOP");
                floatingButtonSearchFunctionality();
                break;
            case R.id.stopsonmap_FABmenu3:
                Log.i(TAG, "ADDSTOP ");
                floatingButtonAddStopFunctionality();
                break;
            case R.id.stopsonmap_mainFAB:
                Log.i(TAG, "MainFab");
                mainFABaction();
                break;
            default:
                Log.i(TAG, "default");
                break;
        }
    }

    private void floatingButtonSearchFunctionality(){
        realtimeStopLogic();
    }

    private void floatingButtonFavouriteFunctionality(){
        ChangeActivity.launchIntent(new ActivitySwitchContainer(new HashMap<String, String>(), getBaseContext(), "FavouritesSelector"));
    }

    private void floatingButtonAddStopFunctionality(){
        //        //Instantiate data from favourite stops database
//        List<FavouriteStop> favourite_stops = new ArrayList<FavouriteStop>();
//        SqliteTransportDatabase db =  new SqliteTransportDatabase(StopsOnMap.this);
//        favourite_stops = db.getAllFavouriteStops();
//        db.close();
//
//        //Setting title and icon for dialog
//        AlertDialog.Builder builder = new AlertDialog.Builder(StopsOnMap.this, R.style.AppTheme_NoActionBar);
//        builder.setIcon(R.drawable.ic_favorite_black_24dp);
//        builder.setTitle("Favourite Stops");
//
//        //Adding contents of database to listview
//        final HashMap<String, FavouriteStop> map_favouritestop_customname = new HashMap<String, FavouriteStop>();
//        final ArrayAdapter<String> array_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
//
//        String stop_displayed_text;
//        for(FavouriteStop stop: favourite_stops){
////            if (stop.getCustomName().equals("")){
////                stop_displayed_text = stop.getStopNumber();
////            } else {
////                stop_displayed_text = stop.getCustomName();
////            }
//            stop_displayed_text = stop.getStopNumber();
//
//            map_favouritestop_customname.put(stop_displayed_text, stop);
//
//            array_adapter.add(stop_displayed_text);
//            System.out.println("FV" + stop_displayed_text);
//        }
//
//        //Set cancel button to dismiss the dialog if needed
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        //Set adapter
//        builder.setAdapter(array_adapter, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                FavouriteStop relevant_stop = map_favouritestop_customname.get(array_adapter.getItem(which));
//                Log.i(TAG, relevant_stop.getStopNumber());
//
//                HashMap<String, String> stopnumber = new HashMap<String, String>();
//                stopnumber.put("stop_number", relevant_stop.getStopNumber());
////                ChangeActivity.launchIntent(new ActivitySwitchContainer(stopnumber, getBaseContext(), ".RealtimeBoardStop"));
//                ChangeActivity.launchIntent(new ActivitySwitchContainer(stopnumber, getBaseContext(), "RealtimeBoardStop"));
//            }
//        });
//
//        builder.show();

    }

    private void mainFABaction(){
        if (!fab_menu_open){
            animateFloatingActionMenuOpen();
        } else {
            animateFloatingActionMenuClose();
        }
    }

    /**
     * Animates opening of floating action menu
     */
    private void animateFloatingActionMenuOpen(){
        fab_menu_open = true;

        fab_container1.setVisibility(View.VISIBLE);
        fab_container2.setVisibility(View.VISIBLE);
        fab_container3.setVisibility(View.VISIBLE);

        //Rotate main button to make + into a X
        main_fab.animate().rotationBy(45);

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
        fab_menu_open = false;
        main_fab.animate().rotationBy(-45); //Make Icon + again from x

        //Return containers to behind main action button & hide containers
        fab_container1.animate().translationY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (fab_menu_open == false){
                    fab_container1.setVisibility(View.GONE);
                }
            }
        });

        fab_container2.animate().translationY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (fab_menu_open == false){
                    fab_container2.setVisibility(View.GONE);
                }
            }
        });
        fab_container3.animate().translationY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (fab_menu_open == false){
                    fab_container3.setVisibility(View.GONE);
                }
            }
        });
    }


}

