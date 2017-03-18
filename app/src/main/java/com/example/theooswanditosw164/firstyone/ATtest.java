package com.example.theooswanditosw164.firstyone;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class ATtest extends AppCompatActivity implements View.OnClickListener {

    Button routes_by_stop_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attest);

        routes_by_stop_button = (Button)findViewById(R.id.attest_routesbystop_button);
        routes_by_stop_button.setOnClickListener(this);

    }

    class SomeAsyncTask extends AsyncTask<String, Void, JSONObject>{

        @Override
        protected JSONObject doInBackground(String... params) {
            HttpURLConnection url_connection = null;
            try{
                URL url = new URL(params[0].toString());
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
                int n = 0;
                while ((line = br.readLine()) != null) {
                    jsonString.append(line);
                    System.out.println(line);
                    n++;
                }
                br.close();
                url_connection.disconnect();

                JSONObject json = new JSONObject(jsonString.toString());

                return json;

            } catch (UnknownHostException e){
                Toast.makeText(getBaseContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            }  catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            ListView some_listview = (ListView)findViewById(R.id.attest_routes_listview);

            ArrayList<String> to_display = new ArrayList<String>();

            try {
                if (json.getString("status").equals("OK")){
                    JSONArray responses_array = json.getJSONArray("response");

                    for (int i = 0; i < responses_array.length(); i++){
                        JSONObject route_json = responses_array.getJSONObject(i);

                        String bus_number = route_json.getString("route_short_name");
                        String bus_number_long = route_json.getString("route_long_name");

                        System.out.println(bus_number + " " + bus_number_long);
                        to_display.add(bus_number + " " + bus_number_long);

                    }
                    ArrayAdapter<String> array_adapter = new ArrayAdapter<String>(ATtest.this, android.R.layout.simple_list_item_1, to_display);
                    some_listview.setAdapter(array_adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void routesByStopFunctionality(String url){
        System.out.println("test");

        new SomeAsyncTask().execute(url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.attest_routesbystop_button:
                routesByStopFunctionality("https://api.at.govt.nz/v2/gtfs/routes/stopid/3330");
                break;
        }
    }
}
