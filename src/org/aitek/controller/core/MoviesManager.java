package org.aitek.controller.core;

import android.app.Activity;
import android.content.Context;
import org.aitek.controller.ui.GenericProgressIndicator;
import org.aitek.controller.loaders.MovieLoader;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.Logger;
import org.aitek.controller.ui.ProgressIndicator;

import java.io.FileOutputStream;
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

    private String genreFilter = null;
    private String genericFilter = null;
    private List<String> genres;
    private List<Movie> movies;
    private List<Movie> filteredMovies = null;
    private String sortField;
    private boolean sortDescending;

    public MoviesManager(Activity activity) throws Exception {

        genreFilter = null;
        genericFilter = null;
        filteredMovies = null;
        sortField = "title";
        sortDescending = false;

        if (movies == null) {

            genres = new ArrayList<>();
            movies = new ArrayList<>();

            GenericProgressIndicator genericProgressIndicator = new MovieLoader(activity);
            genericProgressIndicator.setup();
            new ProgressIndicator().progress("Loading controller..", genericProgressIndicator);
        }
    }

    public void clear() {
        genres = new ArrayList<>();
        movies = new ArrayList<>();
        genreFilter = null;
        genericFilter = null;
        filteredMovies = null;
        sortField = "title";
        sortDescending = false;
    }

    public String insertMovieGenre(String genre) {

        for (String existingGenre : genres) {
            if (existingGenre.toLowerCase().equals(genre.toLowerCase().trim())) {
                return existingGenre;
            }
        }
        genres.add(genre.trim());
        return genre;
    }

    public Movie insertMovie(Movie movie) {

        movies.add(movie);

        return movie;
    }

    public String[] getMovieGenres() {
        List<String> allGenresList = new ArrayList<>();
        allGenresList.add(Constants.ALL_MOVIES);
        allGenresList.addAll(genres);
        String genresArray[] = new String[allGenresList.size()];
        return allGenresList.toArray(genresArray);
    }

    public void setMovieGenres(List<String> newGenres) {
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
                Collections.sort(movies, Collections.reverseOrder());
            } else {
                Collections.sort(movies);
            }
        }
    }

    public void sortMovieGenres() {
        Collections.sort(genres);
    }

    public int getMoviesCount() {
        if (filteredMovies == null) {
            return movies.size();
        }
        return filteredMovies.size();
    }

    public Movie getMovie(int index) {
        if (filteredMovies == null) {
            return movies.get(index);
        }
        return filteredMovies.get(index);
    }

    public List<Movie> getFilteredMovies() {

        filteredMovies = new ArrayList<>();
        for (Movie movie : movies) {

            if ((genericFilter == null || movie.getTitle().toLowerCase().indexOf(genericFilter) >= 0 || movie.getNames().toLowerCase().indexOf(genericFilter) >= 0) &&
                    (genreFilter == null || movie.getGenres().indexOf(genreFilter) >= 0)) {
                filteredMovies.add(movie);
            } else {
                continue;
            }
        }

        return filteredMovies;
    }

    public void setMovieGenreFilter(String value) {
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

    public void setMovieGenericFilter(String value) {
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

    public void setMovieSortField(String field) {
        if (sortField.equals(field)) {
            sortDescending = !sortDescending;
        } else {
            sortField = field;
        }

        sortMovies();
    }

    public void saveMovies(Activity activity) throws Exception {

        final String NEWLINE = "\n";
        StringBuffer fileContent = new StringBuffer();
        String genresArray[] = new String[genres.size()];
        String genresValues = Arrays.toString(genres.toArray(genresArray));
        fileContent.append(genresValues.substring(1, genresValues.length() - 1)).append(NEWLINE);
        for (Movie movie : movies) {

            fileContent.append(movie.getTitle()).append("||");
            fileContent.append(movie.getPath()).append("||");
            fileContent.append(movie.getGenres()).append("||");
            fileContent.append(movie.getNames()).append(NEWLINE);
        }

        Logger.log("file: " + fileContent.toString());

        FileOutputStream outputStream = activity.openFileOutput(Constants.MOVIES_FILE, Context.MODE_PRIVATE);
        outputStream.write(fileContent.toString().getBytes());
        outputStream.close();
    }
}