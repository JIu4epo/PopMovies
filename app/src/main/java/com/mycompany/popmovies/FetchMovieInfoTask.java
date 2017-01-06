package com.mycompany.popmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.mycompany.popmovies.data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 *  Class for connecting to TMDB API, fetching JSON string, writing information into DB
 */

public class FetchMovieInfoTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMovieInfoTask.class.getSimpleName();
    private final Context mContext;

    FetchMovieInfoTask(Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params){
        if (params.length == 0){
            return null;
        }
        HttpURLConnection urlCOnnection = null;
        BufferedReader reader = null;

        String movieInfoJsonStr = null;
        String apiKey = "fa2461a57ac80bd28b2dc05dcb78f1e6"; //delete API key when sharing

        try {
            final String MOVIEINFO_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String MOVIEINFO_SORT = "sort_by";
            final String MOVIEINFO_APIKEY = "api_key";
            Uri builtUri = Uri.parse(MOVIEINFO_BASE_URL).buildUpon().
                    appendQueryParameter(MOVIEINFO_SORT, params[0]).
                    appendQueryParameter(MOVIEINFO_APIKEY, apiKey).build();
            URL url = new URL(builtUri.toString());
            urlCOnnection = (HttpURLConnection) url.openConnection();
            urlCOnnection.setRequestMethod("GET");
            urlCOnnection.connect();

            InputStream inputStream = urlCOnnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null){
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0){
                return null;
            }

            movieInfoJsonStr = buffer.toString();
            getMovieInfoDataFromJason(movieInfoJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlCOnnection !=null){
                urlCOnnection.disconnect();
            }
            if (reader !=null){
                try {
                    reader.close();
                } catch (final IOException e){
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }



    private void getMovieInfoDataFromJason (String movieInfoJsonStr) throws JSONException {
        final String TMDB_RESULTS = "results";
        final String TMDB_IMG_URL = "poster_path"; //maybe need to change to "postar_path"
        final String TMDB_ID = "id";
        final String TMDB_TITLE = "original_title";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_RAITING = "vote_average";
        final String TMDB_POPULARITY = "popularity";
        final String imageUrlBase = "http://image.tmdb.org/t/p/w342";

    try {
        JSONObject movieDataJason = new JSONObject(movieInfoJsonStr);
        JSONArray movieDataArray = movieDataJason.getJSONArray(TMDB_RESULTS);

        Vector<ContentValues> cVVector = new Vector<>(movieDataArray.length());

        for (int i =0; i<movieDataArray.length(); i++){

            JSONObject movieInfo = movieDataArray.getJSONObject(i);

            ContentValues moviesValues = new ContentValues();

            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_NAME, movieInfo.getString(TMDB_TITLE));
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH, imageUrlBase + movieInfo.getString(TMDB_IMG_URL));

/**            String urlFrom = imageUrlBase + movieInfo.getString(TMDB_IMG_URL);
            Log.v(LOG_TAG, urlFrom);

            Utility.imageDownload(mContext, urlFrom, movieInfo.getString(TMDB_IMG_URL));*/


            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, movieInfo.getString(TMDB_RELEASE_DATE));
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, movieInfo.getString(TMDB_OVERVIEW));
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_MDB_ID, movieInfo.getString(TMDB_ID));
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, movieInfo.getString(TMDB_RAITING));
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_POPULARITY, movieInfo.getString(TMDB_POPULARITY));
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_FAV_MOVIE, "false");

            cVVector.add(moviesValues);
        }

        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            //Log.v("--",String.valueOf(MoviesContract.MoviesEntry.CONTENT_URI));
            mContext.getContentResolver().bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI, cvArray);
        }

/**
 *   TODO: Uncomment to check if insert is successful
 *
 *
 */
    /***********************************/
        Cursor cur = mContext.getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                null, null, null, null);

        cVVector = new Vector<ContentValues>(cur.getCount());
        if ( cur.moveToFirst() ) {
            do {
                ContentValues cv = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cur, cv);
                cVVector.add(cv);
            } while (cur.moveToNext());
        }

        Log.v(LOG_TAG, "FetchWeatherTask Complete. " + cVVector.size() + " Inserted");

        String[] resultStrs = convertContentValuesToUXFormat(cVVector);
        Log.v("Result", resultStrs[0] +" :: "+resultStrs[1]);
        //cur.close();
    /***********************************/

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

/**    *//** TODO: Implement addVideos and addReviews same way as addLocation below
     * Helper method to handle insertion of a new location in the weather database.
     *
     * @param locationSetting The location string used to request updates from the server.
     * @param cityName A human-readable city name, e.g "Mountain View"
     * @param lat the latitude of the city
     * @param lon the longitude of the city
     * @return the row ID of the added location.
     *//**
    long addLocation(String locationSetting, String cityName, double lat, double lon) {

        long locationId;

        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(
                WeatherContract.LocationEntry.CONTENT_URI,
                new String[]{WeatherContract.LocationEntry._ID},
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{locationSetting},
                null
        );

        if (cursor.moveToFirst()){
            int locationIndex = cursor.getColumnIndex(WeatherContract.LocationEntry._ID);
            locationId = cursor.getLong(locationIndex);
        } else {
            ContentValues locationValues = new ContentValues();

            locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, lon);

            Uri insertUri = mContext.getContentResolver().insert(
                    WeatherContract.LocationEntry.CONTENT_URI,
                    locationValues
            );
            locationId = ContentUris.parseId(insertUri);
        }
        cursor.close();
        // Students: First, check if the location with this city name exists in the db
        // If it exists, return the current ID
        // Otherwise, insert it using the content resolver and the base URI
        return locationId;
    }*/

/**    @Override
    protected void onPostExecute(Movie[] result){
        if (result !=null){
            mGridViewAdapter.setResult(result);
        }
    }*/

    /** TODO: Uncoment if need to check insertion into DB  */
    /**************************/
     String[] convertContentValuesToUXFormat(Vector<ContentValues> cvv) {
     // return strings to keep UI functional for now
     String[] resultStrs = new String[cvv.size()];
     for ( int i = 0; i < cvv.size(); i++ ) {
     ContentValues weatherValues = cvv.elementAt(i);

     resultStrs[i] = weatherValues.getAsString(MoviesContract.MoviesEntry.COLUMN_NAME) +
     " - " + weatherValues.getAsString(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH);
     }
     return resultStrs;
     }
    /**************************/

    @Override
    protected void onPostExecute(Void aVoid) {
        Cursor cur = mContext.getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                new String[]{MoviesContract.MoviesEntry._ID, MoviesContract.MoviesEntry.COLUMN_MDB_ID},
                null,
                null,
                null);
//        if (cur.moveToFirst()){
//            Log.v(LOG_TAG, "Some umber,  probably 20" + String.valueOf(cur.getCount()));
//        }
        cur.moveToFirst();
        do {
            FetchVideosTask fetchVideosTask = new FetchVideosTask(mContext);
            fetchVideosTask.execute(cur.getString(1), cur.getString(0));

            FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(mContext);
            fetchReviewsTask.execute(cur.getString(1), cur.getString(0));
            //Log.v(LOG_TAG, "--"+cur.getString(0));
        } while (cur.moveToNext());




        super.onPostExecute(aVoid);
        cur.close();
    }





}

