package org.aitek.controller.loaders;

import android.content.Context;
import org.aitek.controller.mede8er.Mede8erCommander;
import org.aitek.controller.ui.GenericProgressIndicator;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/30/13
 * Time: 12:26 PM
 */
public class Mede8erScanner extends GenericProgressIndicator {

    private Mede8erCommander mede8erCommander;
    private boolean initialized;

    public Mede8erScanner(Context context) {
        super(context);
    }

    @Override
    public int next() throws Exception {

        if (!initialized) {

            mede8erCommander = Mede8erCommander.getInstance(context);
            initialized = true;
            return 1;
        }
        else {

            return mede8erCommander.scanJukeboxes();
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

        mede8erCommander.getMoviesManager().save();
    }

}
