package com.mycompany.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mycompany.popmovies.data.MoviesContract.MoviesEntry;
import com.mycompany.popmovies.data.MoviesContract.ReviewsEntry;
import com.mycompany.popmovies.data.MoviesContract.VideosEntry;
/**
 * Created by Borys on 2016-11-16.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;


    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesEntry.TABLE_NAME + " (" +
                MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_MDB_ID + " INTEGER NOT NULL, " +
                MoviesEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MoviesEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MoviesEntry.COLUMN_FAV_MOVIE + " BOOLEAN NOT NULL);";

        final String SQL_CREATE_VIDEOS_TABLE = "CREATE TABLE " + VideosEntry.TABLE_NAME + " (" +
                VideosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VideosEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                VideosEntry.COLUMN_MDB_ID + " TEXT NOT NULL, " +
                VideosEntry.COLUMN_TRAILER_PATH + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + VideosEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MoviesEntry.TABLE_NAME + " (" + MoviesEntry._ID + "));";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + ReviewsEntry.TABLE_NAME + " (" +
                ReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ReviewsEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                ReviewsEntry.COLUMN_MDB_ID + " TEXT NOT NULL, " +
                ReviewsEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                ReviewsEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                ReviewsEntry.COLUMN_URL + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + ReviewsEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MoviesEntry.TABLE_NAME + " (" + MoviesEntry._ID + "));";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VIDEOS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
