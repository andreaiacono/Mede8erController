package org.aitek.controller.core;

import android.content.Context;

import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.Logger;
import org.aitek.controller.utils.MiscUtils;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
    private List<Movie> displayedMovies = null;
    private List<Jukebox> jukeboxes;
    private Context context;
    private Comparator<Movie> comparator = MovieComparator.BY_TITLE_ASC;


    private enum MovieComparator implements Comparator<Movie> {
        BY_TITLE_ASC {
            public int compare(Movie m1, Movie m2) {
                return m1.getSortingTitle().compareTo(m2.getSortingTitle());
            }
        },
        BY_DATE_ASC {
            public int compare(Movie m1, Movie m2) {
                if (m1.getDate() == null) {
                    return -1;
                }
                if (m2.getDate() == null) {
                    return 1;
                }
                return m1.getDate().compareTo(m2.getDate());
            }
        },
        BY_TITLE_DESC {
            public int compare(Movie m1, Movie m2) {
                return -BY_TITLE_ASC.compare(m1, m2);
            }
        },
        BY_DATE_DESC {
            public int compare(Movie m1, Movie m2) {
                return -BY_DATE_ASC.compare(m1, m2);
            }
        }
    }

    public MoviesManager(Context context) throws Exception {
        this.context = context;

        genreFilter = null;
        genericFilter = null;
        displayedMovies = movies;
        genres = new ArrayList<String>();
        movies = new ArrayList<Movie>();
    }

    public void clear() {
        genres = new ArrayList<String>();
        movies = new ArrayList<Movie>();
        displayedMovies = movies;
        jukeboxes = new ArrayList<Jukebox>();
        genreFilter = null;
        genericFilter = null;
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
        movies.add(movie);
        return movie;
    }

    public String[] getGenres() {
        if (genresArray == null) {

            List<String> genresList = new ArrayList<String>();
            genresList.add(Constants.ALL_MOVIES);
            genresList.addAll(genres);
            genresList = MiscUtils.removeEmptyStrings(genresList);
            genresArray = new String[genresList.size()];
            genresList.toArray(genresArray);
        }
        return genresArray;
    }

    public void setGenres(List<String> newGenres) {
        if (newGenres != null) {
            List<String> genresList = new ArrayList<String>();
            genresList.add(Constants.ALL_MOVIES);
            genresList.addAll(newGenres);
            genresList = MiscUtils.removeEmptyStrings(genresList);
            genresArray = new String[genresList.size()];
            genresList.toArray(genresArray);
        }
    }

    public void sortMovies() {
        Collections.sort(movies, comparator);
//        Collections.sort(displayedMovies, comparator);
    }

    public void sortGenres() {
        if (genres != null) {
            Collections.sort(genres);
        }
    }

    public int getCount() {
        if (displayedMovies == null) {
            return movies.size();
        }
        return displayedMovies.size();
    }

    public Movie getMovie(int index) {
        if (displayedMovies == null) {
            return movies.get(index);
        }
        return displayedMovies.get(index);
    }

    public List<Movie> getDisplayedMovies() {

        displayedMovies = new ArrayList<Movie>();
        for (Movie movie : movies) {

            if ((genericFilter == null || movie.getFolder().toLowerCase().indexOf(genericFilter) >= 0 || movie.getPersons().toLowerCase().indexOf(genericFilter) >= 0) &&
                    (genreFilter == null || movie.getGenres().indexOf(genreFilter) >= 0)) {
                displayedMovies.add(movie);
            } else {
                continue;
            }
        }

        return displayedMovies;
    }

    public void setGenreFilter(String value) {
        if (value.equals(Constants.ALL_MOVIES)) {
            value = null;
            if (genericFilter == null) {
                displayedMovies = movies;
                return;
            }
        }
        genreFilter = value;
        displayedMovies = getDisplayedMovies();
    }

    public void setGenericFilter(String value) {
        if (value.equals("")) {
            genericFilter = null;
            if (genreFilter == null) {
                displayedMovies = movies;
                return;
            }
        }
        genericFilter = value.toLowerCase();
        displayedMovies = getDisplayedMovies();
    }

    public void setTitleComparator() {
        if (comparator == MovieComparator.BY_TITLE_ASC) {
            comparator = MovieComparator.BY_TITLE_DESC;
        } else {
            comparator = MovieComparator.BY_TITLE_ASC;
        }
        sortMovies();
    }

    public void setDateComparator() {
        if (comparator == MovieComparator.BY_DATE_DESC) {
            comparator = MovieComparator.BY_DATE_ASC;
        } else {
            comparator = MovieComparator.BY_DATE_DESC;
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
        Logger.log("saving " + Constants.MOVIES_FILE); // + ": " + fileContent.toString());

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
