package com.tosw164.busapp;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by theooswanditosw164 on 26/03/17.
 */

public class ChangeIntent {

    public Intent returnIntent(Activity current_activity, String layout_tag){
        Class my_class = null;
        try {
            my_class = Class.forName("com.tosw164.busapp." + layout_tag);

        } catch (ClassNotFoundException e){
            e.printStackTrace();
            return null;
        }
        return new Intent(current_activity, my_class);
    }
}
