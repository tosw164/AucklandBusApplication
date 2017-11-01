package com.tosw164.busapp;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tosw164.busapp.dataclasses.FavouriteStop;
import com.tosw164.busapp.miscmessages.ToastMessage;
import com.tosw164.busapp.R;
import com.tosw164.busapp.atapi.AtApiRequests;
import com.tosw164.busapp.dataclasses.SqliteTransportDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RealtimeBoardStop extends AppCompatActivity{

    private static final String TAG = RealtimeBoardStop.class.getSimpleName();

    private static final int HOURS_TO_GET = 12;
    private static boolean IS_FAVOURITE;

    String stop_number;
    List<String> listview_contents;
    ListView timetable_view;
    SwipeRefreshLayout refresh_layout;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_realtime_board_stop);

        Toolbar toolbar = (Toolbar) findViewById(R.id.realtime_stopsboard_toolbar);
        setSupportActionBar(toolbar);

        //Get stop number from passed in bundle
//        String stop_number = savedInstanceState.getString("stop_number");
        stop_number = getIntent().getExtras().getString("stop_number");
        System.out.println("STOP NUMBER ON OPEN REALTIMETIMETABLE: " + stop_number);


        //Set is favourite boolean depending on if stop already exist in favourites table
        SqliteTransportDatabase db = new SqliteTransportDatabase(getBaseContext());
        IS_FAVOURITE = (db.getFavouriteStop(stop_number) != null);
        db.close();

        refresh_layout = (SwipeRefreshLayout) findViewById(R.id.realtime_stopsboard_swipe_refresh_layout);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                System.out.println("REFRESH");
                refresh_layout.setRefreshing(false);
            }
        });

        listview_contents = new ArrayList<String>();

        timetable_view = (ListView) findViewById(R.id.realtime_stopsboard_listview);
        timetable_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, listview_contents.get(position));
            }
        });

        new getRealtimeTimetableData().execute(stop_number);

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_realtimeboard_for_stop, menu);

        MenuItem favourite = menu.findItem(R.id.menu_action_favorite);
        if (IS_FAVOURITE){
            favourite.setIcon(R.drawable.ic_favorite_black_24dp);
        } else {
            favourite.setIcon(R.drawable.ic_favorite_border_black_24dp);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_action_favorite:

                IS_FAVOURITE = !IS_FAVOURITE;

                if (IS_FAVOURITE){
                    //TODO add to database
                    addStopToFavourites();
                    ToastMessage.makeToast(getBaseContext(), stop_number + " added to favourites");
                    item.setIcon(R.drawable.ic_favorite_black_24dp);
                } else {
                    //TODO remove from database
                    removeStopFromFavourites();
                    ToastMessage.makeToast(getBaseContext(), stop_number + " removed from favourites");
                    item.setIcon(R.drawable.ic_favorite_border_black_24dp);
                }
                Log.i(TAG, "Favourite");
                break;

            case R.id.menu_action_settings:
                Log.i(TAG, "Settings");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addStopToFavourites(){
        SqliteTransportDatabase db = new SqliteTransportDatabase(getBaseContext());
        db.createFavouriteStop(stop_number, "TEST");

        ToastMessage.makeToast(getBaseContext(), "added current stop from db");
        db.printAllFavouriteStops();

        db.close();
    }

    private void removeStopFromFavourites(){
        SqliteTransportDatabase db = new SqliteTransportDatabase(getBaseContext());
        db.deleteFavouriteStop(new FavouriteStop(stop_number, ""));

        ToastMessage.makeToast(getBaseContext(), "removed current stop from db");
        db.printAllFavouriteStops();

        db.close();
    }


    private static String formatTime(String raw){
        if(raw == null){
            return "";
        }
        if(raw.contains("T")){
            String to_return = raw.split("T")[1];
            return to_return.substring(0,5);
        }
        return "";
    }


    private class getRealtimeTimetableData extends AsyncTask<String, Void, List<String>>{

        @Override
        protected List<String> doInBackground(String... params) {
            List<String> timetable_data = null;

            JSONObject json = AtApiRequests.getRealtimeTimetableFromStopNumber(getBaseContext(), params[0], HOURS_TO_GET + "");
            try{
                if (json.getString("status").equals("OK")){
                    JSONObject responses = json.getJSONObject("response");
                    JSONArray movements = responses.getJSONArray("movements");
                    timetable_data = new ArrayList<String>();
                    for(int i = 0; i < movements.length(); i++){
                        JSONObject move = movements.getJSONObject(i);
                        timetable_data.add("" + move.get("route_short_name") +
                                "\t" + move.get("destinationDisplay") +
                                "\t" + formatTime(move.get("scheduledDepartureTime").toString()) +
                                " " + formatTime(move.get("expectedDepartureTime").toString()));
                    }
                    timetable_data.add(" " + movements.length());


                }
            } catch (JSONException e) {
                //Checks if the API call returns nothing. If does then return null to
                //let user know that no routes were found.
                if (e.getMessage().equals("No value for movements")){
                    return null;
                }
                e.printStackTrace();
            }
            return timetable_data;
        }

        @Override
        protected void onPostExecute(List<String> incoming_routes) {
            if (incoming_routes == null){
                ToastMessage.makeToast(getBaseContext(), "No available routes");
                return;
            }

            ArrayAdapter<String> array_adapter = new ArrayAdapter<String>(RealtimeBoardStop.this, android.R.layout.simple_list_item_1, incoming_routes);
            timetable_view.setAdapter(array_adapter);

//            super.onPostExecute(incoming_routes);
        }
    }
}




