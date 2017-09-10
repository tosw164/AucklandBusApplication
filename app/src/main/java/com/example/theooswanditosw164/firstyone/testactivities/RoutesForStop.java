package com.example.theooswanditosw164.firstyone.testactivities;

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

import com.example.theooswanditosw164.firstyone.R;
import com.example.theooswanditosw164.firstyone.atapi.AtApiRequests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

                        JSONObject incoming_json = responses_array.getJSONObject(i);


                        short_name = incoming_json.getString("route_short_name");
                        trip_headsign = incoming_json.getString("trip_headsign");
                        arr_time = removeSecondsFromTime(incoming_json.getString("departure_time"));

                        String str_todisplay = short_name + " " + trip_headsign + " " + arr_time;
                        current_list_contents.add(str_todisplay);


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
}
