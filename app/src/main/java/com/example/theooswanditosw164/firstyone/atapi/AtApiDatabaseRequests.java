package com.example.theooswanditosw164.firstyone.atapi;

import android.content.Context;
import android.os.AsyncTask;

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
    private static class BundledContextInt {
        private Context context;
        private Integer number_of_stops;

        public BundledContextInt(Context c, Integer n_stops){
            this.context = c;
            this.number_of_stops = n_stops;
        }

        public Integer getNumberOfStops(){
            return number_of_stops;
        }

        public Context getContext(){
            return context;
        }
    }

    public static void populateDB(Context context){
        new getStopsWorker().execute(context);
    }

    private static class getStopsWorker extends AsyncTask<Context, Void, BundledContextInt> {
        @Override
        protected BundledContextInt doInBackground(Context... params) {
            Context context = params[0];

            JSONObject json = AtApiRequests.getAllStops(params[0]);
            try {
                if (json.getString("status").equals("OK")){
                    JSONArray responses_array = json.getJSONArray("response");

                    String short_name, stop_id;
                    Double stop_lat, stop_lng;

                    SqliteTransportDatabase db = new SqliteTransportDatabase(context);
                    db.stopsTablePrintColumnNames();

                    if (responses_array.length() > 0){
                        db.resetStopsTable();
                    }

                    System.out.println(responses_array.length());
                    for (int i = 0; i < responses_array.length(); i++){
                        JSONObject stop_json = responses_array.getJSONObject(i);

                        short_name = stop_json.getString("stop_name");
                        stop_id = stop_json.getString("stop_id");

                        //Due to changes, need to get number before first -
                        stop_id = stop_id.split("\\-")[0];
                        stop_lat = stop_json.getDouble("stop_lat");
                        stop_lng = stop_json.getDouble("stop_lon");

                        System.out.println(i + "/" + responses_array.length() + short_name + " " + stop_id + " ");
                        db.createStop(stop_id, short_name, stop_lat, stop_lng);
//                        System.out.print(i + " ");
                    }
                    BundledContextInt to_return = new BundledContextInt(context, db.countStops());
                    db.close();
                    db.stopsTablePrintColumnNames();
                    return to_return;

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return new BundledContextInt(context, null);
        }

        @Override
        protected void onPostExecute(BundledContextInt bundle) {
            if (bundle.getNumberOfStops() == null){
                ToastMessage.makeToast(bundle.getContext(), "Error adding stops");
            } else {
                ToastMessage.makeToast(bundle.getContext(), "finished adding " + bundle.getNumberOfStops() + " stops");
            }
        }
    }


}
