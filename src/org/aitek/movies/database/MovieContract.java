package org.aitek.movies.database;

import android.provider.BaseColumns;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/21/13
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class MovieContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public MovieContract() {}

    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                    MovieEntry._ID + " INTEGER PRIMARY KEY," +
                    MovieEntry.COLUMN_TITLE + MovieDbHelper.TEXT_TYPE + MovieDbHelper.COMMA_SEP +
                    MovieEntry.COLUMN_RATING + MovieDbHelper.TEXT_TYPE + MovieDbHelper.COMMA_SEP +
                    MovieEntry.COLUMN_DATE + MovieDbHelper.TEXT_TYPE + MovieDbHelper.COMMA_SEP +
                    " UNIQUE (" + MovieEntry.COLUMN_TITLE + ") ON CONFLICT REPLACE" +
                    " )";

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME;


    /* Inner class that defines the table contents */
    public static abstract class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_DATE = "date";
    }
}
