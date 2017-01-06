package com.mycompany.popmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.mycompany.popmovies.data.MoviesContract;

import static com.mycompany.popmovies.Utility.verifyStoragePermissions;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    CursorLoader cursorLoader;
    private static final int MOVIE_LOADER = 0;
    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_NAME,
            MoviesContract.MoviesEntry.COLUMN_POSTER_PATH,
            MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MoviesEntry.COLUMN_OVERVIEW,
            MoviesContract.MoviesEntry.COLUMN_MDB_ID,
            MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE,
            MoviesContract.MoviesEntry.COLUMN_POPULARITY,
            MoviesContract.MoviesEntry.COLUMN_FAV_MOVIE

    };

    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_NAME = 1;
    static final int COL_MOVIE_POSTER_PATH = 2;
    static final int COL_MOVIE_RELEASE_DATE = 3;
    static final int COL_MOVIE_OVERVIEW = 4;
    static final int COL_MOVIE_MDB_ID = 5;
    static final int COL_MOVIE_VOTE_AVERAGE = 6;
    static final int COL_MOVIE_POPULARITY = 7;
    static final int COL_MOVIE_FAV_MOVIE = 8;


    GridView gridView;
    //private GridViewAdapter mGridViewAdapter;
    private GridAdapter mGridAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        mGridAdapter = new GridAdapter(null, getActivity(), 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //mGridViewAdapter = new GridViewAdapter(getActivity());
        //Uri uri = MoviesContract.MoviesEntry.buildMoviesUriWithID()

        gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridView.setAdapter(mGridAdapter);



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if (cursor != null){
                    //Log.v("onItemClicURI",String.valueOf(MoviesContract.MoviesEntry.buildMoviesUriWithID(id)));
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(MoviesContract.MoviesEntry.buildMoviesUriWithID(id));
                    startActivity(intent);
                }



                //Movie movieDetails = mGridViewAdapter.getItem(position);

                //Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra("movie",movieDetails);

            }
        });
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_fragment, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return super.onOptionsItemSelected(item);
    }




    private void getDataFromAPI(){
        FetchMovieInfoTask movieInfoTask = new FetchMovieInfoTask(getContext());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortMethod = prefs.getString(getString(R.string.sort_key),getString(R.string.sort_default));
        movieInfoTask.execute(sortMethod);
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.v("Storage", String.valueOf(
//                Environment.getExternalStorageDirectory().getParent()) + "\n" +
//                Environment.getExternalStorageDirectory().getPath() + "\n" +
//                Environment.getDataDirectory() + "\n" +
//                Environment.getDownloadCacheDirectory() + "\n"+
//                Environment.getRootDirectory()
//        );



        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        String selection;
        String[] selectionArgs = new String[1];

        if (Utility.showFavs(getActivity()).equals("true")) {
            selection = MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry.COLUMN_FAV_MOVIE + " = ? ";
            selectionArgs[0] = Utility.showFavs(getActivity());
        } else {
            selection = null;
            selectionArgs = null;
        }
        //Log.v("onResume", String.valueOf(selection) +" ++ "+ (selection == null ? null : selectionArgs[0]));

        Cursor cursor = getActivity().getContentResolver().query(
                MoviesContract.MoviesEntry.buildMoviesUri(),
                null,
                selection,
                selectionArgs,
                Utility.sortMethod(getActivity()));
/**        Cursor cursor = getActivity().getContentResolver().query(
                MoviesContract.MoviesEntry.buildMoviesUri(),
                null,
                null,
                null,
                Utility.sortMethod(getActivity()));*/
        try {
            if (!cursor.moveToFirst() && Utility.showFavs(getActivity()).equals("false")){
                Log.v("onResume","Getting data from API");
                getDataFromAPI();
            } else if (!cursor.moveToFirst() && Utility.showFavs(getActivity()).equals("true")){
                Toast.makeText(getActivity(),"You don't have favourite movies", Toast.LENGTH_SHORT).show();
            } else {
                Log.v("onResume","Table is not empty");
            }
        } catch (NullPointerException e){
            Log.e("MainActivityFragment", "ERROR", e );
        }
        //cursor.close();
    }

    @Override
    public void onStart(){
        super.onStart();
        verifyStoragePermissions(getActivity());
        //File intSorage = getActivity().getFilesDir();
       // File[] files = intSorage.listFiles();
        //Log.v("Files", "Number of files - " + files.length);
//        for (int i = 0; i < files.length; i++){
//            Log.v("Files","File "+ i + " - "+ files[i]);
//        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieUri = MoviesContract.MoviesEntry.buildMoviesUri();
        String sortOrder = Utility.sortMethod(getActivity());

        String selection;
        String[] selectionArgs = new String[1];

        if (Utility.showFavs(getActivity()).equals("true")) {
            selection = MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry.COLUMN_FAV_MOVIE + " = ? ";
            selectionArgs[0] = Utility.showFavs(getActivity());
        } else {
            selection = null;
            selectionArgs = null;
        }

        cursorLoader = new CursorLoader(getActivity(),movieUri, MOVIE_COLUMNS,selection,selectionArgs, sortOrder);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mGridAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mGridAdapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

    }
    /** Check permissions to read and write files*/
//    private static final int REQUEST_EXTERNAL_STORAGE = 1;
//    private static String[] PERMISSIONS_STORAGE = {
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//    };
//
//    public void verifyStoragePermissions(Activity activity) {
//        int permission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (permission != PackageManager.PERMISSION_GRANTED){
//
//            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
//            Log.v("Permission", "Got permissions");
//        }
//        Log.v("Permission", "Already got them");
//
//    }


}

