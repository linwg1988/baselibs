package stoneonline.framework.utils;

import android.util.Log;

/**
 *
 * @author lwg
 */
public class L {

    private L() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isDebug = true;
    private static final String TAG = "lwg";

    public static void i(String msg) {
        if(msg == null)
            return;
        if (isDebug)
            Log.i(TAG, msg);
    }

    public static void d(String msg) {
        if(msg == null)
            return;
        if (isDebug)
            Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (isDebug)
            Log.e(TAG, msg);
    }

    public static void v(String msg) {
        if(msg == null)
            return;
        if (isDebug)
            Log.v(TAG, msg);
    }

    public static void i(String tag, String msg) {
        if(msg == null)
            return;
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if(msg == null)
            return;
        if (isDebug)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if(msg == null)
            return;
        if (isDebug)
            Log.e(tag, msg);
    }

    public static void v(String tag, String msg) {
        if(msg == null)
            return;
        if (isDebug)
            Log.v(tag, msg);
    }
}