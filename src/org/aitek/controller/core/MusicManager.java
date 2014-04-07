package org.aitek.controller.core;

import android.content.Context;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.Logger;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/17/13
 * Time: 11:17 PM
 */
public class MusicManager {

    private String genreFilter = null;
    private String genericFilter = null;
    private List<String> genres;
    private List<String> music;
    private List<Movie> filteredMusic = null;
    private String sortField;
    private boolean sortDescending;
    private Context context;

    public MusicManager(Context context) throws Exception {
        this.context = context;

        genreFilter = null;
        genericFilter = null;
        filteredMusic = null;
        sortField = "title";

    }

    public void clear() {
        genres = new ArrayList<String>();
        music = new ArrayList<String>();
        genreFilter = null;
        genericFilter = null;
        filteredMusic = null;
        sortField = "title";
        sortDescending = false;
    }

    public String[] getGenres() {
        List<String> allGenresList = new ArrayList<String>();
        allGenresList.add(Constants.ALL_MUSIC);
        allGenresList.addAll(genres);
        String genresArray[] = new String[allGenresList.size()];
        return allGenresList.toArray(genresArray);
    }


    public void saveMusic() throws Exception {

        final String NEWLINE = "\n";
        StringBuffer fileContent = new StringBuffer();
//        String genresArray[] = new String[genres.size()];
//        String genresValues = Arrays.toString(genres.toArray(genresArray));
//        fileContent.append(genresValues.substring(1, genresValues.length() - 1)).append(NEWLINE);
//        for (Movie movie : music) {
//
//            fileContent.append(movie.getTitle()).append("||");
//            fileContent.append(movie.getBaseUrl()).append("||");
//            fileContent.append(movie.getGenres()).append("||");
//            fileContent.append(movie.getNames()).append(NEWLINE);
//        }

        Logger.log("file: " + fileContent.toString());

        FileOutputStream outputStream = context.openFileOutput(Constants.MUSIC_FILE, Context.MODE_PRIVATE);
        outputStream.write(fileContent.toString().getBytes());
        outputStream.close();
    }
}
