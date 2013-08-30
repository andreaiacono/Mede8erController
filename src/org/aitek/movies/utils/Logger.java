package org.aitek.movies.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/28/13
 * Time: 8:45 PM
 */
public class Logger {

    public static void log(String msg) {
        Log.w("mede8er", msg);
    }

    public static void toast(String msg, Context context) {

        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

}
