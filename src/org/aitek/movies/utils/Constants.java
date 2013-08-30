package org.aitek.movies.utils;

import android.app.Activity;
import android.content.res.Resources;
import org.aitek.movies.R;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/22/13
 * Time: 2:43 PM
 */
public class Constants {

    static public final String APP_NAME = Resources.getSystem().getString(R.string.app_name);
    static public final String APP_VERSION = "0.1";
    static public final String APP = R.string.app_name + " v" + APP_VERSION;

    static public final String ALL_MOVIES = "ALL MOVIES";
    static public final String ROOT_DIRECTORY = "/sdcard/MoviePlayer";
    static public final String MOVIES_FILE = "movies.dat";

    static public final int UDP_PORT = 1186;
    static public final int TCP_PORT = 1187;

    static public final String PREFERENCES_MEDE8ER_IPADDRESS = "mede8er_ipaddress";
}
