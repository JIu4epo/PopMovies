package com.mycompany.popmovies;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.mycompany.popmovies.data.MoviesContract;
import com.mycompany.popmovies.data.MoviesDbHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

//import java.util.jar.Manifest;

/**
 * Created by Borys on 2016-11-30.
 */

public class Utility {
    public static String sortMethod(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sort =  prefs.getString(context.getString(R.string.sort_key), context.getString(R.string.sort_default));
        //Log.v("Utility",sort);

        if (sort.equals(context.getString(R.string.pref_sort_popularity))){
            return MoviesContract.MoviesEntry.COLUMN_POPULARITY + " DESC";
        } else {
            return MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }
    }

    public static String showFavs(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String showFavs = String.valueOf(prefs.getBoolean(context.getString(R.string.fav_key), false));
        //Log.v("Utility", "showFavs - "+showFavs);
        if (showFavs.equals("true")){
            return "true";
        } else {
            return "false";
        }
    }

    public static void clearTable(Context context, Uri uri){
        context.getContentResolver().delete(uri, null, null);
        //MoviesContract.MoviesEntry.buildMoviesUri()
    }



    public static void dropDB(Context context){
        context.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
    }

    public static void imageDownload(Context context, String fromUrl, String toUrl){
        Picasso.with(context).load(fromUrl).into(getTarget(toUrl, context));
    }

    private static Target getTarget(final String url, final Context context){
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Log.v("Utility", "File Name - "+ url);
                        //Log.v("Utility", "File Path - "+ context.getFilesDir());

                        File file = new File(context.getFilesDir(), url);
                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                            ostream.flush();
                            ostream.close();
                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }

    }


}


