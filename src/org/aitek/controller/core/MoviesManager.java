package org.aitek.controller.core;

import android.content.Context;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.Logger;

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
    private String[] genresArray;
    private List<Movie> movies;
    private List<Movie> filteredMovies = null;
    private List<Jukebox> jukeboxes;
    private String sortField;
    private boolean sortDescending;
    private Context context;

    public MoviesManager(Context context) throws Exception {
        Logger.log("Starting MovieManager");
        this.context = context;

        genreFilter = null;
        genericFilter = null;
        filteredMovies = null;
        sortField = "title";
        sortDescending = false;

        if (movies == null) {

            Logger.log("Creating MovieManager");
            genres = new ArrayList<>();
            movies = new ArrayList<>();

//            GenericProgressIndicator genericProgressIndicator = new MovieLoader(context);
//            if (genericProgressIndicator.setup()) {
//                new ProgressIndicator().progress("Loading controller..", genericProgressIndicator);
//            }
//            //  Thread.sleep(1000);
//            //setGenres(genericProgressIndicator.getGenres());
        }

    }

    public void clear() {
        genres = new ArrayList<>();
        movies = new ArrayList<>();
        jukeboxes = new ArrayList<>();
        genreFilter = null;
        genericFilter = null;
        filteredMovies = null;
        sortField = "title";
        sortDescending = false;
    }

    public String insertGenre(String genre) {

        for (String existingGenre : genres) {
            if (existingGenre.toLowerCase().equals(genre.toLowerCase().trim())) {
                return existingGenre;
            }
        }
        genres.add(genre.trim());
        return genre;
    }

    public Movie insert(Movie movie) {

//        Logger.log("called insertMovie with " + movie.getFolder());
        movies.add(movie);

        return movie;
    }

    public String[] getGenres() {
        return genresArray;
    }

    public void setGenres(List<String> newGenres) {
        if (newGenres != null) {
            ArrayList<String> genresList = new ArrayList<>();
            genresList.add(Constants.ALL_MOVIES);
            genresList.addAll(newGenres);
            genresArray = new String[genresList.size()];
            genresList.toArray(genresArray);
        }
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

    public void sortGenres() {
        if (genres != null) {
            Collections.sort(genres);
        }
    }

    public int getCount() {
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

            if ((genericFilter == null || movie.getFolder().toLowerCase().indexOf(genericFilter) >= 0 || movie.getPersons().toLowerCase().indexOf(genericFilter) >= 0) &&
                    (genreFilter == null || movie.getGenres().indexOf(genreFilter) >= 0)) {
                filteredMovies.add(movie);
            } else {
                continue;
            }
        }

        return filteredMovies;
    }

    public void setGenreFilter(String value) {
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

    public void setGenericFilter(String value) {
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

    public void setSortField(String field) {
        if (sortField.equals(field)) {
            sortDescending = !sortDescending;
        } else {
            sortField = field;
        }

        sortMovies();
    }

    public void save() throws Exception {

        StringBuilder fileContent = new StringBuilder();
        for (Jukebox jukebox : jukeboxes) {
            fileContent.append(jukebox.toDataFormat());
        }

        // separates jukeboxes from genres/movies
        fileContent.append("\n");
        Collections.sort(genres);
        String genresArray[] = new String[genres.size()];
        String genresValues = Arrays.toString(genres.toArray(genresArray));
        fileContent.append(genresValues.substring(1, genresValues.length() - 1)).append("\n");
        for (Movie movie : movies) {
            fileContent.append(movie.toDataFormat());
        }
        Logger.log("saving " + Constants.MOVIES_FILE + ": " + fileContent.toString());

        FileOutputStream outputStream = context.openFileOutput(Constants.MOVIES_FILE, Context.MODE_PRIVATE);
        outputStream.write(fileContent.toString().getBytes());
        outputStream.close();
    }

    public void setJukeboxes(List<Jukebox> jukeboxes) {
        this.jukeboxes = jukeboxes;
    }

    public void insertGenres(String genresString) {
        String[] genres = genresString.split(" ");
        for (String genre : genres) {
            insertGenre(genre);
        }
    }
}
