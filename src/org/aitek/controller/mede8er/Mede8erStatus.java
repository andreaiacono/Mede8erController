package org.aitek.controller.mede8er;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 9/17/13
 * Time: 11:41 AM
 */
public class Mede8erStatus {

    // horrible hack instead of enum, because Handler  wants an int to pass messages
    public static final int ERROR = -1;
    public static final int DOWN = 0;
    public static final int UP = 1;
    public static final int CONNECTED = 2;
    public static final int NO_JUKEBOX = 3;
    public static final int FULLY_OPERATIONAL = 4;
    public static final int SHOW_MOVIES = 5;
}
