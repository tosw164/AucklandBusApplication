package com.tosw164.busapp.dataclasses;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by TheoOswandi on 9/09/2017.
 */

public class ActivitySwitchContainer {
    HashMap<String, String> extras;
    Context context;
    String layout;

    public ActivitySwitchContainer(HashMap<String, String> extras, Context context, String layout_tag){
        this.extras = extras;
        this.context = context;
        this.layout = layout_tag;
    }

    public HashMap<String, String> getExtras() {
        return extras;
    }

    public Context getContext() {
        return context;
    }

    public String getLayoutTag() {
        return layout;
    }
}
