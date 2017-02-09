package com.mycompany.popmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
 * Created by Borys on 2016-12-22.
 */

public class FetchReviewsTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();
    private final Context mContext;
    ReviewsAdapter mAdapter;

    FetchReviewsTask(Context context, ReviewsAdapter adapter){
        mContext = context;
        mAdapter = adapter;
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

        String reviewsInfoJsonStr = null;
        String apiKey = "fa2461a57ac80bd28b2dc05dcb78f1e6"; //delete API key when sharing

        try {
            final String REVIEWS_BASE_URL = "http://api.themoviedb.org/3/movie/"+params[0]+"/reviews";
            //Log.v(LOG_TAG, REVIEWS_BASE_URL);
            final String REVIEWS_APIKEY = "api_key";
            Uri builtUri = Uri.parse(REVIEWS_BASE_URL).buildUpon().
                    appendQueryParameter(REVIEWS_APIKEY, apiKey).build();
            URL url = new URL(builtUri.toString());
            urlCOnnection = (HttpURLConnection) url.openConnection();
            urlCOnnection.setRequestMethod("GET");
            urlCOnnection.connect();
            Log.v(LOG_TAG, "URI - "+String .valueOf(url));

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

            reviewsInfoJsonStr = buffer.toString();
            getReviewsInfoDataFromJason(reviewsInfoJsonStr);

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

    private void getReviewsInfoDataFromJason (String reviewsInfoJsonStr) throws JSONException {
        final String TMDB_RESULTS = "results";
        final String TMDB_ID = "id";
        final String TMDB_AUTHOR = "author";
        final String TMDB_CONTENT = "content";
        final String TMDB_URL = "url";


        try {
            JSONObject reviewsDataJason = new JSONObject(reviewsInfoJsonStr);
            JSONArray reviewsDataArray = reviewsDataJason.getJSONArray(TMDB_RESULTS);

            Vector<ContentValues> cVVector = new Vector<>(reviewsDataArray.length());

            for (int i =0; i<reviewsDataArray.length(); i++){

                JSONObject reviewInfo = reviewsDataArray.getJSONObject(i);

                ContentValues reviewValues = new ContentValues();

                reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_MDB_ID, reviewInfo.getString(TMDB_ID));
                reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_AUTHOR, reviewInfo.getString(TMDB_AUTHOR));
                reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_CONTENT, reviewInfo.getString(TMDB_CONTENT));
                reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_URL, reviewInfo.getString(TMDB_URL));
                reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_MOVIE_KEY, dBmovieID );

                cVVector.add(reviewValues);
            }

            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                mContext.getContentResolver().bulkInsert(MoviesContract.ReviewsEntry.CONTENT_URI, cvArray);
            }

/**
 *   TODO: Uncomment to check if insert is successful
 *
 *
 */
            /***********************************/
/*            Cursor cur = mContext.getContentResolver().query(MoviesContract.ReviewsEntry.CONTENT_URI,
                    null, null, null, null);
            cVVector = new Vector<ContentValues>(cur.getCount());
            if ( cur.moveToFirst() ) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cur, cv);
                    cVVector.add(cv);
                } while (cur.moveToNext());
            }
            Log.v(LOG_TAG, "FetchReviewsTask Complete. " + cVVector.size() + " Inserted");
            String[] resultStrs = convertContentValuesToUXFormat(cVVector);
            if (resultStrs.length > 1 ) {
                Log.v("Result", resultStrs[0] + " :: " + resultStrs[1]);
            } else {
                Log.v("Result", "The only entry ::"+ resultStrs[0]);
            }*/

            /***********************************/

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /** TODO: Uncoment if need to check insertion into DB  */
    @Override
    protected void onPostExecute(Void aVoid) {
        Cursor cursor = mContext.getContentResolver().query(
                MoviesContract.ReviewsEntry.buildReviewsUriWithID(Long.parseLong(dBmovieID)),
                null,
                null,
                null,
                null);
        mAdapter.swapCursor(cursor);
        super.onPostExecute(aVoid);
    }

    /**************************/
    String[] convertContentValuesToUXFormat(Vector<ContentValues> cvv) {
        // return strings to keep UI functional for now
        String[] resultStrs = new String[cvv.size()];
        for ( int i = 0; i < cvv.size(); i++ ) {
            ContentValues reviewsValues = cvv.elementAt(i);

            resultStrs[i] = reviewsValues.getAsString(MoviesContract.VideosEntry.COLUMN_MOVIE_KEY) +
                    " - " + reviewsValues.getAsString(MoviesContract.VideosEntry.COLUMN_TRAILER_PATH);
        }
        return resultStrs;
    }
    /**************************/
}