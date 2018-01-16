package com.flickersp.android;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Rapidd08 on 1/13/2018.
 */

public class InternetConnection {
    public static boolean checkConnection(Context context) {
        return  ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}
