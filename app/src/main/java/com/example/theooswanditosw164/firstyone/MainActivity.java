package com.example.theooswanditosw164.firstyone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;


public class MainActivity extends Activity implements OnClickListener{

    Button the_button, maps_button;
    TextView the_textview;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        i = 0;

        the_button = (Button)findViewById(R.id.button);
        the_button.setOnClickListener(this);

        maps_button = (Button)findViewById(R.id.maps_button);
        maps_button.setOnClickListener(this);

        the_textview = (TextView)findViewById(R.id.textview);

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
                break;
            case R.id.maps_button:
                launch_intent("MapsActivity");
                break;
        }
    }
}
