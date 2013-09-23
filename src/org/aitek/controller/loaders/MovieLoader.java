package org.aitek.controller.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.aitek.controller.core.Movie;
import org.aitek.controller.ui.GenericProgressIndicator;
import org.aitek.controller.utils.BitmapUtils;
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
        Logger.log("checking for mede8er up");
        if (!mede8erCommander.isUp()) {
            mede8erCommander.connectToMede8er(true);
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        text = "Loading genres..";

        try {
            FileInputStream in = context.openFileInput(Constants.MOVIES_FILE);
            fileLength = in.available();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();

            // first reads the file for having the movies number
            int counter =-1;
            while (bufferedReader.readLine() != null) {
                counter ++;
            }
            bufferedReader.close();
            inputStreamReader.close();
            in.close();
            max = counter;

            in = context.openFileInput(Constants.MOVIES_FILE);
            fileLength = in.available();
            inputStreamReader = new InputStreamReader(in);

            bufferedReader = new BufferedReader(inputStreamReader);
            line = bufferedReader.readLine();
            Logger.log("read genres: " + line);
            genres = Arrays.asList(line.split(","));

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
            String baseUrl =  movieLine.length > 1 ? movieLine[1] : " ";
            String dir =  movieLine.length > 2 ? movieLine[2] : " ";
            String folder =  movieLine.length > 3 ? movieLine[3] : "";
            String movieGenres = movieLine.length > 4 ? movieLine[4] : "";
            String persons = movieLine.length > 5 ? movieLine[5] : "";
            // save xml to datafile
            String xml = "";
            String dirUri = URLEncoder.encode(folder, "utf-8").replace("+", "%20");
            int jukeboxNumber = 0;
            String address = "http://" + Mede8erCommander.getInstance(context).getMede8erIpAddress() + "/jukebox/" + jukeboxNumber + "/";
            URL url = new URL(address + dirUri + "/folder.jpg");
            try {

//                InputStream inputStream = (InputStream) url.getContent();
                Bitmap thumbnail = BitmapUtils.decodeBitmap(url, 100, 210); //BitmapFactory.decodeStream(inputStream, null, options);
                Movie movie = new Movie(address, baseUrl, folder, title, thumbnail, movieGenres, persons, xml);
                movie.setDir(dir);
                mede8erCommander.getMoviesManager().insert(movie);
            }
            catch (FileNotFoundException e) {
                Logger.log("Skipping " + title + ": " + e.getMessage());
                // if the image is not present, just skips the movie
            }
//            read += line.length();
            read ++;
        }
//        else {
//            read = fileLength;
//        }
        return read; // (int) (100 * ((double) read / fileLength));
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
