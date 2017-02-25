package com.mycompany.popmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import com.mycompany.popmovies.sync.PopMoviesSyncAdapter;

import static com.mycompany.popmovies.Utility.verifyStoragePermissions;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private int mPosition = GridView.INVALID_POSITION;
    GridView mGridView;
    private static final String SELECTED_KEY = "selected_option";

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


    private GridAdapter mGridAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mGridAdapter = new GridAdapter(null, getActivity(), 0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        mGridView.setAdapter(mGridAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if (cursor != null){
                    ((Callback) getActivity()).onItemSelected(MoviesContract.MoviesEntry.buildMoviesUriWithID(id), cursor.getString(COL_MOVIE_MDB_ID));
                }
                mPosition = position;
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
            //Log.v(LOG_TAG, "mPosition recorded in savedInstanceState - "+mPosition);

        } else {
            //Log.v(LOG_TAG, "Else? ");
            //Log.v(LOG_TAG, "savedInstanceState - "+String.valueOf(savedInstanceState));
            //Log.v(LOG_TAG, "savedInstanceState.containsKey(SELECTED_KEY) - "+savedInstanceState.containsKey(SELECTED_KEY));
        }
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate");

        Log.v(LOG_TAG, "onCreate, savedInstanceState - "+savedInstanceState);


        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_fragment, menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.refresh){
            //getDataFromAPI();
            PopMoviesSyncAdapter.syncImmediately(getActivity());
        }
        return super.onOptionsItemSelected(item);

    }

    private void getDataFromAPI(){
/*        FetchMovieInfoTask movieInfoTask = new FetchMovieInfoTask(getContext());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortMethod = prefs.getString(getString(R.string.sort_key),getString(R.string.sort_default));
        movieInfoTask.execute(sortMethod);*/
        PopMoviesSyncAdapter.syncImmediately(getActivity());

    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        String selection;
        String[] selectionArgs = new String[1];

          //Boolean.parseBoolean(Utility.showFavs(getActivity()))
        if (Utility.showFavs(getActivity()).equals("true")) {
            selection = MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry.COLUMN_FAV_MOVIE + " = ? ";
            selectionArgs[0] = Utility.showFavs(getActivity());
        } else {
            selection = null;
            selectionArgs = null;
        }
        Cursor cursor = getActivity().getContentResolver().query(
                MoviesContract.MoviesEntry.buildMoviesUri(),
                null,
                selection,
                selectionArgs,
                Utility.sortMethod(getActivity()));

        try {
            if (cursor !=null && !cursor.moveToFirst() && Utility.showFavs(getActivity()).equals("false")){
                Log.v(LOG_TAG,"Getting data from API");
                getDataFromAPI();
            } else if (cursor !=null && !cursor.moveToFirst() && Utility.showFavs(getActivity()).equals("true")){
                Toast.makeText(getActivity(),"You don't have favourite movies", Toast.LENGTH_SHORT).show();
            } else if(cursor !=null){
                cursor.close();
            }
        } catch (NullPointerException e){
            Log.e(LOG_TAG, "ERROR", e );
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        verifyStoragePermissions(getActivity());
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

        cursorLoader = new CursorLoader(getActivity(),movieUri, MOVIE_COLUMNS,selection,selectionArgs, sortOrder + " LIMIT 20");//Limit 20
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mGridAdapter.swapCursor(cursor);
        if (mPosition != GridView.INVALID_POSITION){
            mGridView.smoothScrollToPosition(mPosition);
        }
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != GridView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, mPosition);
        }

        super.onSaveInstanceState(outState);
    }



    public interface Callback {
        void onItemSelected(Uri uri, String mdbID);
    }
}
