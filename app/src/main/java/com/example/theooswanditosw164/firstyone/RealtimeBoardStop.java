package com.example.theooswanditosw164.firstyone;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.theooswanditosw164.firstyone.atapi.AtApiRequests;
import com.example.theooswanditosw164.firstyone.miscmessages.ToastMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RealtimeBoardStop extends AppCompatActivity {

    private static final String TAG = com.example.theooswanditosw164.firstyone.RealtimeBoardStop.class.getSimpleName();

    private static final int HOURS_TO_GET = 12;

    List<String> listview_contents;
    ListView timetable_view;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_realtime_board_stop);

        Toolbar toolbar = (Toolbar) findViewById(R.id.realtime_stopsboard_toolbar);
        setSupportActionBar(toolbar);

        //Get stop number from passed in bundle
//        String stop_number = savedInstanceState.getString("stop_number");
        String stop_number = getIntent().getExtras().getString("stop_number");

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
        return true;
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
                                "\t" + formatTime(move.get("scheduledArrivalTime").toString()) +
                                " " + formatTime(move.get("expectedArrivalTime").toString()));
                    }
                    timetable_data.add(" " + movements.length());


                }
            } catch (JSONException e) {
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

            ArrayAdapter<String> array_adapter = new ArrayAdapter<String>(com.example.theooswanditosw164.firstyone.RealtimeBoardStop.this, android.R.layout.simple_list_item_1, incoming_routes);
            timetable_view.setAdapter(array_adapter);

//            super.onPostExecute(incoming_routes);
        }
    }
}




