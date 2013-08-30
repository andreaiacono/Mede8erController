package org.aitek.movies.loaders;

import android.app.Activity;
import org.aitek.movies.utils.Mede8erCommander;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/30/13
 * Time: 12:26 PM
 */
public class NetworkScanner extends GenericProgressIndicator {

    private Mede8erCommander mede8erCommander;
    private boolean initialized;

    public NetworkScanner(Activity activity) {
        super(activity);
    }

    @Override
    public int next() throws Exception {

        if (!initialized) {

            mede8erCommander = Mede8erCommander.getInstance(activity);
            initialized = true;
            return 1;
        }
        else {

            return mede8erCommander.scanJukebox();
        }
    }

    @Override
    public CharSequence getText() {
        if (!initialized) {
            return "Searching Mede8er media player on the network..";
        }
        else {
            return "Scanning jukebox metadata..";
        }
    }

    @Override
    public void finish() throws Exception {

    }

    @Override
    public Activity getActivity() {
        return activity;
    }
}
