package com.example.theooswanditosw164.firstyone.dataclasses;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by TheoOswandi on 9/09/2017.
 */

public class ActivitySwitchContainer {
    Bundle bundle;
    Context context;
    String layout;

    public ActivitySwitchContainer(Bundle bundle, Context context, String layout_tag){
        this.bundle = bundle;
        this.context = context;
        this.layout = layout_tag;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public Context getContext() {
        return context;
    }

    public String getLayoutTag() {
        return layout;
    }
}
