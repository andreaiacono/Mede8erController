package org.aitek.controller.loaders;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.aitek.controller.activities.MainActivity;
import org.aitek.controller.core.Movie;
import org.aitek.controller.ui.GenericProgressIndicator;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.Logger;
import org.aitek.controller.mede8er.Mede8erCommander;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/24/13
 * Time: 1:05 AM
 */


/**
 * The MovieLoader class is responsible for loading the controller info from the datafile.
 */
public class MovieLoader extends GenericProgressIndicator {
    BitmapFactory.Options options;
    private Activity activity;
    private List<String> genres;
    private BufferedReader bufferedReader;
    private int fileLength;
    private int read = 0;
    private Mede8erCommander mede8erCommander;

    public MovieLoader(Activity activity) throws Exception {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void setup() throws Exception {

        mede8erCommander = Mede8erCommander.getInstance(activity);
        options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        try {
            FileInputStream in = activity.openFileInput(Constants.MOVIES_FILE);
            fileLength = in.available();
            Logger.log("fileLength=" + fileLength);
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            Logger.log("line=" + line);
            genres = Arrays.asList(line.split(", "));
            mede8erCommander.getMoviesManager().setMovieGenres(genres);
        }
        catch (FileNotFoundException e) {
            Logger.log("Error: " + e.getMessage());
        }
    }

    @Override
    public int next() throws Exception {

        String line = bufferedReader.readLine();

        if (line != null) {
            String[] movieLine = line.split("\\|\\|");
            String title = movieLine[0] != null ? movieLine[0] : "NO TITLE";
            String filePath = movieLine[1];
            String movieGenres = movieLine[2];
            String persons = movieLine[3];

            URL url = new URL("file://" + filePath + "/folder.jpg");
            InputStream inputStream = (InputStream) url.getContent();
            Bitmap thumbnail = BitmapFactory.decodeStream(inputStream, null, options);
            Movie movie = new Movie(filePath, title, thumbnail, movieGenres, persons);
            mede8erCommander.getMoviesManager().insertMovie(movie);
            read += line.length();
        }
        else {
            read = fileLength;
        }
        return (int) (100 * ((double) read / fileLength));
    }

    @Override
    public void finish() throws Exception {
        bufferedReader.close();
        mede8erCommander.getMoviesManager().sortMovies();
        mede8erCommander.getMoviesManager().sortMovieGenres();
        MainActivity.imageAdapter.notifyDataSetChanged();
    }
}
