package com.example.theooswanditosw164.firstyone;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.theooswanditosw164.firstyone.atapi.ATapiCall;
import com.example.theooswanditosw164.firstyone.atapi.AtApiRequests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class RoutesForStop extends AppCompatActivity implements View.OnClickListener {

    EditText stopnumber_input;
    Button get_button;
    ListView some_listview;
    ArrayList<String> current_list_contents;


    private static final String TAG = RoutesForStop.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routesbystop);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        stopnumber_input = (EditText)findViewById(R.id.routesbystop_edittext);
        stopnumber_input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN){
                    switch (keyCode){
                        case KeyEvent.KEYCODE_ENTER:
                            getButtonLogic();
                            break;
                    }
                }
                return false;
            }
        });

        get_button = (Button)findViewById(R.id.routesbystop_button);
        get_button.setOnClickListener(this);

        some_listview = (ListView)findViewById(R.id.routesbystop_listview);
        some_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "pos" + current_list_contents.get(position));
            }

        });
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.routesbystop_button:
                getButtonLogic();
                break;
        }
    }

    private void getButtonLogic(){
        String edittext_value = stopnumber_input.getText().toString();
//        Log.i(TAG, "Hello World");
//        Log.i(TAG, edittext_value);

        if (validateEditTextInput(edittext_value)){
            new getRouteForStopInformation().execute(edittext_value);
        } else {
            Toast.makeText(getBaseContext(), "Invalid 4 digit stop number", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateEditTextInput(String s){
        if (s.length() == 0){
            return false;
        } else if (s.length() != 4){
            return false;
        }
        return true;
    }

    class getRouteForStopInformation extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
//            return getJSONforLink("https://api.at.govt.nz/v2/gtfs/routes/stopid/" + params[0]);
//            return getJSONforLink("https://api.at.govt.nz/v2/gtfs/stopTimes/stopId/" + params[0]);
//            return getJSONforLink("https://api.at.govt.nz/v2/gtfs/routes");
//            return getJSONforLink("https://api.at.govt.nz/v2/gtfs/trips");


//            return getJSONforLink("https://api.at.govt.nz/v2/gtfs/stops/stopinfo/" + params[0]);
            return AtApiRequests.getTimetableInformaitonFromStopNumber(getBaseContext(), params[0]);
        }



        @Override
        protected void onPostExecute(JSONObject json) {
            if (json == null){
                Toast.makeText(getBaseContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
                return;
            }


            current_list_contents = new ArrayList<String>();

            String short_name, trip_headsign, arr_time;

            try {
                if (json.getString("status").equals("OK")){
                    JSONArray responses_array = json.getJSONArray("response");

                    for (int i = 0; i < responses_array.length(); i++){
//                    for (int i = 0; i < 5; i++){

                        JSONObject incoming_json = responses_array.getJSONObject(i);

//                        Log.i(TAG, incoming_json.toString());

                        short_name = incoming_json.getString("route_short_name");
                        trip_headsign = incoming_json.getString("trip_headsign");
                        arr_time = removeSecondsFromTime(incoming_json.getString("departure_time"));

                        if (filterTripByTime(arr_time)){
//                        if(true){
                            String str_todisplay = short_name + " " + trip_headsign + " " + arr_time;
//                           System.out.println(str_todisplay);
                            current_list_contents.add(str_todisplay);
                        }


                    }
                    current_list_contents.add(" " + responses_array.length());
                    ArrayAdapter<String> array_adapter = new ArrayAdapter<String>(RoutesForStop.this, android.R.layout.simple_list_item_1, current_list_contents);
                    some_listview.setAdapter(array_adapter);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String removeSecondsFromTime(String initial_time){
        String[] split_time = initial_time.split(":");
        return split_time[0] + ":" + split_time[1];
    }

    //ONE HOUR
    private boolean filterTripByTime(String time){

        Calendar cal = Calendar.getInstance();
        boolean to_return = true;
        String[] scheduled_time = time.split(":");
        int scheduled_hour = Integer.valueOf(scheduled_time[0]);
        int scheduled_minute = Integer.valueOf(scheduled_time[1]);

        int current_hour = cal.get(Calendar.HOUR_OF_DAY);
        int current_minute = cal.get(Calendar.MINUTE);

        if (scheduled_hour == current_hour){
            if (scheduled_minute < current_minute){
                return false;
            }
        }

        System.out.println((scheduled_hour - cal.get(Calendar.HOUR_OF_DAY)) + "hr" + scheduled_time[0] + "mn" + scheduled_time[1] + "curr" + cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE));
        return true;
    }

    private JSONObject getJSONforLink(String url_input){
        return ATapiCall.fetchJSONfromURLwithSubKey(getBaseContext(), url_input);
    }
}
