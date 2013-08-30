package org.aitek.movies.utils;

import android.app.Activity;
import org.aitek.movies.core.MoviesManager;
import org.aitek.movies.net.Command;
import org.aitek.movies.net.Mede8erConnector;
import org.aitek.movies.net.RemoteCommand;
import org.aitek.movies.net.Response;

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

    public Response sendRemoteCommand(RemoteCommand remoteCommand) throws Exception {
        return mede8erConnector.send(Command.RC, remoteCommand.getRemoteCommand());
    }

    public int scanJukebox() {

        return 0;
    }

    public MoviesManager getMoviesManager() {
        return moviesManager;
    }
}
