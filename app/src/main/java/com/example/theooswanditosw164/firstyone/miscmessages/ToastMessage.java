package com.example.theooswanditosw164.firstyone.miscmessages;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by theooswanditosw164 on 19/03/17.
 */

public class ToastMessage {

    public static void makeToast(Context c, String message){
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }
}
