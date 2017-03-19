package com.example.theooswanditosw164.firstyone;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class RoutesForStop extends AppCompatActivity implements View.OnClickListener {

    EditText stopnumber_input;
    Button get_button;

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
        Log.i(TAG, "Hello World");
        Log.i(TAG, edittext_value);

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

    private void timetableInformationLogic(){
        //get value from edittext
        //put into https://api.at.govt.nz/v2/gtfs/stopTimes/stopId/3330
        //for each result
        //save arrival_time, trip_id
        //create TripArrivalTime object
        //add trip_id --> TripArrivalTime to map

    }

    class getRouteForStopInformation extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
//            return getJSONforLink("https://api.at.govt.nz/v2/gtfs/routes/stopid/" + params[0]);
            return getJSONforLink("https://api.at.govt.nz/v2/gtfs/routes");
//            return getJSONforLink("https://api.at.govt.nz/v2/gtfs/trips");

        }


        @Override
        protected void onPostExecute(JSONObject json) {
            ListView some_listview = (ListView)findViewById(R.id.routesbystop_listview);

            ArrayList<String> to_display = new ArrayList<String>();

            String route_name, route_headsign, arrival_time;

            try {
                if (json.getString("status").equals("OK")){
                    JSONArray responses_array = json.getJSONArray("response");

                    for (int i = 0; i < responses_array.length(); i++){
//                    for (int i = 0; i < 5; i++){

//                        JSONObject incoming_json = responses_array.getJSONObject(i);

//                        Log.i(TAG, incoming_json.toString());
//                        route_name = incoming_json.getString("route_short_name");



//                        JSONObject tripsbyrouteJSON = new JSONObject(getJSONforLink())

//                        String bus_number = route_json.getString("route_short_name");
//                        String bus_number_long = route_json.getString("route_long_name");

//                        System.out.println(bus_number + " " + bus_number_long);
//                        to_display.add(bus_number + " " + bus_number_long);

//                        n++;

                    }
                    to_display.add(" " + responses_array.length());
                    ArrayAdapter<String> array_adapter = new ArrayAdapter<String>(RoutesForStop.this, android.R.layout.simple_list_item_1, to_display);
                    some_listview.setAdapter(array_adapter);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject getJSONforLink(String url_input){
        HttpURLConnection url_connection = null;
        try{
            URL url = new URL(url_input);
            url_connection = (HttpURLConnection)url.openConnection();
            url_connection.setRequestProperty("Ocp-Apim-Subscription-Key", "92e47087b5c44366b1b74f96f42632df");

            // Request not successful
            if (url_connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Request Failed. HTTP Error Code: " + url_connection.getResponseCode());
            }

            // Read response
            BufferedReader br = new BufferedReader(new InputStreamReader(url_connection.getInputStream()));

            StringBuffer jsonString = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
                System.out.println("LINE:" + line);
            }
            br.close();
            url_connection.disconnect();

            JSONObject json = new JSONObject(jsonString.toString());

            return json;

        } catch (UnknownHostException e){
            Toast.makeText(getBaseContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
