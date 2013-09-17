package org.aitek.controller.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.aitek.controller.core.Movie;
import org.aitek.controller.ui.GenericProgressIndicator;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.Logger;
import org.aitek.controller.mede8er.Mede8erCommander;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
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

    private List<String> genres;
    private BufferedReader bufferedReader;
    private int fileLength;
    private int read = 0;
    private Mede8erCommander mede8erCommander;
    private String text;

    public MovieLoader(Context context) throws Exception {
        super(context);
    }

    @Override
    public boolean setup() {

        mede8erCommander = Mede8erCommander.getInstance(context);
        if (!mede8erCommander.isUp()) {
            return false;
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        text = "Loading genres..";

        try {
            FileInputStream in = context.openFileInput(Constants.MOVIES_FILE);
            fileLength = in.available();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            genres = Arrays.asList(line.split(", "));

        }
        catch (FileNotFoundException e) {
            Logger.log("Error: " + e.getMessage());
            return false;
        }
        catch (IOException e) {
            Logger.log("Error: " + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public int next() throws Exception {

        text = "Loading movies..";
        String line = bufferedReader.readLine();

        if (line != null) {
            String[] movieLine = line.split("\\|\\|");
            String title = movieLine[0] != null ? movieLine[0] : "NO TITLE";
            String filePath = movieLine[1];
            String movieGenres = movieLine.length > 2 ? movieLine[2] : "";
            String persons = movieLine.length > 3 ? movieLine[3] : "";
            // save xml to datafile
            String xml = "";
            String dirUri = URLEncoder.encode(filePath.substring(1), "utf-8").replace("+", "%20");
            int jukeboxNumber = 0;
            String address = "http://" + Mede8erCommander.getInstance(context).getMede8erIpAddress() + "/jukebox/" + jukeboxNumber + "/";
            URL url = new URL(address + dirUri + "/folder.jpg");
            try {
                InputStream inputStream = (InputStream) url.getContent();
                Bitmap thumbnail = BitmapFactory.decodeStream(inputStream, null, options);
                Movie movie = new Movie(address, filePath, title, thumbnail, movieGenres, persons, xml);
                mede8erCommander.getMoviesManager().insert(movie);
            }
            catch (FileNotFoundException e) {
                // if the image is not present, just skips the movie
            }
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
        mede8erCommander.getMoviesManager().sortGenres();
        //MainActivity.movieGridAdapter.notifyDataSetChanged();
    }

    @Override
    public CharSequence getText() {
        return text;
    }
}
