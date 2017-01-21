package com.mycompany.popmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mycompany.popmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    TextView tvTitle, tvDate, tvPlot, tvRaiting, tvRuntime;
    ImageView ivPoster, favStar;
    ListView lvTrailers, lvReviews;
    String[] items, itemLabel;
    Cursor reviewsCursor, trailersCursor;
    ImageButton ibFav;
    ScrollView scrollView;

    String dBmovieID, fav;
    String mDBmovieID;

    ShareActionProvider mShareActionProvider;
    String shareLine;

    private static final int MOVIE_LOADER = 0;
    private static final int VIDEO_LOADER = 1;



    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_NAME,
            MoviesContract.MoviesEntry.COLUMN_POSTER_PATH,
            MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MoviesEntry.COLUMN_OVERVIEW,
            MoviesContract.MoviesEntry.COLUMN_MDB_ID,
            MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE,
            MoviesContract.MoviesEntry.COLUMN_POPULARITY,
            MoviesContract.MoviesEntry.COLUMN_FAV_MOVIE,
            MoviesContract.MoviesEntry.COLUMN_RUNTIME
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
    static final int COL_MOVIE_RUNTIME = 9;


    public DetailActivityFragment() {
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }
        return new CursorLoader(getActivity(),intent.getData(), MOVIE_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        if (!data.moveToFirst()){
            return;
        }

        tvTitle = (TextView) getView().findViewById(R.id.movie_title);
        tvDate = (TextView) getView().findViewById(R.id.movie_date);
        tvPlot = (TextView) getView().findViewById(R.id.movie_plot);
        tvRaiting = (TextView) getView().findViewById(R.id.movie_rating);
        ivPoster = (ImageView) getView().findViewById(R.id.movie_poster);
        ibFav = (ImageButton) getView().findViewById(R.id.fav_button);
        tvRuntime = (TextView) getView().findViewById(R.id.movie_run_time);


        scrollView = (ScrollView) getView().findViewById(R.id.scroll_view);



        tvTitle.setText(data.getString(COL_MOVIE_NAME));
        tvRaiting.setText(data.getString(COL_MOVIE_VOTE_AVERAGE));
        tvPlot.setText(data.getString(COL_MOVIE_OVERVIEW));
        tvDate.setText(data.getString(COL_MOVIE_RELEASE_DATE));
        tvRuntime.setText(data.getString(COL_MOVIE_RUNTIME));

        fav = data.getString(COL_MOVIE_FAV_MOVIE);
        if (fav.equals("true")){
            ibFav.clearColorFilter();
        } else {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);

            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            ibFav.setColorFilter(filter);

        }


        shareLine = "Check out " + data.getString(COL_MOVIE_NAME) + " #PopMovies";


        /* Downloading image to internalStorage. Using cache while image is downloading */
        String imageBaseUrl = "http://image.tmdb.org/t/p/w342/";
        String imageName = data.getString(COL_MOVIE_POSTER_PATH).replace(imageBaseUrl, "");
        String imageUrl = data.getString(COL_MOVIE_POSTER_PATH);

        File img = new File(getActivity().getFilesDir() + "/" +imageName);

        if (!img.exists()){
            /*If img does not exist, it will get downloaded. Meanwhile Picasso will use image from the web*/
            Utility.imageDownload(getActivity(), imageUrl , imageName);
            Picasso.with(getActivity()).load(data.getString(COL_MOVIE_POSTER_PATH)).into(ivPoster);
        } else {
            Picasso.with(getActivity()).load(img).into(ivPoster);
        }
        /*  */

        dBmovieID = data.getString(COL_MOVIE_ID);
        mDBmovieID = data.getString(COL_MOVIE_MDB_ID);


        ibFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fav.equals("false")){
                    fav = "true";
                    ibFav.clearColorFilter();
                } else {
                    fav = "false";
                    ColorMatrix matrix = new ColorMatrix();
                    matrix.setSaturation(0);

                    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                    ibFav.setColorFilter(filter);
                }

                final ContentValues moviesValues = new ContentValues();
                moviesValues.put(MoviesContract.MoviesEntry.COLUMN_FAV_MOVIE, fav);

                int rowsUpdated = getActivity().getContentResolver().update(
                        MoviesContract.MoviesEntry.buildMoviesUriWithID(Long.parseLong(dBmovieID)),
                        moviesValues,
                        null,
                        null
                );
            }
        });



        lvTrailers = (ListView) getView().findViewById(R.id.listview_trailers);
        lvReviews = (ListView) getView().findViewById(R.id.listview_reviews);



        trailersCursor = getActivity().getContentResolver().query(
                MoviesContract.VideosEntry.buildVideosUriWithID(Long.parseLong(dBmovieID)),
                null,
                null,
                null,
                null);

        reviewsCursor = getActivity().getContentResolver().query(
                MoviesContract.ReviewsEntry.buildReviewsUriWithID(Long.parseLong(dBmovieID)),
                null,
                null,
                null,
                null);

        if (!trailersCursor.moveToFirst()){
            FetchVideosTask fetchVideosTask = new FetchVideosTask(getActivity());
            fetchVideosTask.execute(mDBmovieID, dBmovieID);
        }

        if (!reviewsCursor.moveToFirst()){
            FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(getActivity());
            fetchReviewsTask.execute(mDBmovieID, dBmovieID);
        }

            VideosAdapter vAdapter = new VideosAdapter(trailersCursor, getActivity(), 0);
            lvTrailers.setAdapter(vAdapter);

            ReviewsAdapter rAdapter = new ReviewsAdapter(reviewsCursor, getActivity(), 0);
            lvReviews.setAdapter(rAdapter);
        setListViewHeightBasedOnChildren(lvReviews);
        setListViewHeightBasedOnChildren(lvTrailers);


        lvTrailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse(trailersCursor.getString(3)));
                startActivity(sendIntent);
            }
        });

        lvReviews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse(reviewsCursor.getString(5)));
                startActivity(sendIntent);
            }
        });

        if (mShareActionProvider != null){
            Log.v(LOG_TAG, "mShareActionProvider is not null");
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }


        scrollView.smoothScrollTo(0,0);
        //cursor.close();

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }


    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ActionBar.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_fragment, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (shareLine != null){
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent (){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/plane");
        intent.putExtra(Intent.EXTRA_TEXT, shareLine);
        return intent;
    }
}
