package com.mycompany.popmovies;

import android.content.ContentValues;
import android.content.Context;
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
 * Created by Borys on 2016-11-19.
 */

public class FetchMovieInfoTask extends AsyncTask<String, Void, Movie[]> {
    private final String LOG_TAG = FetchMovieInfoTask.class.getSimpleName();


    private GridViewAdapter mGridViewAdapter;
    private final Context mContext;

    FetchMovieInfoTask(Context context, GridViewAdapter gridViewAdapter){
        mContext = context;
        mGridViewAdapter = gridViewAdapter;
    }

    @Override
    protected Movie[] doInBackground(String... params){
        if (params.length == 0){
            return null;
        }
        HttpURLConnection urlCOnnection = null;
        BufferedReader reader = null;

        String movieInfoJsonStr = null;
        //Log.v(LOG_TAG, "+++"+params[0]);
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

        } catch (IOException e){
            Log.e(LOG_TAG, "Error ", e);
            return null;
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

        try {
            return getMovieInfoDataFromJason(movieInfoJsonStr);
        } catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(),e);
            e.printStackTrace();
        }
        return null;


    }

    String[] convertContentValuesToUXFormat(Vector<ContentValues> cvv) {
        // return strings to keep UI functional for now
        String[] resultStrs = new String[cvv.size()];
        for ( int i = 0; i < cvv.size(); i++ ) {
            ContentValues weatherValues = cvv.elementAt(i);

            resultStrs[i] = weatherValues.getAsString(MoviesContract.MoviesEntry.COLUMN_NAME) +
                    " - " + weatherValues.getAsString(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE);
        }
        return resultStrs;
    }

    private Movie[] getMovieInfoDataFromJason (String movieInfoJsonStr) throws JSONException {
        final String TMDB_RESULTS = "results";
        final String TMDB_IMG_URL = "poster_path"; //maybe need to change to "postar_path"
        final String TMDB_ID = "id";
        final String TMDB_TITLE = "original_title";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_RAITING = "vote_average";
        final String imageUrlBase = "http://image.tmdb.org/t/p/w185";

    try {
        JSONObject movieDataJason = new JSONObject(movieInfoJsonStr);
        JSONArray movieDataArray = movieDataJason.getJSONArray(TMDB_RESULTS);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieDataArray.length());


        Movie[] resultArray = new Movie[movieDataArray.length()];

        for (int i =0; i<movieDataArray.length(); i++){

            JSONObject movieInfo = movieDataArray.getJSONObject(i);
            resultArray[i] = new Movie();

            resultArray[i].setId(movieInfo.getString(TMDB_ID)); //TODO: change ID to int
            resultArray[i].setTitle(movieInfo.getString(TMDB_TITLE));
            resultArray[i].setPosterUri(imageUrlBase + movieInfo.getString(TMDB_IMG_URL));
            resultArray[i].setOverview(movieInfo.getString(TMDB_OVERVIEW));
            resultArray[i].setRating(movieInfo.getString(TMDB_RAITING));
            resultArray[i].setDate(movieInfo.getString(TMDB_RELEASE_DATE));

            ContentValues moviesValues = new ContentValues();

            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_NAME, resultArray[i].getTitle());
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH, resultArray[i].getPosterUri());
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, resultArray[i].getDate());
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, resultArray[i].getOverview());
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_MDB_ID, resultArray[i].getId());
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, resultArray[i].getRating());
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
 *   Uncomment to check if insert is successful
 *
 *
 * Cursor cur = mContext.getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
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
        Log.v("Result", resultStrs[0] +" :: "+resultStrs[1]);*/



        return resultArray;

    } catch (JSONException e) {
        Log.e(LOG_TAG, e.getMessage(), e);
        e.printStackTrace();
    }
        //return resultArray;
        return null;
    }



    @Override
    protected void onPostExecute(Movie[] result){
        if (result !=null){
            mGridViewAdapter.setResult(result);
        }
    }
}

