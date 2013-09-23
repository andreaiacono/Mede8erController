package org.aitek.controller.mede8er;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import org.aitek.controller.core.*;
import org.aitek.controller.loaders.ImageShowerTask;
import org.aitek.controller.mede8er.net.Mede8erConnector;
import org.aitek.controller.mede8er.net.Response;
import org.aitek.controller.utils.BitmapUtils;
import org.aitek.controller.utils.Logger;

import java.io.IOException;

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

    public void playMovieDir(String movieDir) throws Exception {
        String command = Command.PLAY.toString().toLowerCase() + " <moviedir>" + movieDir + "</movieDir>";
        new CommandLauncher().execute(command);
    }

    public void playFile(String file) throws Exception {
        String command = Command.PLAY.toString().toLowerCase() + " <file>" + file + "</file>";
        new CommandLauncher().execute(command);
    }

    public Response playFiles(String[] files) throws Exception {
        StringBuilder argument = new StringBuilder();
        for (String file : files) {
            argument.append("<file>").append(file).append("</file>");
        }
        return mede8erConnector.send(Command.PLAY.toString().toLowerCase() + " " + argument.toString());
    }

    public Response jukeboxCommand(JukeboxCommand jukeboxCommand) throws Exception {
        return jukeboxCommand(jukeboxCommand, "entry 0");
    }

    public Response jukeboxCommand(JukeboxCommand jukeboxCommand, String id) throws Exception {
        return mede8erConnector.send(Command.JUKEBOX.toString().toLowerCase() + " " + jukeboxCommand.toString().toLowerCase() + " " + id);
    }

    public Response remoteCommand(RemoteCommand remoteCommand) throws Exception {
        return mede8erConnector.send(Command.RC.toString().toLowerCase() + " " + remoteCommand.getRemoteCommand());
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

    public void connectToMede8er(boolean asNewThread) {
        mede8erConnector.connectToMede8er(asNewThread);
    }

    public class CommandLauncher extends AsyncTask<String, Void, Response> {

        @Override
        protected Response doInBackground(String... strings) {
            try {
                Logger.log("Launched command [" + strings[0] + "].");
                return mede8erConnector.send(strings[0]);
            }
            catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return null;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
        }
    }
}
