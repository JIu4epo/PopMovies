package com.mycompany.popmovies;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    public static String apiSortMethod(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.sort_key),context.getString(R.string.sort_default));
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

    public static void clearButFavorites(Context context){
        final String sMovieWithFav =
                MoviesContract.MoviesEntry.TABLE_NAME+ "." + MoviesContract.MoviesEntry.COLUMN_FAV_MOVIE + " = ? ";

        Cursor cursor = context.getContentResolver().query(
                MoviesContract.MoviesEntry.buildMoviesUri(),
                new String[]{MoviesContract.MoviesEntry._ID},
                sMovieWithFav,
                new String[]{"False"},
                null
        );
        if (cursor != null){
                while (cursor.moveToNext()){
                    context.getContentResolver().delete(
                            MoviesContract.ReviewsEntry.buildReviewsUri(),
                            MoviesContract.ReviewsEntry.TABLE_NAME+ "." + MoviesContract.ReviewsEntry.COLUMN_MOVIE_KEY + " = ? ",
                            new String[]{cursor.getString(0)}
                    );

                    context.getContentResolver().delete(
                            MoviesContract.VideosEntry.buildVideosUri(),
                            MoviesContract.VideosEntry.TABLE_NAME+ "." + MoviesContract.VideosEntry.COLUMN_MOVIE_KEY + " = ? ",
                            new String[]{cursor.getString(0)}
                    );
                }

            cursor.close();
        }


        context.getContentResolver().delete(
                MoviesContract.MoviesEntry.buildMoviesUri(),
                sMovieWithFav,
                new String[]{"false"});


    }


    public static void dropDB(Context context){
        context.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
    }

    public static void imageDownload(Context context, String fromUrl, String toUrl){
        Picasso.with(context).load(fromUrl).into(getTarget(toUrl, context));
/*        Log.v("Storage", String.valueOf(
        Environment.getExternalStorageDirectory().getParent()) + "\n" +
        Environment.getExternalStorageDirectory().getPath() + "\n" +
        Environment.getDataDirectory() + "\n" +
        Environment.getDownloadCacheDirectory() + "\n"+
        Environment.getRootDirectory()
        );*/

/*        File intSorage = getActivity().getFilesDir();
         File[] files = intSorage.listFiles();
        Log.v("Files", "Number of files - " + files.length);
        for (int i = 0; i < files.length; i++){
            Log.v("Files","File "+ i + " - "+ files[i]);
        }*/



    }

    private static Target getTarget(final String url, final Context context){
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

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

    public static String movieYearExtractor(String string){
        return string.substring(0,4);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getApiKey(){
        return "fa2461a57ac80bd28b2dc05dcb78f1e6"; //delete API key when sharing

    }

    /** MainActivityFragment.onLoadFinished*/
    /*        if (cursor.getCount()%2 == 1){
            *//*If uneven number of posters(number of favourites can be uneven), add placeholder *//*
            Log.v(LOG_TAG, "Cursor is uneven");
            MatrixCursor placeholderRow = new MatrixCursor(MOVIE_COLUMNS);
            placeholderRow.addRow(new String[]{,null, "placeholder",null,null,null,null, null, null});
            Cursor[] cursors = {placeholderRow,cursor};
            cursor = new MergeCursor(cursors);
        }*/

    /** Check permissions to read and write files*/
/*    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public void verifyStoragePermissions(Activity activity) {
        int permission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            Log.v("Permission", "Got permissions");
        }
        Log.v("Permission", "Already got them");

    }*/

    /**Async for testing NubaApi*/
/*    public class FetchNubaInfo extends AsyncTask<String, Void, Void> {

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

    }*/
}


