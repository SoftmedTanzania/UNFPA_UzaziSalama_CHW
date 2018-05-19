package com.softmed.uzazi_salama.util;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by ali on 9/14/17.
 */

public class OrientationHelper {

    private static final String TAG = OrientationHelper.class.getSimpleName();

    public static void setProperOrientationForDevice(Activity activity) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        float widthDp = displayMetrics.widthPixels / displayMetrics.density;
        if (widthDp >= 600)
            // tablet
            lockOrientationLandscape(activity);
        else
            // phone
            lockOrientationPortrait(activity);
    }

    private static void lockOrientationPortrait(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Log.d(TAG, "portrait mode locked.");
    }


    private static void lockOrientationLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Log.d(TAG, "landscape mode locked.");

    }
}
