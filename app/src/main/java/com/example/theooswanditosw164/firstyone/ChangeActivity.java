package com.example.theooswanditosw164.firstyone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.theooswanditosw164.firstyone.dataclasses.ActivitySwitchContainer;

import java.util.HashMap;

/**
 * Created by TheoOswandi on 9/09/2017.
 */

public class ChangeActivity extends Activity {
    public static void launchIntent(ActivitySwitchContainer container){

        String layout_tag = container.getLayoutTag();
        Context old_context = container.getContext();
        HashMap<String, String> extra_items = container.getExtras();

        System.out.println(layout_tag);

        Class my_class = null;
        try {
            my_class = Class.forName("com.example.theooswanditosw164.firstyone." + layout_tag);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent to_launch = new Intent(old_context, my_class);

        if (extra_items != null) {
            Bundle bundle = new Bundle();
            for (String k: extra_items.keySet()){
                System.out.println(k + " " + extra_items.get(k));
            }

            for (String k: extra_items.keySet()){
                bundle.putString(k, extra_items.get(k));
            }
            to_launch.putExtras(bundle);
        }

        old_context.startActivity(to_launch);
    }
}
