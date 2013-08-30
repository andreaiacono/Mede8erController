package org.aitek.movies.database;

import android.provider.BaseColumns;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/21/13
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenreContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public GenreContract() {}

    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + GenreEntry.TABLE_NAME + " (" +
                    GenreEntry._ID + " INTEGER PRIMARY KEY," +
                    GenreEntry.COLUMN_NAME + MovieDbHelper.TEXT_TYPE + MovieDbHelper.COMMA_SEP +
                    " UNIQUE (" + GenreEntry.COLUMN_NAME + ") ON CONFLICT REPLACE" +
            " )";

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + GenreEntry.TABLE_NAME;


    /* Inner class that defines the table contents */
    public static abstract class GenreEntry implements BaseColumns {
        public static final String TABLE_NAME = "genres";
        public static final String COLUMN_NAME = "genre";
    }
}
