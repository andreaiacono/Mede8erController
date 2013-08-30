package org.aitek.movies.loaders;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import org.aitek.movies.activities.MainActivity;
import org.aitek.movies.core.Movie;
import org.aitek.movies.core.MoviesManager;
import org.aitek.movies.utils.Constants;
import org.aitek.movies.utils.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/24/13
 * Time: 1:05 AM
 * To change this template use File | Settings | File Templates.
 */


/**
 * The MovieLoader class is responsible for loading the movies info from the datafile.
 */
public class MovieLoader implements Progressable {
    BitmapFactory.Options options;
    private Activity activity;
    private List<String> genres;
    private BufferedReader bufferedReader;
    private int fileLength;
    private int read = 0;

    @Override
    public void setup(Activity activity) throws Exception {
        this.activity = activity;

        options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        FileInputStream in = activity.openFileInput(Constants.MOVIES_FILE);
        fileLength = in.available();
        Logger.log("fileLength=" + fileLength);
        InputStreamReader inputStreamReader = new InputStreamReader(in);

        bufferedReader = new BufferedReader(inputStreamReader);
        String line = bufferedReader.readLine();
        Logger.log("line=" + line);
        genres = Arrays.asList(line.split(", "));
        MoviesManager.setGenres(genres);
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
            MoviesManager.insertMovie(movie);
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
        MoviesManager.sortMovies();
        MoviesManager.sortGenres();
        MainActivity.imageAdapter.notifyDataSetChanged();
    }

    @Override
    public Activity getActivity() {
        return activity;
    }
}
