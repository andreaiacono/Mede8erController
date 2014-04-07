package org.aitek.controller.loaders;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import org.aitek.controller.activities.MainActivity;
import org.aitek.controller.core.Jukebox;
import org.aitek.controller.core.Movie;
import org.aitek.controller.mede8er.JukeboxCommand;
import org.aitek.controller.mede8er.Mede8erCommander;
import org.aitek.controller.mede8er.Mede8erStatus;
import org.aitek.controller.mede8er.net.Response;
import org.aitek.controller.parsers.JsonParser;
import org.aitek.controller.ui.GenericProgressIndicator;
import org.aitek.controller.utils.Logger;
import org.aitek.controller.utils.StatusException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static org.aitek.controller.mede8er.Mede8erStatus.FULLY_OPERATIONAL;
import static org.aitek.controller.mede8er.Mede8erStatus.NO_JUKEBOX;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/30/13
 * Time: 12:26 PM
 */
public class Mede8erScannerProgressIndicator extends GenericProgressIndicator {

    private Mede8erCommander mede8erCommander;
    private boolean initialized;
    private int scanStep;
    private List<Jukebox> jukeboxes;
    private int elementCounter;
    private int jukeboxCounter;
    private int totalElements;
    private int parsedElements;


    public Mede8erScannerProgressIndicator(Context context) {
        super(context);
        jukeboxes = new ArrayList<Jukebox>();
    }

    @Override
    public int next() throws Exception {

        if (!initialized) {
            mede8erCommander = Mede8erCommander.getInstance(context);
            mede8erCommander.getMoviesManager().clear();
            initialized = true;
            return 1;
        } else {

            try {
                return scanJukeboxes();
            }
            catch (StatusException e) {
                if (e.getStatus() == Mede8erStatus.NO_JUKEBOX) {
                    Handler dialogHandler = ((MainActivity) context).getDialogHandler();
                    dialogHandler.sendMessage(Message.obtain(dialogHandler, NO_JUKEBOX));
                }
                return 100;
            }
        }
    }

    private int scanJukeboxes() throws Exception {

        switch (scanStep) {

            // STEP 0: queries the mede8er for jukeboxes
            case 0:
                Logger.log("Step 0");
                Response response = mede8erCommander.jukeboxCommand(JukeboxCommand.QUERY, false);
                if (response.getContent().toUpperCase().equals("EMPTY")) {
                    throw new StatusException(Mede8erStatus.NO_JUKEBOX);
                }
                jukeboxes = JsonParser.getJukeboxes(response.getContent(), mede8erCommander.getMede8erIpAddress());
                scanStep++;
                return 5;

            // STEP 1: computes the length of processing of all the jukeboxes
            case 1:
                Logger.log("Step 1");
                totalElements = 0;
                for (Jukebox jukebox : jukeboxes) {
                    response = mede8erCommander.jukeboxCommand(JukeboxCommand.OPEN, jukebox.getId(), false);
                    jukebox.setJsonContent(new JSONObject(response.getContent()));
                    totalElements += jukebox.getLength();
                }
                parsedElements = 0;
                elementCounter = 0;
                jukeboxCounter = 0;
                scanStep++;
                return 10;

            // STEP 2: processes every element of all the jukeboxes
            case 2:
                if (jukeboxCounter >= jukeboxes.size()) {

                    return 100;
                }
                Jukebox jukebox = jukeboxes.get(jukeboxCounter);
                JSONObject meta = jukebox.getJsonContent().optJSONObject("meta");

                // if not complete, completes the jukebox object
                if (jukebox.getFanart() == null) {
                    jukebox.setFanart(meta.optString("fanart"));
                    jukebox.setThumb(meta.optString("thumb"));
                    jukebox.setAbsolutePath(meta.optString("absolute_path"));
                    jukebox.setSubdir(meta.optString("subdir"));
                }

                // it's a movie
                if (jukebox.getElement(elementCounter).optJSONArray("video").length() > 0) {

                    Movie movie = JsonParser.getMovie(jukebox, elementCounter);

                    if (movie != null) {
                        mede8erCommander.getMoviesManager().insertGenres(movie.getGenres());
                        mede8erCommander.getMoviesManager().insert(movie);
                    }
                }

                if (elementCounter == jukebox.getLength() - 1) {
                    Logger.log("finished jukebox " + jukeboxCounter);
                    elementCounter = 0;
                    jukeboxCounter++;
                } else {
                    elementCounter++;
                }
                parsedElements++;

                int percent = 10 + ((int) (90 * ((double) parsedElements / totalElements)));
                return percent;
        }

        Logger.log("should not arrive here.");
        return 100;
    }

    @Override
    public CharSequence getText() {
        if (!initialized) {
            return "Searching Mede8er media player on the network..";
        } else {
            return "Scanning jukebox metadata..";
        }
    }

    @Override
    public void finish() throws Exception {

        Logger.log("Mede8erScanner finished. Now saving movie file and creating page.");
        mede8erCommander.getMoviesManager().setJukeboxes(jukeboxes);
        mede8erCommander.getMoviesManager().save();
        mede8erCommander.getMoviesManager().sortMovies();
        mede8erCommander.getMoviesManager().sortGenres();

        ((MainActivity) context).getDialogHandler().sendMessage(Message.obtain(((MainActivity) context).getDialogHandler(), FULLY_OPERATIONAL));
    }

}
