package org.aitek.movies.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/21/13
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Mede8erPlayer.db";
    static final String TEXT_TYPE = " TEXT";
    static final String COMMA_SEP = ",";
    private SQLiteDatabase readableDb;
    private SQLiteDatabase writableDb;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        readableDb = getReadableDatabase();
        writableDb = getWritableDatabase();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(GenreContract.SQL_CREATE_ENTRIES);
        db.execSQL(PersonContract.SQL_CREATE_ENTRIES);
        db.execSQL(MovieContract.SQL_CREATE_ENTRIES);
        db.execSQL(MovieGenreContract.SQL_CREATE_ENTRIES);
        db.execSQL(ActorsContract.SQL_CREATE_ENTRIES);
        db.execSQL(DirectorsContract.SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(GenreContract.SQL_DELETE_ENTRIES);
        db.execSQL(PersonContract.SQL_DELETE_ENTRIES);
        db.execSQL(MovieContract.SQL_DELETE_ENTRIES);
        db.execSQL(MovieGenreContract.SQL_DELETE_ENTRIES);
        db.execSQL(ActorsContract.SQL_DELETE_ENTRIES);
        db.execSQL(DirectorsContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void insertGenre(String genre) {

        ContentValues values = new ContentValues();
        values.put(GenreContract.GenreEntry.COLUMN_NAME, genre);
        long newRowId = writableDb.insert(GenreContract.GenreEntry.TABLE_NAME, null, values);
    }

    public String[] getGenres() {

        String[] projection = {
            GenreContract.GenreEntry.COLUMN_NAME,
        };

        String sortOrder = GenreContract.GenreEntry.COLUMN_NAME + " ASC";

        Cursor cursor = readableDb.query(
            GenreContract.GenreEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            sortOrder
        );

        String[] genres = new String[cursor.getCount()];
        int counter = 0;
        cursor.moveToFirst();
        do {
            String genre = cursor.getString(0);
            genres[counter++] = genre;
            cursor.moveToNext();
        } while (!cursor.isLast());

        return genres;
    }

//    public void loadMovies() {
//
//        String[] projection = {
//                GenreContract.GenreEntry.COLUMN_NAME,
//        };
//
//        String sortOrder = GenreContract.GenreEntry.COLUMN_NAME + " ASC";
//
//        Movie movie = new Movie(null, title, null, null, actors, directors, genres);
//        MoviesManager.insertMovie(movie);
//
//
//        String selection = "";
//
//        if (genreFilter != null) {
//            selection = GenreContract.GenreEntry.COLUMN_NAME + "='" + genreFilter + "' AND ";
//        }
//        if (genericFilter != null) {
//            if (selection != null) {
//                selection += " AND ";
//            }
//
//            titleWhere = GenreContract.GenreEntry.COLUMN_NAME + "='" + genreFilter + "' AND ";
//        }
//
//        selection = genreWhere;
//
//
//
//        Cursor cursor = readableDb.query(
//                GenreContract.GenreEntry.TABLE_NAME,
//                projection,
//                selection,
//                null,
//                null,
//                null,
//                sortOrder
//        );
//
//        String[] genres = new String[cursor.getCount()];
//        int counter = 0;
//        cursor.moveToFirst();
//        do {
//            String genre = cursor.getString(0);
//            genres[counter++] = genre;
//            cursor.moveToNext();
//        } while (!cursor.isLast());
//
//        return genres;
//    }

}