package com.example.theooswanditosw164.firstyone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.theooswanditosw164.firstyone.dataclasses.ActivitySwitchContainer;

/**
 * Created by TheoOswandi on 9/09/2017.
 */

public class ChangeActivity extends Activity {
    public static void launchIntent(ActivitySwitchContainer container){

        String layout_tag = container.getLayoutTag();
        Context old_context = container.getContext();
        Bundle intent_bundle = container.getBundle();

        Class my_class = null;
        try {
            my_class = Class.forName("com.example.theooswanditosw164.firstyone." + layout_tag);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent to_launch = new Intent(old_context, my_class);
        old_context.startActivity(to_launch);
    }
}
