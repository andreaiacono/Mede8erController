package org.aitek.controller.mede8er;

import android.content.Context;
import android.os.AsyncTask;
import org.aitek.controller.core.MoviesManager;
import org.aitek.controller.core.MusicManager;
import org.aitek.controller.mede8er.net.Mede8erConnector;
import org.aitek.controller.mede8er.net.Response;
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

    private Mede8erCommander(Context context, boolean launch) {
        this.context = context;
        mede8erConnector = new Mede8erConnector(context, launch);
    }

    public static Mede8erCommander getInstance(Context context) {

        return getInstance(context, false);
    }

    public static Mede8erCommander getInstance(Context context, boolean launch) {

        if (mede8erCommander == null) {
            mede8erCommander = new Mede8erCommander(context, launch);
        }

        return mede8erCommander;
    }

    public void launchConnector() {
        mede8erConnector.launchConnector();
        try {
            Logger.both("Querying mede8er jukeboxes...", context);
            jukeboxCommand(JukeboxCommand.QUERY, true);
        }
        catch (IOException e) {
            Logger.both("Error querying mede8er jukeboxes: " + e.getMessage(), context);
            e.printStackTrace();
        }

    }

    public boolean isUp() {
        return mede8erConnector.isUp();
    }

    public boolean isConnected() {
        return mede8erConnector.isConnected();
    }

    public void playMovieDir(String movieDir) throws IOException {
        String command = Command.PLAY.toString().toLowerCase() + " <moviedir>" + movieDir + "</moviedir>";
        new CommandLauncher().execute(command);
    }

    public void playFile(String file) throws IOException {
        String command = Command.PLAY.toString().toLowerCase() + " <file>" + file + "</file>";
        new CommandLauncher().execute(command);
    }

    public Response playFiles(String[] files) throws IOException {
        StringBuilder argument = new StringBuilder();
        for (String file : files) {
            argument.append("<file>").append(file).append("</file>");
        }
        return mede8erConnector.send(Command.PLAY.toString().toLowerCase() + " " + argument.toString());
    }

    public Response jukeboxCommand(JukeboxCommand jukeboxCommand, boolean inBackground) throws IOException {
        return jukeboxCommand(Command.JUKEBOX.toString().toLowerCase() + " " + jukeboxCommand.toString().toLowerCase() + " entry 0", inBackground);
    }

    public Response jukeboxCommand(JukeboxCommand jukeboxCommand, String id, boolean inBackground) throws IOException {
        return jukeboxCommand(Command.JUKEBOX.toString().toLowerCase() + " " + jukeboxCommand.toString().toLowerCase() + " " + id, inBackground);
    }

    public void movieCommand(MovieCommand movieCommand, String param) {
        String command = Command.MOVIE.toString().toLowerCase() + " " + movieCommand.toString().toLowerCase() + (param != null ? " " + param : "");
        new CommandLauncher().execute(command);
    }

    public void remoteCommand(RemoteCommand remoteCommand, String param) {
        String command = Command.RC.toString().toLowerCase() + " " + remoteCommand.getRemoteCommand() + (param != null ? " " + param : "");
        new CommandLauncher().execute(command);
    }

    public void remoteCommand(RemoteCommand remoteCommand) {
        remoteCommand(remoteCommand, null);
    }

    public MoviesManager getMoviesManager() {
        if (moviesManager == null) {
            try {
                moviesManager = new MoviesManager(context);
            }
            catch (Exception e) {
                e.printStackTrace();
                Logger.toast("An error has occurred getting MoviesManager: " + e.getMessage(), context);
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

    public Response jukeboxCommand(String command, boolean inBackground) throws IOException {
        if (inBackground) {
            new CommandLauncher().execute(command);
            return null;
        }
        else {
            return mede8erConnector.send(command);
        }
    }

    public void getMovieLength(Callbackable callbackable) throws Exception {
        String command = Command.STATUS.toString().toLowerCase() + " movietime ";
        new CommandLauncher(callbackable).execute(command);
    }

    public void openJukebox(String id) throws IOException {
        jukeboxCommand(JukeboxCommand.OPEN, id, false);
    }

    public class CommandLauncher extends AsyncTask<String, Void, Response> {

        private Callbackable callbackable;

        public CommandLauncher() {
        }

        public CommandLauncher(Callbackable callbackable) {
            this.callbackable = callbackable;
        }

        @Override
        protected Response doInBackground(String... strings) {
            try {
                Logger.log("Launched command [" + strings[0] + "].");
                return mede8erConnector.send(strings[0]);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Response response) {
            if (callbackable != null) {
                callbackable.callback(response);
            }
        }
    }
}
