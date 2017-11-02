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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
        toolbar.setTitleTextColor(0x000000);
        setSupportActionBar(toolbar);

        //Get stop number from passed in bundle
        stop_number = getIntent().getExtras().getString("stop_number");
        System.out.println("STOP NUMBER ON OPEN REALTIMETIMETABLE: " + stop_number);


        getSupportActionBar().setTitle("Routes for stop: " + stop_number);


        //Set is favourite boolean depending on if stop already exist in favourites table
        SqliteTransportDatabase db = new SqliteTransportDatabase(getBaseContext());
        IS_FAVOURITE = (db.getFavouriteStop(stop_number) != null);
        db.close();

        refresh_layout = (SwipeRefreshLayout) findViewById(R.id.realtime_stopsboard_swipe_refresh_layout);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getRealtimeTimetableData().execute(stop_number);
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

    /**
     * Method that instantiates the favourite heart icon depending on if current stop is favourited
     * @param menu
     * @return
     */
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

                //Toggle status of current stop number to/from favourite and notfavourite
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

    /**
     * Method that adds current stop number associated with realtime board to favourites table
     *  in the database.
     */
    private void addStopToFavourites(){
        SqliteTransportDatabase db = new SqliteTransportDatabase(getBaseContext());
        db.createFavouriteStop(stop_number, "TEST");

        //TODO removed as used for debugging
        ToastMessage.makeToast(getBaseContext(), "added current stop from db");
        db.printAllFavouriteStops();

        db.close();
    }

    /**
     * Method that removes current stop number associated with realtime board from favourites
     * table in the database.
     */
    private void removeStopFromFavourites(){
        SqliteTransportDatabase db = new SqliteTransportDatabase(getBaseContext());
        db.deleteFavouriteStop(new FavouriteStop(stop_number, ""));

        //TODO removed as used for debugging
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

            //Format to convert to/from
            SimpleDateFormat expected_date_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            expected_date_format.setTimeZone(TimeZone.getTimeZone("NZST"));
            SimpleDateFormat scheduled_date_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.s'Z'");
            scheduled_date_format.setTimeZone(TimeZone.getTimeZone("NZST"));

            SimpleDateFormat output_time_format = new SimpleDateFormat("HH:mm");

            Date expected_date;
            String expected_time;

            Date scheduled_date;
            String scheduled_time;

            //Calls the API to get JSON representing routes for stop for given hours from now.
            JSONObject json = AtApiRequests.getRealtimeTimetableFromStopNumber(getBaseContext(), params[0], HOURS_TO_GET + "");
            try{
                if (json.getString("status").equals("OK")){
                    JSONObject responses = json.getJSONObject("response");
                    JSONArray movements = responses.getJSONArray("movements");
                    timetable_data = new ArrayList<String>();

                    //Iterates through each route item and extract estimated and scheduled times.
                    for(int i = 0; i < movements.length(); i++){
                        JSONObject trip = movements.getJSONObject(i);

                        //TODO convert time here
                        scheduled_time = trip.get("scheduledDepartureTime").toString();
                        expected_time = trip.get("expectedDepartureTime").toString();


                        System.out.println("SCHED" + scheduled_time +
                                            "\nEXP" + expected_time);
                        System.out.println(TimeZone.getDefault().toString());
                        scheduled_date = scheduled_date_format.parse(scheduled_time);
                        if(!expected_time.equals("null")){
                            expected_date = expected_date_format.parse(expected_time);
                        } else {
                            expected_date = Calendar.getInstance().getTime();
                        }


                        //TODO format this nicer
                        timetable_data.add("" + trip.get("route_short_name") +
                                "\t" + trip.get("destinationDisplay") +
                                "\t" + output_time_format.format(scheduled_date) +
                                " " + output_time_format.format(expected_date));
                    }
                    timetable_data.add(" " + movements.length() + " " + Calendar.getInstance().getTime());


                }
            } catch (JSONException e) {
                //Checks if the API call returns nothing. If does then return null to
                //let user know that no routes were found.
                if (e.getMessage().equals("No value for movements")){
                    return null;
                }

                //Print stack trace for debugging and identifying when unexpected behaviour observed
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Return timetable data to populate listview the user sees
            return timetable_data;
        }

        /**
         * Method that updates the listview on the GUI side of the application
         * @param incoming_routes is list of strings containig the incoming routes
         *                        and expected & scheduled departure times
         */
        @Override
        protected void onPostExecute(List<String> incoming_routes) {
            if (incoming_routes == null){
                ToastMessage.makeToast(getBaseContext(), "No available routes");
            } else {
                ArrayAdapter<String> array_adapter = new ArrayAdapter<String>(RealtimeBoardStop.this, android.R.layout.simple_list_item_1, incoming_routes);
                timetable_view.setAdapter(array_adapter);
            }

            if(refresh_layout.isRefreshing()){
                refresh_layout.setRefreshing(false);
            }
        }
    }
}




