package org.aitek.movies.utils;

import android.app.Activity;
import org.aitek.movies.core.Jukebox;
import org.aitek.movies.core.MoviesManager;
import org.aitek.movies.loaders.JsonParser;
import org.aitek.movies.net.*;

import java.net.InetAddress;

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
    private String scannedId;
    private Jukebox[] jukeboxes;
    private int jukeboxCounter;

    private Mede8erCommander(Activity activity) throws Exception {
        mede8erConnector = new Mede8erConnector(activity);
        moviesManager = new MoviesManager(activity);
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
        for (String file: files) {
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

    public int scanJukebox() throws Exception {

        switch (scanStep)  {
            case 0:
                Response response = jukeboxCommand(JukeboxCommand.QUERY);
                if (response.getContent().equals("EMPTY")) {
                    return -1;
                }
                jukeboxes = JsonParser.getJukeboxes(response.getContent());
                jukeboxCounter = 0;
                scanStep++;
                return 10;

            case 1:
                response = jukeboxCommand(JukeboxCommand.OPEN, jukeboxes[jukeboxCounter].getId());

                jukeboxCounter++;
                if (jukeboxCounter == jukeboxes.length-1) {
                    scanStep++;
                }
                return 10 + (90 - (int) (90 * ((double)jukeboxes.length/jukeboxCounter)));
            case 2:
                return 100;
        }

        return 0;
    }

    public MoviesManager getMoviesManager() {
        return moviesManager;
    }
}
