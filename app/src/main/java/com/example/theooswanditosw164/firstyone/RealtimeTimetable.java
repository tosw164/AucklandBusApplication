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

import com.example.theooswanditosw164.firstyone.atapi.AtApiRequests;
import com.example.theooswanditosw164.firstyone.miscmessages.ToastMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RealtimeTimetable extends AppCompatActivity  implements View.OnClickListener{

    EditText stop_number_input;
    Button get_button;
    ListView timetable_listview;
    ArrayList<String> timetable_contents;

    private static final String TAG = RealtimeTimetable.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_timetable);

        timetable_contents = new ArrayList<String>();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        stop_number_input = (EditText) findViewById(R.id.realtime_board_edittext);
        stop_number_input.setOnKeyListener(new View.OnKeyListener() {
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

        get_button = (Button) findViewById(R.id.realtime_board_button);
        get_button.setOnClickListener(this);

        timetable_listview = (ListView) findViewById(R.id.realtime_board_listview);
        timetable_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "pos" + timetable_contents.get(position));
            }

        });

    }

    private void getButtonLogic(){
        String input_value = stop_number_input.getText().toString();

        if(validateEditTextInput(input_value)){
            new getRealtimeTimetableInformation().execute(input_value);
        } else {
            ToastMessage.makeToast(getBaseContext(), "Invalid 4 digit stop number");
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

    class getRealtimeTimetableInformation extends AsyncTask<String, Void, JSONObject>{
        @Override
        protected JSONObject doInBackground(String... params) {
            return AtApiRequests.getRealtimeTimetableFromStopNumber(getBaseContext(), params[0], "1");
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json == null){
                ToastMessage.makeToast(getBaseContext(), "Failed to connect to server");
            }

            timetable_contents = new ArrayList<String>();

            String shortname, destination, time;

            try{
                if (json.getString("status").equals("OK")){
                    JSONObject responses = json.getJSONObject("response");
                    JSONArray movements = responses.getJSONArray("movements");
                    for(int i = 0; i < movements.length(); i++){
                        JSONObject move = movements.getJSONObject(i);
//                        Log.i(TAG, "" + move.get("route_short_name"));
                        timetable_contents.add("" + move.get("route_short_name") +
                                "\t" + move.get("destinationDisplay") +
                                "\t" + move.get("scheduledArrivalTime").toString() +
                                " " + move.get("expectedArrivalTime").toString());
                    }

                    timetable_contents.add(" " + movements.length());
                    ArrayAdapter<String> array_adapter = new ArrayAdapter<String>(RealtimeTimetable.this, android.R.layout.simple_list_item_1, timetable_contents);
                    timetable_listview.setAdapter(array_adapter);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.realtime_board_button:
                getButtonLogic();
                break;
        }
    }
}
