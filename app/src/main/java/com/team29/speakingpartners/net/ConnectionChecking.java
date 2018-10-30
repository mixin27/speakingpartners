package com.team29.speakingpartners.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/** Checking Connection State */
public class ConnectionChecking  {

    public static final String TAG = ConnectionChecking.class.getSimpleName();

    public static boolean checkConnection(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

}
