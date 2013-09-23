package org.aitek.controller.ui;

import android.app.Activity;
import android.content.Context;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/24/13
 * Time: 12:06 AM
 */
public abstract class GenericProgressIndicator {


    protected Context context;
    private int counter;
    protected int max = 100;
    private List<String> genres;

    public GenericProgressIndicator(Context context) {
        this.context = context;
    }

    /**
     * setups the indicator
     * @return true if setup was ok and process has to continue; false if process has not to go on.
     * @throws Exception
     */
    public boolean setup() {
        counter = 0;
        return true;
    }

    /**
     * returns the max for the progress bar
     * @return
     */
    public int getMax() {
        return max;
    }


    /**
     * processes the next element of the set
     * @return
     * @throws Exception
     */
    public int next() throws Exception {
        return counter ++;
    }


    /**
     * finishes the execution
     * @throws Exception
     */
    public void finish() throws Exception {

    }

    public Context getContext() {
        return context;
    }

    public CharSequence getText() {
        return "Message for item " + counter;
    }

    public List<String> getGenres() {
        return genres;
    }
}
