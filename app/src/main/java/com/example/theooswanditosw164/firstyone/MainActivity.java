package com.example.theooswanditosw164.firstyone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

import com.example.theooswanditosw164.firstyone.atapi.AtApiRequests;
import com.example.theooswanditosw164.firstyone.dataclasses.HashMapContainers;


public class MainActivity extends Activity implements OnClickListener{

    Button the_button, maps_button, newmaps_button, at_test_button, timetable_button;
    TextView the_textview;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        i = 0;

        the_button = (Button)findViewById(R.id.button);
        the_button.setOnClickListener(this);
        the_button.setText("Hello");

        maps_button = (Button)findViewById(R.id.maps_button);
        maps_button.setOnClickListener(this);
        maps_button.setText("HelloWorld GoogleMaps example");

        newmaps_button = (Button)findViewById(R.id.newmaps_button);
        newmaps_button.setOnClickListener(this);
        newmaps_button.setText("MapsView random functionality testing");

        at_test_button = (Button)findViewById(R.id.at_test_button);
        at_test_button.setOnClickListener(this);
        at_test_button.setText("AT test, Routes and timetable information");

        timetable_button = (Button)findViewById(R.id.timetable_button);
        timetable_button.setOnClickListener(this);
        timetable_button.setText("Stop & Route information by stopnumber input");

        the_textview = (TextView)findViewById(R.id.textview);

        HashMapContainers.getInstance(getBaseContext());
    }

    private void launch_intent(String layout_tag){
        Class my_class = null;
        try {
            my_class = Class.forName("com.example.theooswanditosw164.firstyone." + layout_tag);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent to_launch = new Intent(MainActivity.this,my_class);
        startActivity(to_launch);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                i++;
                the_textview.setText("current count: " + i);
                HashMapContainers.getInstance(getBaseContext()).test();
//                AtApiRequests.printAllTrips(getBaseContext());
                break;
            case R.id.maps_button:
                launch_intent("MapsActivity");
                break;
            case R.id.newmaps_button:
                launch_intent("NewMapActivity");
                break;
            case R.id.at_test_button:
                launch_intent("ATtest");
                break;
            case R.id.timetable_button:
                launch_intent("RoutesForStop");
                break;

        }
    }
}
