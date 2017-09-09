package com.example.theooswanditosw164.firstyone.atapi;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.theooswanditosw164.firstyone.dataclasses.BusStop;
import com.example.theooswanditosw164.firstyone.dataclasses.SqliteTransportDatabase;
import com.example.theooswanditosw164.firstyone.miscmessages.ToastMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by TheoOswandi on 9/09/2017.
 */

public class AtApiDatabaseRequests extends ATapiCall {

    /**
     * Custom struct-like class to hold passed in Context and generated JSON
     */
    private class BundledContextJSON {
        private Context context;
        private JSONObject json;

        public BundledContextJSON(Context c, JSONObject json){
            this.context = c;
            this.json = json;
        }

        public JSONObject getJSON(){
            return json;
        }

        public Context getContext(){
            return context;
        }
    }

    private void populateDB(Context context){
        new getStopsWorker().execute(context);
    }

    class getStopsWorker extends AsyncTask<Context, Void, BundledContextJSON> {
        @Override
        protected BundledContextJSON doInBackground(Context... params) {
            BundledContextJSON bundle = new BundledContextJSON(params[0], AtApiRequests.getAllStops(params[0]));

            return bundle;
        }

        @Override
        protected void onPostExecute(BundledContextJSON bundle) {
            printAllStops(bundle);
        }
    }

    private void printAllStops(BundledContextJSON bundle){
        JSONObject json = bundle.getJSON();
        Context context = bundle.getContext();

        try {
            if (json.getString("status").equals("OK")){
                JSONArray responses_array = json.getJSONArray("response");

                if (responses_array.length() == 0){
                    ToastMessage.makeToast(context, "Nothing returned");
                } else {
                    String short_name, stop_id;
                    Double stop_lat, stop_lng;

                    SqliteTransportDatabase db = new SqliteTransportDatabase(context);
                    db.printColumnNames();
                    for (int i = 0; i < responses_array.length(); i++){
                        JSONObject stop_json = responses_array.getJSONObject(i);

                        short_name = stop_json.getString("stop_name");
                        stop_id = stop_json.getString("stop_id");
                        stop_lat = stop_json.getDouble("stop_lat");
                        stop_lng = stop_json.getDouble("stop_lon");

                        db.createStop(stop_id, short_name, stop_lat, stop_lng);
                    }

                    ToastMessage.makeToast(context, "finished adding " + db.countStops() + " stops");
                    db.close();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
