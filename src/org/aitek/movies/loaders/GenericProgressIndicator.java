package org.aitek.movies.loaders;

import android.app.Activity;
import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/24/13
 * Time: 12:06 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class GenericProgressIndicator {


    protected Activity activity;
    private int counter;

    public GenericProgressIndicator(Activity activity) {

        this.activity = activity;
    }

    /**
     * setups the indicator
     * @return
     * @throws Exception
     */
    public void setup() throws Exception {
        counter = 0;
    }

    /**
     * returns the max for the progress bar
     * @return
     */
    public int getMax() {
        return 100;
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

    public Activity getActivity() {
        return activity;
    }

    public CharSequence getText() {
        return "Message for item " + counter;
    }
}
