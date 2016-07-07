package org.aitek.controller.loaders;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.Message;
import org.aitek.controller.activities.MainActivity;
import org.aitek.controller.core.Jukebox;
import org.aitek.controller.core.Movie;
import org.aitek.controller.mede8er.JukeboxCommand;
import org.aitek.controller.mede8er.Mede8erCommander;
import org.aitek.controller.mede8er.Mede8erStatus;
import org.aitek.controller.mede8er.net.Response;
import org.aitek.controller.parsers.JsonParser;
import org.aitek.controller.utils.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.aitek.controller.mede8er.Mede8erStatus.SHOW_MOVIES;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 1/4/14
 * Time: 4:19 PM
 */
public class Mede8erLoaderTask extends AsyncTask<String, Void, String> {

    private MainActivity mainActivity;
    private Mede8erLoaderProgressIndicator mede8erLoaderProgressIndicator;

    public Mede8erLoaderTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPreExecute() {

        mede8erLoaderProgressIndicator = new Mede8erLoaderProgressIndicator();
    }

    @Override
    protected String doInBackground(String... params) {
        Logger.log("Launching Mede8erLoader in bg");
        try {
            Looper.prepare();
            //Mede8erCommander.getInstance(mainActivity.getApplicationContext(), false).getMoviesManager().clear();
            mede8erLoaderProgressIndicator.setup();
            mede8erLoaderProgressIndicator.countMovies();
            mede8erLoaderProgressIndicator.getMovies();
        }
        catch (Exception e) {
            Logger.both("An error has occurred scanning the mede8er: " + e.getMessage(), mainActivity);
            e.printStackTrace();
        }

        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        mede8erLoaderProgressIndicator.finish();
        mainActivity.getDialogHandler().sendMessage(Message.obtain(mainActivity.getDialogHandler(), SHOW_MOVIES));
    }

    /**
     * The progress indicator dialog for loading movies info from mede8er
     */
    class Mede8erLoaderProgressIndicator {

        private Context context;
        private Mede8erCommander mede8erCommander;
        private ProgressDialog progressBar;
        private List<Jukebox> jukeboxes;

        public Mede8erLoaderProgressIndicator() {
            this.context = mainActivity.getApplicationContext();
            jukeboxes = new ArrayList<Jukebox>();

            progressBar = new ProgressDialog(mainActivity);
            progressBar.setCancelable(false);
            progressBar.setMessage("Loading movies from mede8er");
            progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressBar.setProgress(0);
            progressBar.show();

            mede8erCommander = Mede8erCommander.getInstance(context);
        }

        public void setup() throws StatusException {

            Logger.log("Step 0");
            try {
                Response response = mede8erCommander.jukeboxCommand(JukeboxCommand.QUERY, false);
                if (response.isEmpty()) {
                    throw new StatusException(Mede8erStatus.NO_JUKEBOX);
                }
                jukeboxes = JsonParser.getJukeboxes(response.getContent(), mede8erCommander.getMede8erIpAddress());
            }
            catch (JSONException e) {
                Logger.both("Error in setup of mede8er loader: " + e.getMessage(), context);
                e.printStackTrace();
            }
            catch (IOException ioe) {
                Logger.both("Error parsing data for mede8er loader: " + ioe.getMessage(), context);
                ioe.printStackTrace();
            }
        }

        public void countMovies() {

            Logger.log("Step 1");
            int totalElements = 0;
            for (Jukebox jukebox : jukeboxes) {
                try {
                    Response response = mede8erCommander.jukeboxCommand(JukeboxCommand.OPEN, jukebox.getId(), false);
                    jukebox.setJsonContent(new JSONObject(response.getContent()));
                    totalElements += jukebox.getLength();
                }
                catch (IOException e) {
                    Logger.both("IOError counting movies: " + e.getMessage(), context);
                    e.printStackTrace();
                }
                catch (Exception e) {
                    Logger.both("Error counting movies: " + e.getMessage(), context);
                    e.printStackTrace();
                }
            }
            progressBar.setMax(totalElements);

        }

        public void getMovies() throws Exception {

            int counter = 0;

            // loops over all the jukeboxes
            for (Jukebox jukebox : jukeboxes) {

                JSONObject meta = jukebox.getJsonContent().optJSONObject("meta");

                // if info is not complete, completes the jukebox object
                if (jukebox.getFanart() == null) {
                    jukebox.setFanart(meta.optString("fanart"));
                    jukebox.setThumb(meta.optString("thumb"));
                    jukebox.setAbsolutePath(meta.optString("absolute_path"));
                    jukebox.setSubdir(meta.optString("subdir"));
                }

                // loops over all the items of the jukebox
                for (int elementCounter = 0; elementCounter < jukebox.getLength(); elementCounter++) {

                    // it's a movie
                    if (jukebox.getElement(elementCounter).optJSONArray("video").length() > 0) {

                        Movie movie = JsonParser.getMovie(jukebox, elementCounter);

                        if (movie != null) {
                            mede8erCommander.getMoviesManager().insertGenres(movie.getGenres());
                            mede8erCommander.getMoviesManager().insert(movie);

                            // loads the thumb from the mede8er
                            String address = movie.getNameHttpAddress() + "/" + movie.getJukebox().getThumb();
                            URL url = new URL(address);
                            Bitmap thumbnail = BitmapUtils.decodeBitmap(url, 100, 210);

                            // and saves it to cache
                            String thumbnailFilename = IoUtils.normalizeFilename(Constants.THUMBS_DIRECTORY + movie.getFolder());
                            IoUtils.saveImageFile(thumbnailFilename, thumbnail);
                            movie.setThumbnail(thumbnail);
                        }
                    }

                    progressBar.setProgress(++counter);
                }
            }
        }

        public void finish() {

            progressBar.dismiss();
            mede8erCommander.getMoviesManager().setJukeboxes(jukeboxes);
            mede8erCommander.getMoviesManager().sortMovies();
            mede8erCommander.getMoviesManager().sortGenres();

            try {
                mede8erCommander.getMoviesManager().save();
            }
            catch (Exception e) {
                Logger.both("Error during MovieManager save: " + e.getMessage(), context);
                e.printStackTrace();
            }
        }

    }
}
