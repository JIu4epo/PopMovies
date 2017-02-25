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


public class FetchReviewsTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();
    private final Context mContext;
    private ReviewsAdapter mAdapter;

    FetchReviewsTask(Context context, ReviewsAdapter adapter){
        mContext = context;
        mAdapter = adapter;
    }
    private String dBmovieID;


    @Override
    protected Void doInBackground(String... params){
        if (params.length == 0){
            return null;
        }
        dBmovieID = params[1];

        HttpURLConnection urlCOnnection = null;
        BufferedReader reader = null;

        String reviewsInfoJsonStr;
        String apiKey = Utility.getApiKey();

        try {
            final String REVIEWS_BASE_URL = "http://api.themoviedb.org/3/movie/"+params[0]+"/reviews";
            final String REVIEWS_APIKEY = "api_key";
            Uri builtUri = Uri.parse(REVIEWS_BASE_URL).buildUpon().
                    appendQueryParameter(REVIEWS_APIKEY, apiKey).build();
            URL url = new URL(builtUri.toString());
            urlCOnnection = (HttpURLConnection) url.openConnection();
            urlCOnnection.setRequestMethod("GET");
            urlCOnnection.connect();

            InputStream inputStream = urlCOnnection.getInputStream();
            //StringBuffer buffer = new StringBuffer();
            StringBuilder buffer = new StringBuilder();
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

            Cursor cursor = mContext.getContentResolver().query(
                    MoviesContract.ReviewsEntry.buildReviewsUri(),
                    new String[]{MoviesContract.ReviewsEntry.COLUMN_MDB_ID, MoviesContract.ReviewsEntry._ID},
                    null,
                    null,
                    null);

            for (int i =0; i<reviewsDataArray.length(); i++){

                JSONObject reviewInfo = reviewsDataArray.getJSONObject(i);

                ContentValues reviewValues = new ContentValues();

                reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_MDB_ID, reviewInfo.getString(TMDB_ID));
                reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_AUTHOR, reviewInfo.getString(TMDB_AUTHOR));
                reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_CONTENT, reviewInfo.getString(TMDB_CONTENT));
                reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_URL, reviewInfo.getString(TMDB_URL));
                reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_MOVIE_KEY, dBmovieID );

                boolean isInDB = false;

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        if (reviewInfo.getString(TMDB_ID).equals(cursor.getString(0))) {
                            isInDB = true;
                            break;
                        }
                    }

                    if (!isInDB) {
                        mContext.getContentResolver().insert(MoviesContract.ReviewsEntry.CONTENT_URI, reviewValues);
                    }
                    cursor.moveToPosition(-1);
                }
            }

            if (cursor != null) cursor.close();


        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Cursor cursor = mContext.getContentResolver().query(
                MoviesContract.ReviewsEntry.buildReviewsUriWithID(Long.parseLong(dBmovieID)),
                null,
                null,
                null,
                null);
        if (cursor != null) {
            mAdapter.swapCursor(cursor);
            cursor.close();
        }
        super.onPostExecute(aVoid);
    }

}