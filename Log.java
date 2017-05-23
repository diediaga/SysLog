package org.lalalab.databiographyclient.databiographyclient;

/**
 * Log wrapper
 */
public class Log {
    private static final String TAG = "SysLog";

    public static void v(String msg){
        android.util.Log.v(TAG, msg);
    }

    public static void d(String s, String msg){
        android.util.Log.d(TAG, msg);
    }

    public static void e(String msg, Throwable th){
        android.util.Log.e(TAG, msg, th);
    }
}
