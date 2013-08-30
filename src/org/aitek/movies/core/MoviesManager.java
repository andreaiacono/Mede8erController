package org.aitek.movies.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import org.aitek.movies.core.Movie;
import org.aitek.movies.loaders.FileSystemScanner;
import org.aitek.movies.loaders.MovieLoader;
import org.aitek.movies.loaders.Progressable;
import org.aitek.movies.utils.Constants;
import org.aitek.movies.utils.ProgressIndicator;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/17/13
 * Time: 11:17 PM
 */
public class MoviesManager {

    static private String genreFilter = null;
    static private String genericFilter = null;
    static private List<String> genres;
    static private List<Movie> movies;
    static private List<Movie> filteredMovies = null;
    static private String sortField;
    static private boolean sortDescending;

    public static void init(Activity activity) throws Exception {

        genreFilter = null;
        genericFilter = null;
        filteredMovies = null;
        sortField = "title";
        sortDescending = false;

        if (movies == null) {

            genres = new ArrayList<String>();
            movies = new ArrayList<Movie>();

            Progressable progressable = new MovieLoader();
            progressable.setup(activity);
            new ProgressIndicator().progress("Loading movies..", progressable);
        }
    }

    public static void clear() {
        genres = new ArrayList<String>();
        movies = new ArrayList<Movie>();
        genreFilter = null;
        genericFilter = null;
        filteredMovies = null;
        sortField = "title";
        sortDescending = false;
    }

    public static String insertGenre(String genre) {

        for (String existingGenre : genres) {
            if (existingGenre.toLowerCase().equals(genre.toLowerCase().trim())) {
                return existingGenre;
            }
        }
        genres.add(genre.trim());
        return genre;
    }

    public static Movie insertMovie(Movie movie) {

        movies.add(movie);

        return movie;
    }

    public static String[] getGenres() {
        List<String> allGenresList = new ArrayList<String>();
        allGenresList.add(Constants.ALL_MOVIES);
        allGenresList.addAll(genres);
        String genresArray[] = new String[allGenresList.size()];
        return allGenresList.toArray(genresArray);
    }

    public static Movie getMovie(String title) {
        for (Movie movie : movies) {
            if (movie.getTitle().equals(title)) {
                return movie;
            }
        }
        return null;
    }

    public static void sortMovies() {
        if ("title".equals(sortField)) {
            if (sortDescending) {
                Collections.sort(movies, Collections.reverseOrder());
            }
            else {
                Collections.sort(movies);
            }
        }
    }

    public static void sortGenres() {
        Collections.sort(genres);
    }

    public static int getMoviesCount() {
        if (filteredMovies == null) {
            return movies.size();
        }
        return filteredMovies.size();
    }

    public static Movie getMovie(int index) {
        if (filteredMovies == null) {
            return movies.get(index);
        }
        return filteredMovies.get(index);
    }

    public static List<Movie> getFilteredMovies() {

        filteredMovies = new ArrayList<Movie>();
        for (Movie movie : movies) {

            if ((genericFilter == null || movie.getTitle().toLowerCase().indexOf(genericFilter) >= 0 || movie.getNames().toLowerCase().indexOf(genericFilter) >= 0) &&
                (genreFilter == null || movie.getGenres().indexOf(genreFilter) >=0)) {
                filteredMovies.add(movie);
            } else {
                continue;
            }
        }

        return filteredMovies;
    }



    public static void setGenreFilter(String value) {
        if (value.equals(Constants.ALL_MOVIES)) {
            value = null;
            if (genericFilter == null) {
                filteredMovies = null;
                return;
            }
        }
        genreFilter = value;
        filteredMovies = getFilteredMovies();
    }

    public static void setGenericFilter(String value) {
        if (value.equals("")) {
            genericFilter = null;
            if (genreFilter == null) {
                filteredMovies = null;
                return;
            }
        }
        genericFilter = value.toLowerCase();
        filteredMovies = getFilteredMovies();
    }

    public static void setSortField(String field) {
        if (sortField.equals(field)) {
            sortDescending = !sortDescending;
        } else {
            sortField = field;
        }

        sortMovies();
    }

    public static void saveMovies(Activity activity) throws Exception {

        final String NEWLINE = "\n";
        StringBuffer fileContent = new StringBuffer();
        String genresArray[] = new String[genres.size()];
        String genresValues = Arrays.toString(genres.toArray(genresArray));
        fileContent.append(genresValues.substring(1, genresValues.length() - 1)).append(NEWLINE);
        for (Movie movie : movies) {

            fileContent.append(movie.getTitle()).append("||");
            fileContent.append(movie.getAbsolutePath()).append("||");
            fileContent.append(movie.getGenres()).append("||");
            fileContent.append(movie.getNames()).append(NEWLINE);
        }

        //Log.w("mede8er", "file: " + fileContent.toString());

        FileOutputStream outputStream = activity.openFileOutput(Constants.MOVIES_FILE, Context.MODE_PRIVATE);
        outputStream.write(fileContent.toString().getBytes());
        outputStream.close();
    }

    public static void setGenres(List<String> newGenres) {
        genres = newGenres;
    }
}
