package com.mycompany.popmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * DB Contract
 */

public class MoviesContract {


    public static final String CONTENT_AUTHORITY = "com.mycompany.popmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";
    public static final String PATH_VIDEOS = "videos";
    public static final String PATH_REVIEWS = "reviews";

    public static final class VideosEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEOS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +"/"+CONTENT_AUTHORITY+"/"+PATH_VIDEOS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_VIDEOS;

        public static final String TABLE_NAME = "videos";
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        public static final String COLUMN_MDB_ID = "mdb_video_id";
        public static final String COLUMN_TRAILER_PATH = "path";

        public static Uri buildVideosUri(){
            /**  content://com.mycompany.popmovies/videos  */
            return CONTENT_URI;
        }

        public static Uri buildVideosUriWithID(long id){
            /**  content://com.mycompany.popmovies/videos/id  */
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIDFromURI(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
         }
    }

    public static final class ReviewsEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +"/"+CONTENT_AUTHORITY+"/"+PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_REVIEWS;

        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        public static final String COLUMN_MDB_ID = "mdb_review_id";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_URL = "url";


        public static Uri buildReviewsUri(){
            /**  content://com.mycompany.popmovies/reviews  */
            return CONTENT_URI;
        }
        public static Uri buildReviewsUriWithID(long id){
            /**  content://com.mycompany.popmovies/reviews/id  */
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIDFromURI(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static final class MoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +"/"+CONTENT_AUTHORITY+"/"+PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_MOVIES;

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_MDB_ID = "mdb_movie_id";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_FAV_MOVIE = "fav_movie";
        public static final String COLUMN_RUNTIME = "runtime";

        public static Uri buildMoviesUriWithID(long id){
            /**  content://com.mycompany.popmovies/movies/id  */
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMoviesUri(){
            /**  content://com.mycompany.popmovies/movies  */
            return CONTENT_URI;
        }

/*        public static Uri buildMoviesUriWithLimit(){
            return
        }*/

        public static String getIDFromURI(Uri uri){
            return String.valueOf(uri.getPathSegments().get(1));
        }
    }
}
