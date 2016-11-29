package com.mycompany.popmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.mycompany.popmovies.data.MoviesContract.MoviesEntry;
import com.mycompany.popmovies.data.MoviesContract.ReviewsEntry;
import com.mycompany.popmovies.data.MoviesContract.VideosEntry;

/**
 * Created by Borys on 2016-11-19.
 */

public class MoviesProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int VIDEOS = 200;
    static final int REVIEWS = 300;

    private static final SQLiteQueryBuilder sMoviesWithVideos;

    static{
        sMoviesWithVideos = new SQLiteQueryBuilder();
        //movies INNER JOIN videos ON movies._id = videos.movie_id
        sMoviesWithVideos.setTables(
                MoviesEntry.TABLE_NAME + " INNER JOIN " +
                        VideosEntry.TABLE_NAME +
                        " ON " + MoviesEntry.TABLE_NAME +
                        "." + MoviesEntry._ID +
                        " = " + VideosEntry.TABLE_NAME +
                        "." + VideosEntry.COLUMN_MOVIE_KEY);
    }
    private static final SQLiteQueryBuilder sMoviesWithReviews;

    static{
        sMoviesWithReviews = new SQLiteQueryBuilder();
        //movies INNER JOIN reviews ON movies._id = reviews.movie_id
        sMoviesWithVideos.setTables(
                MoviesEntry.TABLE_NAME + " INNER JOIN " +
                        ReviewsEntry.TABLE_NAME +
                        " ON " + MoviesEntry.TABLE_NAME +
                        "." + MoviesEntry._ID +
                        " = " + ReviewsEntry.TABLE_NAME +
                        "." + ReviewsEntry.COLUMN_MOVIE_KEY);
    }



    static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_REVIEWS, REVIEWS);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_VIDEOS, VIDEOS);

//        int matcher = uriMatcher.match(MoviesContract.VideosEntry.CONTENT_URI);
//        Log.v("matcher", String.valueOf(matcher));
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public String getType( Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                return MoviesEntry.CONTENT_TYPE;
            case VIDEOS:
                return VideosEntry.CONTENT_TYPE;
            case REVIEWS:
                return ReviewsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs,
                         String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case VIDEOS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        VideosEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REVIEWS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ReviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES: {
                long _id = db.insert(MoviesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesEntry.buildMoviesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case VIDEOS: {
                long _id = db.insert(VideosEntry.TABLE_NAME, null, values);
                if (_id > 0 ){
                    returnUri = VideosEntry.buildVideosUri(_id);
                } else  {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                } break;
            }

            case REVIEWS: {
                long _id = db.insert(ReviewsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ReviewsEntry.buildReviewsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete( Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        if (selection == null) selection = "1";
        int rowsDeleted;
        switch (match){
            case MOVIES: {
                rowsDeleted = db.delete(MoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case VIDEOS:{
                rowsDeleted = db.delete(VideosEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case REVIEWS:{
                rowsDeleted = db.delete(VideosEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }

        }

        if (rowsDeleted !=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        int  rowsUpdated;

        switch (match){
            case MOVIES:{
                rowsUpdated = db.update(MoviesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case VIDEOS:{
                rowsUpdated = db.update(VideosEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case REVIEWS:{
                rowsUpdated = db.update(ReviewsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (rowsUpdated !=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Log.v("provider", String.valueOf(match));

        switch (match) {
            case MOVIES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

}
