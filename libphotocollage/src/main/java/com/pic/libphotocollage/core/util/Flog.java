package com.pic.libphotocollage.core.util;

import android.util.Log;

/**
 * Created by hoavt on 25/07/2016.
 */
public class Flog {
    public static final String CTAG = "TestLayout";
    private static final String TAG = "LibPhotoCollageCore";
    private static final boolean mShow = false;
    private static final boolean mDebugShow = true;

    public static void i(String content) {
        if (mShow) {
            Log.i(TAG, content + "");
        }
    }

    public static void d(String content) {
        if (mDebugShow) {
            Log.i(TAG, content + "");
        }
    }

    public static void i(String tag, String content) {
        if (mShow) {
            Log.i(tag, content + "");
        }
    }

    public static void d(String tag, String content) {
        if (mDebugShow) {
            Log.i(tag, content + "");
        }
    }

    public static void d(String tag, Object... objects) {
        if (mDebugShow) {
            for (Object object: objects) {
                Log.i(tag, object.toString() + "\n");
            }
        }
    }
}
