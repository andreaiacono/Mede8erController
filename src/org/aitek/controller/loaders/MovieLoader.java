package org.aitek.controller.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import org.aitek.controller.activities.MainActivity;
import org.aitek.controller.core.Jukebox;
import org.aitek.controller.core.Movie;
import org.aitek.controller.mede8er.Mede8erCommander;
import org.aitek.controller.ui.GenericProgressIndicator;
import org.aitek.controller.utils.BitmapUtils;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.IoUtils;
import org.aitek.controller.utils.Logger;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.aitek.controller.mede8er.Status.ERROR;
import static org.aitek.controller.mede8er.Status.FULLY_OPERATIONAL;

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
    private Context context;
    private final MainActivity mainActivity;
    BitmapFactory.Options options;
    private List<String> genres;
    private Map<String, Jukebox> jukeboxes;
    private BufferedReader bufferedReader;
    private int read = 0;
    private Mede8erCommander mede8erCommander;
    private String text;


    public MovieLoader(Context context, MainActivity mainActivity) throws Exception {
        super(context);
        this.context = context;
        this.mainActivity = mainActivity;
    }

    @Override
    public boolean setup() {
        Logger.log("Setting up MovieLoader (max=" + max + ")");

        mede8erCommander = Mede8erCommander.getInstance(context);
        if (!mede8erCommander.isUp()) {
            mede8erCommander.connectToMede8er(false);
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        text = "Counting movies..";

        try {
            // first, opens the jukebox
//            mede8erCommander.jukeboxCommand(JukeboxCommand.QUERY, true);
//            mede8erCommander.jukeboxCommand(JukeboxCommand.OPEN, "0", true);

            FileInputStream in = context.openFileInput(Constants.MOVIES_FILE);
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            // the first rows contain the jukeboxes followed by a newline and then the genres, so has to be skipped for counting
            while ((line = bufferedReader.readLine()) != null) {
//                Logger.log("MOVIEFILE = " + line);
                if (line.equals("")) {
                    break;
                }
            }

            // now just read the whole file for counting the movies
            int counter = -1;
            while ((line = bufferedReader.readLine()) != null) {
//                Logger.log("MOVIEFILE2 = " + line);
                counter++;
            }
            bufferedReader.close();
            inputStreamReader.close();
            in.close();
            Logger.log("(max=" + max + ")");
            max = counter;
//            max = 10;
            Logger.log("(max=" + max + ")");

            in = context.openFileInput(Constants.MOVIES_FILE);
            inputStreamReader = new InputStreamReader(in);
            bufferedReader = new BufferedReader(inputStreamReader);
            jukeboxes = Jukebox.getJukeboxMap(bufferedReader, mede8erCommander.getMede8erIpAddress());

            line = bufferedReader.readLine();
            Logger.log("read genres: " + line + " (max=" + max + ")");
            genres = Arrays.asList(line.split(", "));
            Logger.log("henres=" + Arrays.toString(genres.toArray()));
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
        Logger.log("finished movieloader setup with max= " + max);
        return true;
    }

    @Override
    public int next() throws Exception {
        Logger.log("calling movieloader next ");

        if (read == -1) {
            return Integer.MAX_VALUE;
        }

        text = "Loading movies..";
        String line = bufferedReader.readLine();

        if (line != null) {

            // creates the movie
            Movie movie = Movie.createFromDataFile(line, jukeboxes);
            Logger.log("Read movie " + movie);

            if (movie != null) {

                // loads the thumbnail
                String address = movie.getNameHttpAddress() + "/" + movie.getJukebox().getThumb();
                String thumbnailFilename = IoUtils.normalizeFilename(Constants.THUMBS_DIRECTORY + movie.getFolder());

                // loads the thumbnail from the file system
                Bitmap thumbnail = IoUtils.readImageFile(thumbnailFilename);

                // if not present
                if (thumbnail == null) {

                    // loads it from the mede8er
                    URL url = new URL(address);
                    thumbnail = BitmapUtils.decodeBitmap(url, 100, 210);

                    // and saves it
                    IoUtils.saveImageFile(thumbnailFilename, thumbnail);
                    Logger.log("Saved image " + thumbnailFilename);
                }
                else {

                    Logger.log("image " + thumbnailFilename + " loaded from cache.");
                }
                movie.setThumbnail(thumbnail);
                mede8erCommander.getMoviesManager().insert(movie);
            }
            read++;
        }
        return read;
    }

    @Override
    public void finish() throws Exception {
        if (bufferedReader != null) {
            bufferedReader.close();
        }
        //Logger.log("henres in finish=" + Arrays.toString(genres.toArray()));
        mede8erCommander.getMoviesManager().setGenres(genres);
        mede8erCommander.getMoviesManager().sortMovies();
        mede8erCommander.getMoviesManager().sortGenres();

        mainActivity.getDialogHandler().sendMessage(Message.obtain(mainActivity.getDialogHandler(), FULLY_OPERATIONAL));
        //MainActivity.movieGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void fail() {
        mainActivity.getDialogHandler().sendMessage(Message.obtain(mainActivity.getDialogHandler(), ERROR));
    }

    @Override
    public CharSequence getText() {
        return text;
    }

    @Override
    public Context getContext() {
        return mainActivity;
    }

}
