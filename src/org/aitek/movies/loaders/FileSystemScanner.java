package org.aitek.movies.loaders;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.aitek.movies.core.Movie;
import org.aitek.movies.core.MoviesManager;
import org.aitek.movies.utils.Constants;
import org.aitek.movies.utils.XmlParser;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/16/13
 * Time: 2:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileSystemScanner implements Progressable {

    private File[] list;
    private int listCounter = 0;
    private int fileNumber;
    private Activity activity;

    @Override
    public void setup(Activity activity) {
        this.activity = activity;
        list = new File(Constants.ROOT_DIRECTORY).listFiles();
        fileNumber = list.length;
    }

    @Override
    public int next() throws Exception {

        saveMovieInfo(list[listCounter]);
        return (int) (100 * ((double) listCounter / fileNumber));
    }


    @Override
    public void finish() throws Exception {
        MoviesManager.saveMovies(activity);
        MoviesManager.sortMovies();
        MoviesManager.sortGenres();
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    private void saveMovieInfo(File f) throws Exception {

        listCounter++;
        if (f.isDirectory()) {

            File imageFile = new File(f.getAbsoluteFile() + "/folder.jpg");
            File xmlFile = new File(Constants.ROOT_DIRECTORY + "/" + f.getName() + f.getName() + ".xml");
            if (imageFile.exists() && xmlFile.exists()) {

                String xmlFilename = "file://" + Constants.ROOT_DIRECTORY + "/" + f.getName() + f.getName() + ".xml";
                InputStream xmlInputStream = (InputStream) new URL(xmlFilename).getContent();
                Movie movie = XmlParser.parse(xmlInputStream, activity.getApplicationContext());
                movie.setAbsolutePath(f.getAbsolutePath());

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;

                String imageFilename = "file://" + f.getAbsoluteFile() + "/folder.jpg";
                InputStream inputStream = (InputStream) new URL(imageFilename).getContent();
                Bitmap thumbnail = BitmapFactory.decodeStream(inputStream, null, options);
                movie.setThumbnail(thumbnail);
            }
        }
    }
}
