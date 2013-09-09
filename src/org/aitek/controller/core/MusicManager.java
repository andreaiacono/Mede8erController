package org.aitek.controller.core;

import android.content.Context;
import org.aitek.controller.loaders.MovieLoader;
import org.aitek.controller.ui.GenericProgressIndicator;
import org.aitek.controller.ui.ProgressIndicator;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.Logger;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
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
    private List<Movie> music;
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
        sortDescending = false;

        if (music == null) {

            genres = new ArrayList<>();
            music = new ArrayList<>();

            GenericProgressIndicator genericProgressIndicator = new MovieLoader(context);
            if (genericProgressIndicator.setup()) {
                new ProgressIndicator().progress("Loading controller..", genericProgressIndicator);
            }
        }
    }

    public void clear() {
        genres = new ArrayList<>();
        music = new ArrayList<>();
        genreFilter = null;
        genericFilter = null;
        filteredMusic = null;
        sortField = "title";
        sortDescending = false;
    }

    public String[] getGenres() {
        List<String> allGenresList = new ArrayList<>();
        allGenresList.add(Constants.ALL_MUSIC);
        allGenresList.addAll(genres);
        String genresArray[] = new String[allGenresList.size()];
        return allGenresList.toArray(genresArray);
    }

    public void setGenres(List<String> newGenres) {
        genres = newGenres;
    }

//    public Movie getMovie(String title) {
//        for (Movie movie : controller) {
//            if (movie.getTitle().equals(title)) {
//                return movie;
//            }
//        }
//        return null;
//    }

    public void sortMovies() {
        if ("title".equals(sortField)) {
            if (sortDescending) {
                Collections.sort(music, Collections.reverseOrder());
            } else {
                Collections.sort(music);
            }
        }
    }

    public void sortMovieGenres() {
        Collections.sort(genres);
    }

    public int getMoviesCount() {
        if (filteredMusic == null) {
            return music.size();
        }
        return filteredMusic.size();
    }

    public Movie getMovie(int index) {
        if (filteredMusic == null) {
            return music.get(index);
        }
        return filteredMusic.get(index);
    }

    public List<Movie> getFilteredMusic() {

        filteredMusic = new ArrayList<>();
        for (Movie movie : music) {

            if ((genericFilter == null || movie.getTitle().toLowerCase().indexOf(genericFilter) >= 0 || movie.getNames().toLowerCase().indexOf(genericFilter) >= 0) &&
                    (genreFilter == null || movie.getGenres().indexOf(genreFilter) >= 0)) {
                filteredMusic.add(movie);
            } else {
                continue;
            }
        }

        return filteredMusic;
    }

    public void setGenreFilter(String value) {
        if (value.equals(Constants.ALL_MUSIC)) {
            value = null;
            if (genericFilter == null) {
                filteredMusic = null;
                return;
            }
        }
        genreFilter = value;
        filteredMusic = getFilteredMusic();
    }

    public void setGenericFilter(String value) {
        if (value.equals("")) {
            genericFilter = null;
            if (genreFilter == null) {
                filteredMusic = null;
                return;
            }
        }
        genericFilter = value.toLowerCase();
        filteredMusic = getFilteredMusic();
    }

    public void setSortField(String field) {
        if (sortField.equals(field)) {
            sortDescending = !sortDescending;
        } else {
            sortField = field;
        }

        sortMovies();
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
//            fileContent.append(movie.getBasePath()).append("||");
//            fileContent.append(movie.getGenres()).append("||");
//            fileContent.append(movie.getNames()).append(NEWLINE);
//        }

        Logger.log("file: " + fileContent.toString());

        FileOutputStream outputStream = context.openFileOutput(Constants.MUSIC_FILE, Context.MODE_PRIVATE);
        outputStream.write(fileContent.toString().getBytes());
        outputStream.close();
    }
}
