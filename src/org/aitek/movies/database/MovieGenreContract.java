package org.aitek.movies.database;

import android.provider.BaseColumns;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/21/13
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class MovieGenreContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public MovieGenreContract() {}

    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MovieGenreEntry.TABLE_NAME + " (" +
                    MovieGenreEntry.COLUMN_ID_MOVIE + MovieDbHelper.TEXT_TYPE + MovieDbHelper.COMMA_SEP +
                    MovieGenreEntry.COLUMN_ID_GENRE + MovieDbHelper.TEXT_TYPE + MovieDbHelper.COMMA_SEP +
                    " UNIQUE (" + MovieGenreEntry.COLUMN_ID_MOVIE + "," + MovieGenreEntry.COLUMN_ID_GENRE + ") ON CONFLICT REPLACE" +
                    " )";

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieGenreEntry.TABLE_NAME;

    /* Inner class that defines the table contents */
    public static abstract class MovieGenreEntry implements BaseColumns {
        public static final String TABLE_NAME = "actors";
        public static final String COLUMN_ID_MOVIE = "id_movie";
        public static final String COLUMN_ID_GENRE = "id_genre";
    }
}

