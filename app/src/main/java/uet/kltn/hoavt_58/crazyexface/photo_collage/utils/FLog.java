package uet.kltn.hoavt_58.crazyexface.photo_collage.utils;

import android.util.Log;

/**
 * Created by Adm on 8/3/2016.
 */
public class FLog {
    private static final boolean IS_DEBUG = true;
    private static final String TAG = "PHOTO_COLLAGE";

    public static void d(String msg) {
        if (!IS_DEBUG) return;
        Log.d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (!IS_DEBUG) return;
        Log.d(tag, msg);
    }
}
