package org.aitek.controller.mede8er;

import android.app.Activity;
import org.aitek.controller.core.Element;
import org.aitek.controller.core.Jukebox;
import org.aitek.controller.core.Movie;
import org.aitek.controller.core.MoviesManager;
import org.aitek.controller.mede8er.net.Mede8erConnector;
import org.aitek.controller.mede8er.net.Response;
import org.aitek.controller.parsers.JsonParser;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/26/13
 * Time: 11:51 AM
 */
public class Mede8erCommander {

    private static Mede8erCommander mede8erCommander;
    private Mede8erConnector mede8erConnector;
    private MoviesManager moviesManager;
    private int scanStep;
    private Jukebox[] jukeboxes;
    private int jukeboxCounter;
    private int elements;
    private Activity activity;

    private Mede8erCommander(Activity activity) throws Exception {
        this.activity = activity;
        mede8erConnector = new Mede8erConnector(activity);
    }

    public static Mede8erCommander getInstance(Activity activity) throws Exception {

        if (mede8erCommander == null) {
            mede8erCommander = new Mede8erCommander(activity);
        }

        return mede8erCommander;
    }

    public Response playMovieDir(String movieDir) throws Exception {
        return mede8erConnector.send(Command.PLAY, "<moviedir>" + movieDir + "</movieDir>");
    }

    public Response playFile(String file) throws Exception {
        return mede8erConnector.send(Command.PLAY, "<file>" + file + "</file>");
    }

    public Response playFiles(String[] files) throws Exception {
        StringBuilder argument = new StringBuilder();
        for (String file : files) {
            argument.append("<file>").append(file).append("</file>");
        }
        return mede8erConnector.send(Command.PLAY, argument.toString());
    }

    public Response jukeboxCommand(JukeboxCommand jukeboxCommand) throws Exception {
        return mede8erConnector.send(Command.JUKEBOX, "entry");
    }

    public Response jukeboxCommand(JukeboxCommand jukeboxCommand, String id) throws Exception {
        return mede8erConnector.send(Command.JUKEBOX, jukeboxCommand.toString().toLowerCase() + " " + id);
    }

    public Response remoteCommand(RemoteCommand remoteCommand) throws Exception {
        return mede8erConnector.send(Command.RC, remoteCommand.getRemoteCommand());
    }

    public int scanJukeboxes() throws Exception {

        switch (scanStep) {

            // STEP 1
            case 0:
                Response response = jukeboxCommand(JukeboxCommand.QUERY);
                if (response.getContent().equals("EMPTY")) {
                    return -1;
                }
                jukeboxes = JsonParser.getJukeboxes(response.getContent());
                jukeboxCounter = 0;
                scanStep++;
                return 5;

            case 1:

                // STEP 2
                elements = 0;
                for (Jukebox jukebox : jukeboxes) {
                    response = jukeboxCommand(JukeboxCommand.OPEN, jukebox.getId());
                    jukebox.setJsonContent(response.getContent());
                    elements += jukebox.getLength();
                }
                return 10;

            // STEP 3
            case 2:
                for (Jukebox jukebox : jukeboxes) {

                    if (jukeboxCounter > jukebox.getLength()) {
                        continue;
                    }

                    // creates the element
                    String json = jukebox.getElement(jukeboxCounter);
                    Element element = JsonParser.getElement(json);
                    insertElement(element);

                    if (jukeboxCounter == jukebox.getLength() - 1) {
                        jukeboxCounter = 0;
                    } else {
                        jukeboxCounter++;
                    }

                    return 10 + (90 - (int) (90 * ((double) jukeboxes.length / jukeboxCounter)));
                }

                // if arrives here, all elements has been parsed
                return 100;
        }

        // should be not arrive here
        return -1;
    }

    private void insertElement(Element element) throws Exception {

        switch (element.getType()) {
            case MOVIE_FOLDER:
                getMoviesManager().insertMovie((Movie) element);
                break;
        }
    }

    public MoviesManager getMoviesManager() {
        if (moviesManager == null) {
            try {
                moviesManager = new MoviesManager(activity);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return moviesManager;
    }
}
