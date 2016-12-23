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

import com.mycompany.popmovies.data.MoviesContract;

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
                    Log.v("onItemClicURI",String.valueOf(MoviesContract.MoviesEntry.buildMoviesUriWithID(id)));
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
        Cursor cursor = getActivity().getContentResolver().query(MoviesContract.MoviesEntry.buildMoviesUri(), null, null, null, Utility.sortMethod(getActivity()));
        try {
            if (!cursor.moveToFirst()){
                Log.v("onResume","Getting data from API");
                getDataFromAPI();
            } else {
                Log.v("onResume","Table is not empty");
            }
        } catch (NullPointerException e){
            Log.e("MainActivityFragment", "ERROR", e );
        }
    }

    @Override
    public void onStart(){
        super.onStart();

        //updateImgs();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieUri = MoviesContract.MoviesEntry.buildMoviesUri();
        //Log.v("URI",String.valueOf(movieUri));
        String sortOrder = Utility.sortMethod(getActivity());

        //Utility.sortMethod(getActivity());
        //CursorLoader cursorLoader = new CursorLoader(getActivity(),movieUri, MOVIE_COLUMNS,null,null, sortOrder);
        cursorLoader = new CursorLoader(getActivity(),movieUri, MOVIE_COLUMNS,null,null, sortOrder);

        //Log.v("Cursor Loader", String.valueOf(cursorLoader));

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
}

