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
public interface Progressable {

    /**
     * setups the progressable
     * @param activity
     * @return
     * @throws Exception
     */
    public void setup(Activity activity) throws Exception;

    /**
     * processes the next element of the set
     * @return
     * @throws Exception
     */
    public int next() throws Exception;


    /**
     * finishes the execution
     * @throws Exception
     */
    public void finish() throws Exception;


    public Activity getActivity();
}
