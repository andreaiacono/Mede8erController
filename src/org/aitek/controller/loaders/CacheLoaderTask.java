package org.aitek.controller.loaders;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.Message;
import org.aitek.controller.activities.MainActivity;
import org.aitek.controller.core.Jukebox;
import org.aitek.controller.core.Movie;
import org.aitek.controller.mede8er.Mede8erCommander;
import org.aitek.controller.mede8er.Mede8erStatus;
import org.aitek.controller.utils.BitmapUtils;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.IoUtils;
import org.aitek.controller.utils.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/24/13
 * Time: 1:05 AM
 */


/**
 * The CacheLoaderTask class is responsible for loading the movies info from the cache on the device
 */
public class CacheLoaderTask extends AsyncTask<String, Void, String> {

    private MainActivity mainActivity;
    private CacheLoaderProgressIndicator cacheLoaderProgressIndicator;
    private boolean isSuccessful;

    public CacheLoaderTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPreExecute() {

        cacheLoaderProgressIndicator = new CacheLoaderProgressIndicator();
        cacheLoaderProgressIndicator.setup();
    }

    @Override
    protected String doInBackground(String... params) {
        Logger.log("Launching CacheLoader in bg");
        try {
            Looper.prepare();
            cacheLoaderProgressIndicator.run();
            isSuccessful = true;
        }
        catch (Exception e) {
            Logger.both("An error has occurred loading from cache: " + e.getMessage(), mainActivity);
            e.printStackTrace();
        }

        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        cacheLoaderProgressIndicator.finish();
        if (isSuccessful) {
            mainActivity.getDialogHandler().sendMessage(Message.obtain(mainActivity.getDialogHandler(), Mede8erStatus.SHOW_MOVIES));
        }
    }

    /**
     * Progress indicator for cache loader
     */
    class CacheLoaderProgressIndicator {

        private final ProgressDialog progressBar;
        BitmapFactory.Options options;
        private Context context;
        private List<String> genres;
        private Map<String, Jukebox> jukeboxes;
        private BufferedReader bufferedReader;
        private Mede8erCommander mede8erCommander;


        public CacheLoaderProgressIndicator() {

            this.context = mainActivity.getApplicationContext();

            progressBar = new ProgressDialog(mainActivity);
            progressBar.setCancelable(false);
            progressBar.setMessage("Loading movies from cache");
            progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressBar.setProgress(0);

            mede8erCommander = Mede8erCommander.getInstance(context);
        }

        public void setup() {

            try {
                FileInputStream in = context.openFileInput(Constants.MOVIES_FILE);
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                bufferedReader = new BufferedReader(inputStreamReader);
                String line;

                // the first rows contain the jukeboxes followed by a newline and then the genres, so has to be skipped for counting
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.equals("")) {
                        break;
                    }
                }

                // now just read the whole file for counting the movies (so to set the progress bar's max)
                int counter = -1;
                while ((bufferedReader.readLine()) != null) {
                    counter++;
                }
                bufferedReader.close();
                inputStreamReader.close();
                in.close();
                int max = counter;
                progressBar.setMax(max);
                progressBar.show();
            }
            catch (Exception e) {
                Logger.log("Error in setup Of MovieLoader: " + e.getMessage());
                e.printStackTrace();
            }
            Logger.log("Finished setup");

        }

        public boolean run() {

            Logger.log("Setting up MovieLoader");

            options = new BitmapFactory.Options();
            options.inSampleSize = 4;

            try {
                // now reads the file for real
                FileInputStream in = context.openFileInput(Constants.MOVIES_FILE);
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                bufferedReader = new BufferedReader(inputStreamReader);

                // creates jukeboxes
                jukeboxes = Jukebox.getJukeboxMap(bufferedReader, mede8erCommander.getMede8erIpAddress());
                Logger.log("Finished creating Jukebox map.");

                // now opens the jukeboxes
                for (String jukeboxId: jukeboxes.keySet()) {
                    mede8erCommander.openJukebox(jukeboxId);
                }

                // creates genres
                String line = bufferedReader.readLine();
                genres = Arrays.asList(line.split(", "));

                // now reads movies properties
                int counter = 0;
                while ((line = bufferedReader.readLine()) != null) {

                    // creates the movie
                    Movie movie = Movie.createFromDataFile(line, jukeboxes);

                    if (movie != null) {

                        // loads the thumbnail from the file system
                        String thumbnailFilename = IoUtils.normalizeFilename(Constants.THUMBS_DIRECTORY + movie.getFolder());
                        Bitmap thumbnail = IoUtils.readImageFile(thumbnailFilename);

                        // if not present
                        if (thumbnail == null) {

                            // loads it from the mede8er
                            String address = movie.getNameHttpAddress() + "/" + movie.getJukebox().getThumb();
                            URL url = new URL(address);
                            thumbnail = BitmapUtils.decodeBitmap(url, 100, 210);

                            // and saves it
                            IoUtils.saveImageFile(thumbnailFilename, thumbnail);
                        }
                        else {
//                        Logger.log("image " + thumbnailFilename + " loaded from cache.");
                        }
                        movie.setThumbnail(thumbnail);
                        mede8erCommander.getMoviesManager().insert(movie);
                    }
                    progressBar.setProgress(++counter);
                }

                bufferedReader.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                Logger.log("CacheLoaderProgress Error: " + e.getMessage());
                return false;
            }

            Logger.log("CacheLoader finished run");
            return true;
        }

        public void finish() {

            progressBar.dismiss();
            mede8erCommander.getMoviesManager().setGenres(genres);
            mede8erCommander.getMoviesManager().sortMovies();
            mede8erCommander.getMoviesManager().sortGenres();
            Logger.log("CacheLoader finished.");
        }

    }

}
