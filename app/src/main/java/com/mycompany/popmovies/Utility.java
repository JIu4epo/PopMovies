package com.mycompany.popmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mycompany.popmovies.data.MoviesContract;
import com.mycompany.popmovies.data.MoviesDbHelper;

/**
 * Created by Borys on 2016-11-30.
 */

public class Utility {
    public static String sortMethod(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sort =  prefs.getString(context.getString(R.string.sort_key), context.getString(R.string.sort_default));
        Log.v("Utility",sort);

        if (sort.equals(context.getString(R.string.pref_sort_popularity))){
            return MoviesContract.MoviesEntry.COLUMN_POPULARITY + " DESC";
        } else {
            return MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }
    }

    public static void clearTable(Context context, Uri uri){
        context.getContentResolver().delete(uri, null, null);
        //MoviesContract.MoviesEntry.buildMoviesUri()
    }



    public static void dropDB(Context context){
        context.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
    }




}


