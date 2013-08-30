package org.aitek.movies.database;

import android.provider.BaseColumns;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/21/13
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class PersonContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public PersonContract() {}

    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PersonEntry.TABLE_NAME + " (" +
                    PersonEntry._ID + " INTEGER PRIMARY KEY," +
                    PersonEntry.COLUMN_NAME + MovieDbHelper.TEXT_TYPE + MovieDbHelper.COMMA_SEP +
                    PersonEntry.COLUMN_LASTNAME + MovieDbHelper.TEXT_TYPE + MovieDbHelper.COMMA_SEP +
                    PersonEntry.COLUMN_IS_ACTOR + MovieDbHelper.TEXT_TYPE + MovieDbHelper.COMMA_SEP +
                    PersonEntry.COLUMN_IS_DIRECTOR + MovieDbHelper.TEXT_TYPE + MovieDbHelper.COMMA_SEP +
                    "UNIQUE (" + PersonEntry.COLUMN_NAME + "," + PersonEntry.COLUMN_LASTNAME + "," + PersonEntry.COLUMN_IS_ACTOR + ") ON CONFLICT REPLACE" +

                    " )";

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PersonEntry.TABLE_NAME;


    /* Inner class that defines the table contents */
    public static abstract class PersonEntry implements BaseColumns {
        public static final String TABLE_NAME = "persons";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LASTNAME = "lastname";
        public static final String COLUMN_IS_ACTOR = "isActor";
        public static final String COLUMN_IS_DIRECTOR = "isDirector";
    }
}
