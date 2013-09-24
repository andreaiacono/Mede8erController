package org.aitek.controller.loaders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.aitek.controller.activities.MainActivity;
import org.aitek.controller.core.Movie;
import org.aitek.controller.ui.GenericProgressIndicator;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.mede8er.Mede8erCommander;
import org.aitek.controller.parsers.XmlParser;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/16/13
 * Time: 2:31 PM
 */
public class FileSystemScanner extends GenericProgressIndicator {

    private File[] list;
    private int listCounter = 0;
    private int fileNumber;

    public FileSystemScanner(MainActivity activity) {
        super(activity);
    }

    @Override
    public boolean setup() {
        list = new File(Constants.ROOT_DIRECTORY).listFiles();
        fileNumber = list.length;
        return true;
    }

    @Override
    public int next() throws Exception {

        saveMovieInfo(list[listCounter]);
        return (int) (100 * ((double) listCounter / fileNumber));
    }


    @Override
    public void finish() throws Exception {
        Mede8erCommander mede8erCommander = Mede8erCommander.getInstance(context);
        mede8erCommander.getMoviesManager().save();
        mede8erCommander.getMoviesManager().sortMovies();
        mede8erCommander.getMoviesManager().sortGenres();
    }

    private void saveMovieInfo(File f) throws Exception {

        listCounter++;
        if (f.isDirectory()) {

            File imageFile = new File(f.getAbsoluteFile() + "/folder.jpg");
            File xmlFile = new File(Constants.ROOT_DIRECTORY + "/" + f.getName() + f.getName() + ".xml");
            if (imageFile.exists() && xmlFile.exists()) {

                String xmlFilename = "file://" + Constants.ROOT_DIRECTORY + "/" + f.getName() + f.getName() + ".xml";
                InputStream xmlInputStream = (InputStream) new URL(xmlFilename).getContent();
                Movie movie = null; //XmlParser.parseMovie(xmlInputStream, "", context);
                //movie.setBaseUrl(f.getAbsolutePath());

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
