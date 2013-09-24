package org.aitek.controller.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.aitek.controller.core.Jukebox;
import org.aitek.controller.core.Movie;
import org.aitek.controller.mede8er.JukeboxCommand;
import org.aitek.controller.mede8er.Mede8erCommander;
import org.aitek.controller.ui.GenericProgressIndicator;
import org.aitek.controller.utils.BitmapUtils;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
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
 * The MovieLoader class is responsible for loading the controller info from the datafile.
 */
public class MovieLoader extends GenericProgressIndicator {
    BitmapFactory.Options options;
    private List<String> genres;
    private Map<String, Jukebox> jukeboxes;
    private BufferedReader bufferedReader;
    private int read = 0;
    private Mede8erCommander mede8erCommander;
    private String text;

    public MovieLoader(Context context) throws Exception {
        super(context);
    }

    @Override
    public boolean setup() {

        mede8erCommander = Mede8erCommander.getInstance(context);
        Logger.log("checking for mede8er up");
        if (!mede8erCommander.isUp()) {
            mede8erCommander.connectToMede8er(true);
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        text = "Counting movies..";

        try {
            // first, opens the jukebox
            mede8erCommander.jukeboxCommand(JukeboxCommand.QUERY, false);
            mede8erCommander.jukeboxCommand(JukeboxCommand.OPEN, "0", false);

            FileInputStream in = context.openFileInput(Constants.MOVIES_FILE);
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            bufferedReader = new BufferedReader(inputStreamReader);

            // the first rows contain the jukeboxes followed by te genres, so has to be skipped for counting
            while (!bufferedReader.readLine().equals("\n")) ;

            // now just read the whole file for counting the movies
            int counter = -1;
            while (bufferedReader.readLine() != null) {
                counter++;
            }
            bufferedReader.close();
            inputStreamReader.close();
            in.close();
            max = counter;

            in = context.openFileInput(Constants.MOVIES_FILE);
            inputStreamReader = new InputStreamReader(in);
            bufferedReader = new BufferedReader(inputStreamReader);
            jukeboxes = Jukebox.getJukeboxMap(bufferedReader, mede8erCommander.getMede8erIpAddress());

            String line = bufferedReader.readLine();
            Logger.log("read genres: " + line);
            genres = Arrays.asList(line.split(","));
        }
        catch (FileNotFoundException e) {
            Logger.log("Error: " + e.getMessage());
            read = -1;
            return false;
        }
        catch (IOException e) {
            Logger.log("Error: " + e.getMessage());
            read = -1;
            return false;
        }

        return true;
    }

    @Override
    public int next() throws Exception {

        if (read == -1) {
            return Integer.MAX_VALUE;
        }

        text = "Loading movies..";
        String line = bufferedReader.readLine();

        if (line != null) {

            // creates the movie
            Movie movie = Movie.createFromDataFile(line, jukeboxes);

            // loads the thumbnail
            String address = movie.getNameHttpAddress() + movie.getJukebox().getThumb();
            URL url = new URL(address);
            Bitmap thumbnail = BitmapUtils.decodeBitmap(url, 100, 210);

            movie.setThumbnail(thumbnail);
            mede8erCommander.getMoviesManager().insert(movie);
            read++;
        }
        return read;
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
