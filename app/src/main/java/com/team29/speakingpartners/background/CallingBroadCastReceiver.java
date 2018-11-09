package com.team29.speakingpartners.background;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.team29.speakingpartners.net.ConnectionChecking;

public class CallingBroadCastReceiver extends BroadcastReceiver {

    private static final String TAG = CallingBroadCastReceiver.class.getSimpleName();

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        // Check Connection
        if (ConnectionChecking.checkConnection(context)) {
            Log.d(TAG, "Connection Available");
            Intent i = new Intent(context, CallingStateService.class);
            context.startService(i);
        } else {
            Log.d(TAG, "Connection Not Available");
            Intent i = new Intent(context, CallingStateService.class);
            context.stopService(i);
        }

    }
}
