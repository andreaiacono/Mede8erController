package org.aitek.controller.mede8er;

import android.content.Context;
import org.aitek.controller.core.*;
import org.aitek.controller.mede8er.net.Mede8erConnector;
import org.aitek.controller.mede8er.net.Response;
import org.aitek.controller.parsers.JsonParser;
import org.aitek.controller.utils.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private int scanStep;
    private List<Jukebox> jukeboxes;
    private int elementCounter;
    private int jukeboxCounter;
    private int totalElements;
    private Context context;
    private int parsedElements;

    private Mede8erCommander(Context context) {
        this.context = context;
        jukeboxes = new ArrayList<>();
        mede8erConnector = new Mede8erConnector(context);
    }

    public static Mede8erCommander getInstance(Context context) {

        if (mede8erCommander == null) {
            mede8erCommander = new Mede8erCommander(context);
        }

        return mede8erCommander;
    }

    public boolean isMede8erUp() {
        try {
            mede8erConnector.retrieveMede8erIpAddress();
            return true;
        }
        catch (IOException e) {
            Logger.log("Mede8er is not connected:" + e.getMessage());
            return false;
        }
    }

    public boolean isMede8erConnectionAlive() {
        try {
            mede8erConnector.send(Command.STATUS, "");
            return true;
        }
        catch (IOException e) {
            return false;
        }
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

    public int scanJukeboxes() throws Exception {

        switch (scanStep) {

            // STEP 0
            case 0:
                Logger.log("Step 0");
                Response response = jukeboxCommand(JukeboxCommand.QUERY);
                if (response.getContent().toUpperCase().equals("EMPTY")) {
                    throw new Exception("Mede8er says there are no jukeboxes. Maybe the NAS is not connected?");
                }
                jukeboxes = JsonParser.getJukeboxes(response.getContent());
                scanStep++;
                return 5;

            // STEP 1
            case 1:
                Logger.log("Step 1");
                totalElements = 0;
                for (Jukebox jukebox : jukeboxes) {
                    response = jukeboxCommand(JukeboxCommand.OPEN, jukebox.getId());
                    jukebox.setJsonContent(new JSONObject(response.getContent()));
                    totalElements += jukebox.getLength();
                }
                parsedElements = 0;
                elementCounter = 0;
                jukeboxCounter = 0;
                scanStep++;
                return 10;

            // STEP 2
            case 2:
                Logger.log("Step 2");
                Logger.log("getting jukebox " + jukeboxCounter);

                if (jukeboxCounter >= jukeboxes.size()) {
                    return 100;
                }

                Jukebox jukebox = jukeboxes.get(jukeboxCounter);

                // creates the element
                String url = "http://" + Mede8erCommander.getInstance(context).getMede8erIpAddress();
                JSONObject meta = jukebox.getJsonContent().optJSONObject("meta");
                url = url + meta.optString("subdir");
                Element element = JsonParser.getElement(context, jukebox.getElement(elementCounter), url, meta.optString("fanart"), meta.optString("thumb"));
                if (element != null) {
                    insertElement(element);
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
                Logger.log("percent=" + percent + " parsed=" + parsedElements + " total=" + totalElements);
                return percent;
        }

        Logger.log("has arrived here.. :-(");

        // should not arrive here
        return 100;
    }

    public void insertElement(Element element) throws Exception {

        switch (element.getType()) {
            case MOVIE_FOLDER:
                getMoviesManager().insert((Movie) element);
                break;
        }
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
}
