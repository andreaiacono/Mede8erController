package org.aitek.controller.mede8er;

import android.content.Context;
import org.aitek.controller.core.*;
import org.aitek.controller.mede8er.net.Mede8erConnector;
import org.aitek.controller.mede8er.net.Response;

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
    private MusicManager musicManager;
    private Context context;

    private Mede8erCommander(Context context) {
        this.context = context;
        mede8erConnector = new Mede8erConnector(context);
    }

    public static Mede8erCommander getInstance(Context context) {

        if (mede8erCommander == null) {
            mede8erCommander = new Mede8erCommander(context);
        }

        return mede8erCommander;
    }

    public boolean isUp() {
        return mede8erConnector.isUp();
    }

    public boolean isConnected() {
        return mede8erConnector.isConnected();
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
        return jukeboxCommand(jukeboxCommand, "entry 0");
    }

    public Response jukeboxCommand(JukeboxCommand jukeboxCommand, String id) throws Exception {
        return mede8erConnector.send(Command.JUKEBOX, jukeboxCommand.toString().toLowerCase() + " " + id);
    }

    public Response remoteCommand(RemoteCommand remoteCommand) throws Exception {
        return mede8erConnector.send(Command.RC, remoteCommand.getRemoteCommand());
    }

    public MoviesManager getMoviesManager() {
        if (moviesManager == null) {
            try {
                moviesManager = new MoviesManager(context);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return moviesManager;
    }

    public MusicManager getMusicManager() {
        if (musicManager == null) {
            try {
                musicManager = new MusicManager(context);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return musicManager;
    }

    public String getMede8erIpAddress() {
        return mede8erConnector.getInetAddress();
    }

    public void connectToMede8er() {
        mede8erConnector.connectToMede8er();
    }
}
