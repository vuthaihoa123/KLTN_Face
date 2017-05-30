package uet.kltn.hoavt_58.crazyexface.helpers;

import android.util.Log;

/**
 * Created by hoavt_58 on 4/9/17.
 */

public class Flog {

    private static final String TAG = "ExFace";
    private static boolean isShow = true;

    public static void d(String msg) {
        if (isShow) {
            Log.d(TAG, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isShow) {
            Log.d(tag, msg);
        }
    }
}
