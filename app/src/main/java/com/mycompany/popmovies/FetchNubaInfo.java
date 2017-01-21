package com.mycompany.popmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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



public class FetchNubaInfo extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMoreMovieInfoTask.class.getSimpleName();
    private final Context mContext;

    FetchNubaInfo(Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params){

        HttpURLConnection urlCOnnection = null;
        BufferedReader reader = null;

        String movieInfoJsonStr = null;
        String apiKey = "fa2461a57ac80bd28b2dc05dcb78f1e6"; //delete API key when sharing

        try {
            final String MOVIE_BASE_URL = "http://boryst.com/get_nuba_json.php?pass=1234";
            Log.v(LOG_TAG, MOVIE_BASE_URL);

            URL url = new URL(MOVIE_BASE_URL);
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

    private void getMovieInfoDataFromJason (String videosInfoJsonStr) throws JSONException {
        try {
            JSONObject movieDataJason = new JSONObject(videosInfoJsonStr);
            Log.v(LOG_TAG, String.valueOf(movieDataJason));

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

}

