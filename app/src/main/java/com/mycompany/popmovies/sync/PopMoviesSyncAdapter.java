package com.mycompany.popmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.mycompany.popmovies.R;
import com.mycompany.popmovies.Utility;
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



public class PopMoviesSyncAdapter extends AbstractThreadedSyncAdapter {
String LOG_TAG = PopMoviesSyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL = 60 * 60 * 24; //Sync one a day
    //public static final int SYNC_INTERVAL = 60 * 3; //Sync every 3 mins

    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public PopMoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.v(LOG_TAG,"syncing");
        HttpURLConnection urlCOnnection = null;
        BufferedReader reader = null;
        String sortMethod = Utility.apiSortMethod(getContext());

        String movieInfoJsonStr = null;
        String apiKey = Utility.getApiKey(); //delete API key when sharing

        try {
            final String MOVIEINFO_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String MOVIEINFO_SORT = "sort_by";
            final String MOVIEINFO_APIKEY = "api_key";
            Uri builtUri = Uri.parse(MOVIEINFO_BASE_URL).buildUpon().
                    appendQueryParameter(MOVIEINFO_SORT, sortMethod).
                    appendQueryParameter(MOVIEINFO_APIKEY, apiKey).build();
            URL url = new URL(builtUri.toString());
            urlCOnnection = (HttpURLConnection) url.openConnection();
            urlCOnnection.setRequestMethod("GET");
            urlCOnnection.connect();

            InputStream inputStream = urlCOnnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null){
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0){
                return;
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


        return;
    }

    private void getMovieInfoDataFromJason (String movieInfoJsonStr) throws JSONException {
        final String TMDB_RESULTS = "results";
        final String TMDB_IMG_URL = "poster_path";
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

            /*Clear table of "un-favorites" before inserting new batch*/
            Utility.clearButFavorites(getContext());

            /*Check if DB is not empty. If not empty - there are Favs*/
            Cursor cursor = getContext().getContentResolver().query(MoviesContract.MoviesEntry.buildMoviesUri(),
                    new String[]{MoviesContract.MoviesEntry.COLUMN_MDB_ID, MoviesContract.MoviesEntry._ID}, null, null, null);

            for (int i =0; i<movieDataArray.length(); i++){

                JSONObject movieInfo = movieDataArray.getJSONObject(i);

                ContentValues moviesValues = new ContentValues();

                moviesValues.put(MoviesContract.MoviesEntry.COLUMN_NAME, movieInfo.getString(TMDB_TITLE));
                moviesValues.put(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH, imageUrlBase + movieInfo.getString(TMDB_IMG_URL));
                moviesValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, movieInfo.getString(TMDB_RELEASE_DATE));
                moviesValues.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, movieInfo.getString(TMDB_OVERVIEW));
                moviesValues.put(MoviesContract.MoviesEntry.COLUMN_MDB_ID, movieInfo.getString(TMDB_ID));
                moviesValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, movieInfo.getString(TMDB_RAITING));
                moviesValues.put(MoviesContract.MoviesEntry.COLUMN_POPULARITY, movieInfo.getString(TMDB_POPULARITY));
                moviesValues.put(MoviesContract.MoviesEntry.COLUMN_FAV_MOVIE, "false");
                moviesValues.put(MoviesContract.MoviesEntry.COLUMN_RUNTIME, 0);

                boolean isInDB = false;
                while (cursor.moveToNext()) {
                    if (movieInfo.getString(TMDB_ID).equals(cursor.getString(0))){
                        isInDB = true;
                        break;

                    }
                }
                if (!isInDB){
                    /*Movie is not in DB, lets add it*/
                    getContext().getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, moviesValues);
                } else {
                    /*Movie is in DB, lets update it's rating and popularity*/
                    ContentValues values = new ContentValues();
                    values.put(MoviesContract.MoviesEntry.COLUMN_POPULARITY, movieInfo.getString(TMDB_POPULARITY));
                    values.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, movieInfo.getString(TMDB_RAITING));

                    getContext().getContentResolver().update(
                            MoviesContract.MoviesEntry.buildMoviesUriWithID(Long.parseLong(cursor.getString(1))),
                            values,
                            null,
                            null
                    );
                }
                cursor.moveToPosition(-1);
            }
            cursor.close();
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /*Helper methods*/
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context){
        PopMoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime){
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if  (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else  {
            ContentResolver.addPeriodicSync(account,authority, new Bundle(),syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }
}
