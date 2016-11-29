package com.mycompany.popmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Borys on 2016-11-16.
 */

public class MoviesContract {


    public static final String CONTENT_AUTHORITY = "com.mycompany.popmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";
    public static final String PATH_VIDEOS = "videos";
    public static final String PATH_REVIEWS = "reviews";


//    public static long normalizeDate(long startDate) {
//        Time time = new Time();
//        time.set(startDate);
//        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
//        return time.setJulianDay(julianDay);
//    }


    public static final class VideosEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEOS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +"/"+CONTENT_AUTHORITY+"/"+PATH_VIDEOS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_VIDEOS;

        public static final String TABLE_NAME = "videos";
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        public static final String COLUMN_MDB_ID = "mdb_video_id";
        public static final String COLUMN_TRAILER_PATH = "path";

        public static Uri buildVideosUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
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

        public static Uri buildReviewsUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
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
        public static final String COLUMN_FAV_MOVIE = "fav_movie";

        public static Uri buildMoviesUri(long id){
            /**
             * content://com.mycompany.popmovies/movies
             * */
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMoviesVideos(String videos){
            /**
             * content://com.mycompany.popmovies/movies/videos
             * */
            //Log.v("test Uri", String.valueOf(CONTENT_URI.buildUpon().appendPath(locationSetting).build()));
            return CONTENT_URI.buildUpon().appendPath(videos).build();
        }

        public static Uri buildMoviesReviews(String reviews){
            /**
             * content://com.mycompany.popmovies/movies/reviews
             * */
            return CONTENT_URI.buildUpon().appendPath(reviews).build();
        }




//        public static String getLocationSettingFromUri(Uri uri){
//            return uri.getPathSegments().get(1);
//        }
//
//        public static long getDateFromUri(Uri uri){
//            return Long.parseLong(uri.getPathSegments().get(2));
//        }
//
//        public static long getStartDateFromUri(Uri uri){
//            String dateString = uri.getQueryParameter(COLUMN_DATE);
//            if (null != dateString && dateString.length() > 0){
//                return Long.parseLong(dateString);
//            } else {
//                return 0;
//            }
//        }


    }
}
