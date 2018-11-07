package com.team29.speakingpartners.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class CheckPermissionForApp {

    public static final String TAG = CheckPermissionForApp.class.getSimpleName();

    public static boolean checkSelfPermission(Activity context, String permission, int requestCode) {
        Log.i(TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(context,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(context,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

}
