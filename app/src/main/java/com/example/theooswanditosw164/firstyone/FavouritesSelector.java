package com.example.theooswanditosw164.firstyone;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.theooswanditosw164.firstyone.dataclasses.ActivitySwitchContainer;
import com.example.theooswanditosw164.firstyone.dataclasses.FavouriteStop;
import com.example.theooswanditosw164.firstyone.dataclasses.SqliteTransportDatabase;
import com.example.theooswanditosw164.firstyone.miscmessages.ToastMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FavouritesSelector extends Activity {

    private static final String TAG = com.example.theooswanditosw164.firstyone.FavouritesSelector.class.getSimpleName();

    ListView favourite_stops_listview;
    List<FavouriteStop> favourite_stops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites_selector);

        SqliteTransportDatabase db = new SqliteTransportDatabase(getBaseContext());
        favourite_stops = db.getAllFavouriteStops();
        db.close();

        if (favourite_stops.size() == 0){
            ToastMessage.makeToast(getBaseContext(), "No favourite stops");
            finish();
            return;
        }

        final List<String> listview_contents = new ArrayList<String>();
        String stop_displayed_text;
        for(FavouriteStop stop: favourite_stops){
            listview_contents.add(stop.getStopNumber());

        }
        ArrayAdapter<String> array_adapter = new ArrayAdapter<String>(FavouritesSelector.this, android.R.layout.simple_list_item_1, listview_contents);

        favourite_stops_listview = (ListView) findViewById(R.id.favourite_stops_listview);
        favourite_stops_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "selected: " + listview_contents.get(position));

                HashMap<String, String> extras = new HashMap<String, String>();
                extras.put("stop_number", listview_contents.get(position));
                ChangeActivity.launchIntent(new ActivitySwitchContainer(extras, getBaseContext(), "RealtimeBoardStop"));

            }
        });
        favourite_stops_listview.setAdapter(array_adapter);
    }
}
