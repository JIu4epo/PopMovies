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
 * Created by Borys on 2016-12-02.
 */

public class FetchVideosTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchVideosTask.class.getSimpleName();
    private final Context mContext;

    FetchVideosTask(Context context){
        mContext = context;
    }
    String dBmovieID;
    VideosAdapter adapter;


    @Override
    protected Void doInBackground(String... params){
        if (params.length == 0){
            return null;
        }
        dBmovieID = params[1];

        HttpURLConnection urlCOnnection = null;
        BufferedReader reader = null;

        String videosInfoJsonStr = null;
        String apiKey = "fa2461a57ac80bd28b2dc05dcb78f1e6"; //delete API key when sharing

        try {
            final String VIDEOS_BASE_URL = "http://api.themoviedb.org/3/movie/"+params[0]+"/videos";
            //Log.v(LOG_TAG, VIDEOS_BASE_URL);
            final String VIDEOS_APIKEY = "api_key";
            Uri builtUri = Uri.parse(VIDEOS_BASE_URL).buildUpon().
                    appendQueryParameter(VIDEOS_APIKEY, apiKey).build();
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

            videosInfoJsonStr = buffer.toString();
            getVideosInfoDataFromJason(videosInfoJsonStr);

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

    private void getVideosInfoDataFromJason (String videosInfoJsonStr) throws JSONException {
        final String TMDB_RESULTS = "results";
        final String TMDB_KEY = "key";
        final String TMDB_ID = "id";
        final String TMDB_NAME = "name";
        final String TMDB_SITE = "site";
        final String TMDB_TYPE = "type";

        final String videoUrlBase = "https://www.youtube.com/watch?v=";

        try {
            JSONObject videosDataJason = new JSONObject(videosInfoJsonStr);
            JSONArray videosDataArray = videosDataJason.getJSONArray(TMDB_RESULTS);

            Vector<ContentValues> cVVector = new Vector<>(videosDataArray.length());

            for (int i =0; i<videosDataArray.length(); i++){

                JSONObject videoInfo = videosDataArray.getJSONObject(i);

                ContentValues videoValues = new ContentValues();

                videoValues.put(MoviesContract.VideosEntry.COLUMN_TRAILER_PATH, videoUrlBase + videoInfo.getString(TMDB_KEY));
                videoValues.put(MoviesContract.VideosEntry.COLUMN_MDB_ID, videoInfo.getString(TMDB_ID));
                videoValues.put(MoviesContract.VideosEntry.COLUMN_MOVIE_KEY, dBmovieID );

                //moviesValues.put(MoviesContract.MoviesEntry.COLUMN_FAV_MOVIE, "false");

                cVVector.add(videoValues);
            }

            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                //Log.v("--",String.valueOf(MoviesContract.MoviesEntry.CONTENT_URI));
                mContext.getContentResolver().bulkInsert(MoviesContract.VideosEntry.CONTENT_URI, cvArray);
            }

/**
 *   TODO: Uncomment to check if insert is successful
 *
 *
 */
            /***********************************/
/*            Cursor cur = mContext.getContentResolver().query(MoviesContract.VideosEntry.CONTENT_URI,
                    null, null, null, null);

            cVVector = new Vector<ContentValues>(cur.getCount());
            if ( cur.moveToFirst() ) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cur, cv);
                    cVVector.add(cv);
                } while (cur.moveToNext());
            }

            //Log.v(LOG_TAG, "FetchVideosTask Complete. " + cVVector.size() + " Inserted");

            String[] resultStrs = convertContentValuesToUXFormat(cVVector);
            //Log.v("Result", resultStrs[0] +" :: "+resultStrs[1]);*/

            /***********************************/

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /** TODO: Uncoment if need to check insertion into DB  */
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
/**        final Cursor cursor = mContext.getContentResolver().query(
                //MoviesContract.VideosEntry.CONTENT_URI,
                MoviesContract.VideosEntry.buildVideosUriWithID(Long.parseLong(dBmovieID)),
                null,
                //new String[]{MoviesContract.VideosEntry.COLUMN_TRAILER_PATH},
                null,
                //new String[]{dBmovieID},
                null,
                null);
        String[] items = new String[cursor.getCount()];
        int i = 0;
        do {
            Log.v(LOG_TAG, cursor.getString(0) + " - " + cursor.getString(1) + " - " + cursor.getString(2) + " - " + cursor.getString(3));
            items[i] = cursor.getString(3);
            i++;
        } while (cursor.moveToNext());
        adapter.setResult(items);*/

    }

    /**************************/
    String[] convertContentValuesToUXFormat(Vector<ContentValues> cvv) {
        // return strings to keep UI functional for now
        String[] resultStrs = new String[cvv.size()];
        for ( int i = 0; i < cvv.size(); i++ ) {
            ContentValues weatherValues = cvv.elementAt(i);

            resultStrs[i] = weatherValues.getAsString(MoviesContract.VideosEntry.COLUMN_MOVIE_KEY) +
                    " - " + weatherValues.getAsString(MoviesContract.VideosEntry.COLUMN_TRAILER_PATH);
        }
        return resultStrs;
    }
    /**************************/
}

