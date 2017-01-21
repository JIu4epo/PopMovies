package com.mycompany.popmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.mycompany.popmovies.data.MoviesContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Borys on 2017-01-10.
 */


/**
 * Created by Borys on 2016-12-02.
 */

public class FetchMoreMovieInfoTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMoreMovieInfoTask.class.getSimpleName();
    private final Context mContext;

    FetchMoreMovieInfoTask(Context context){
        mContext = context;
    }
    String dBmovieID;

    @Override
    protected Void doInBackground(String... params){
        if (params.length == 0){
            return null;
        }
        dBmovieID = params[1];

        HttpURLConnection urlCOnnection = null;
        BufferedReader reader = null;

        String movieInfoJsonStr = null;
        String apiKey = "fa2461a57ac80bd28b2dc05dcb78f1e6"; //delete API key when sharing

        try {
            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/"+params[0];
            //Log.v(LOG_TAG, MOVIE_BASE_URL);
            final String MOVIE_APIKEY = "api_key";
            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon().
                    appendQueryParameter(MOVIE_APIKEY, apiKey).build();
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


            /** */

            /***/

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

    private void getMovieInfoDataFromJason (String videosInfoJsonStr) throws JSONException {
        final String TMDB_RUNTIME = "runtime";
        try {
            JSONObject movieDataJason = new JSONObject(videosInfoJsonStr);
            //Log.v(LOG_TAG, String.valueOf(movieDataJason));
            String runtime = movieDataJason.getString(TMDB_RUNTIME);
            //Log.v(LOG_TAG, String.valueOf(runtime));

            final ContentValues movieValue = new ContentValues();
            movieValue.put(MoviesContract.MoviesEntry.COLUMN_RUNTIME, runtime);

            if (runtime !=null){
                mContext.getContentResolver().update(
                        MoviesContract.MoviesEntry.buildMoviesUriWithID(Long.parseLong(dBmovieID)),
                        movieValue,
                        null,
                        null
                );
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

}

