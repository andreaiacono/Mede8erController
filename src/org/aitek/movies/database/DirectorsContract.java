package org.aitek.movies.database;

import android.provider.BaseColumns;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/21/13
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectorsContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DirectorsContract() {}
    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DirectorMovieEntry.TABLE_NAME + " (" +
                    DirectorMovieEntry.COLUMN_ID_MOVIE + MovieDbHelper.TEXT_TYPE + MovieDbHelper.COMMA_SEP +
                    DirectorMovieEntry.COLUMN_ID_PERSON + MovieDbHelper.TEXT_TYPE + MovieDbHelper.COMMA_SEP +
                    " UNIQUE (" + DirectorMovieEntry.COLUMN_ID_MOVIE + "," + DirectorMovieEntry.COLUMN_ID_PERSON + ") ON CONFLICT REPLACE" +
                    " )";

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DirectorMovieEntry.TABLE_NAME;



    /* Inner class that defines the table contents */
    public static abstract class DirectorMovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "directors";
        public static final String COLUMN_ID_MOVIE = "id_movie";
        public static final String COLUMN_ID_PERSON = "id_person";
    }
}

