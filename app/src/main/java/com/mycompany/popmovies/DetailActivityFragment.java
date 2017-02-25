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
import android.widget.Toast;

import com.mycompany.popmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    TextView tvTitle, tvDate, tvPlot, tvRating, tvRuntime;
    static final String DETAIL_URI = "URI";
    ImageView ivPoster, favStar;
    ListView lvTrailers, lvReviews;
    Cursor reviewsCursor, trailersCursor;
    ImageButton ibFav;
    ScrollView scrollView;

    String dBMovieID, fav, runtime, rating;
    String mDBmovieID;
    Uri mUri;

    ShareActionProvider mShareActionProvider;
    String shareLine;

    private static final int MOVIE_LOADER = 0;
    private static final int VIDEO_LOADER = 1;
    private static final int REVIEW_LOADER = 2;



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

    private static final String[] VIDEO_COLUMNS = {
            MoviesContract.VideosEntry.TABLE_NAME + "." + MoviesContract.VideosEntry._ID,
            MoviesContract.VideosEntry.COLUMN_MOVIE_KEY,
            MoviesContract.VideosEntry.COLUMN_MDB_ID,
            MoviesContract.VideosEntry.COLUMN_TRAILER_PATH
    };

    private static final String[] REVIEW_COLUMNS = {
            MoviesContract.ReviewsEntry.TABLE_NAME + "." + MoviesContract.ReviewsEntry._ID,
            MoviesContract.ReviewsEntry.COLUMN_MOVIE_KEY,
            MoviesContract.ReviewsEntry.COLUMN_MDB_ID,
            MoviesContract.ReviewsEntry.COLUMN_CONTENT,
            MoviesContract.ReviewsEntry.COLUMN_AUTHOR,
            MoviesContract.ReviewsEntry.COLUMN_URL
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

    static final int COL_VIDEO_ID = 0;
    static final int COL_VIDEO_MOVIE_KEY = 1;
    static final int COL_VIDEO_MDB_ID = 2;
    static final int COL_VIDEO_TRAILER_PATH = 3;




    public DetailActivityFragment() {
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader;
        switch (id){
            case MOVIE_LOADER:{
                loader = new CursorLoader(getActivity(),mUri, MOVIE_COLUMNS, null, null, null);
                break;
            }
            case VIDEO_LOADER:{

                Uri uri = MoviesContract.VideosEntry.buildVideosUriWithID(MoviesContract.VideosEntry.getIDFromURI(mUri));
                loader = new CursorLoader(getActivity(),uri, VIDEO_COLUMNS, null, null, null);
                break;
            }
            case REVIEW_LOADER:{
                loader = new CursorLoader(getActivity(),MoviesContract.ReviewsEntry.buildReviewsUriWithID(MoviesContract.ReviewsEntry.getIDFromURI(mUri)), REVIEW_COLUMNS, null, null, null);
                break;
            }

            default: {
                return null;
            }
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {

            case MOVIE_LOADER: {

            if (!data.moveToFirst()) {
                    return;
                }



                /**Update runtime*/
                if (data.getString(COL_MOVIE_RUNTIME).equals("0")){
                    if (Utility.isNetworkAvailable(getActivity())) {
                        FetchMoreMovieInfoTask fetchMoreMovieInfoTask = new FetchMoreMovieInfoTask(getActivity());
                        fetchMoreMovieInfoTask.execute(mDBmovieID, dBMovieID);
                    } else {
                        Toast.makeText(getActivity(), "App needs Internet connection to update runtime, reviews and trailers", Toast.LENGTH_LONG).show();
                    }
                }

                getActivity().setTitle(data.getString(COL_MOVIE_NAME));
                tvTitle.setText(data.getString(COL_MOVIE_NAME));
                rating = data.getString(COL_MOVIE_VOTE_AVERAGE) + "/10";
                tvRating.setText(rating);
                tvPlot.setText(data.getString(COL_MOVIE_OVERVIEW));
                tvDate.setText(Utility.movieYearExtractor(data.getString(COL_MOVIE_RELEASE_DATE)));
                runtime = data.getString(COL_MOVIE_RUNTIME) + "min";
                tvRuntime.setText(runtime);

                fav = data.getString(COL_MOVIE_FAV_MOVIE);
                if (fav.equals("true")) {
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

                File img = new File(getActivity().getFilesDir() + "/" + imageName);

                if (!img.exists()) {
            /*If img does not exist, it will get downloaded. Meanwhile Picasso will use image from the web*/
                    Utility.imageDownload(getActivity(), imageUrl, imageName);
                    Picasso.with(getActivity()).load(data.getString(COL_MOVIE_POSTER_PATH)).into(ivPoster);
                } else {
                    Picasso.with(getActivity()).load(img).into(ivPoster);
                }

            ibFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (fav.equals("false")) {
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
                                MoviesContract.MoviesEntry.buildMoviesUriWithID(Long.parseLong(dBMovieID)),
                                moviesValues,
                                null,
                                null
                        );
                    }
                });
                break;
            }
                case VIDEO_LOADER: {

                    //} else if (loader.getId() == VIDEO_LOADER){
                    lvTrailers = (ListView) getView().findViewById(R.id.listview_trailers);
                    trailersCursor = data;
                    VideosAdapter vAdapter;

                    if (!trailersCursor.moveToFirst()) {
                        vAdapter = new VideosAdapter(null, getActivity(), 0);
                        FetchVideosTask fetchVideosTask = new FetchVideosTask(getActivity(), vAdapter);
                        fetchVideosTask.execute(mDBmovieID, dBMovieID);
                        //return;
                    }
                    /** Trailers */


                    else {
                        trailersCursor.moveToFirst();
                        vAdapter = new VideosAdapter(trailersCursor, getActivity(), 0);
                    }


                    lvTrailers.setAdapter(vAdapter);
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

                    break;
                }

            case REVIEW_LOADER: {

/*        reviewsCursor = getActivity().getContentResolver().query(
                MoviesContract.ReviewsEntry.buildReviewsUriWithID(Long.parseLong(dBMovieID)),
                null,
                null,
                null,
                null);*/
                lvReviews = (ListView) getView().findViewById(R.id.listview_reviews);
                reviewsCursor = data;
                ReviewsAdapter rAdapter;

                if (!reviewsCursor.moveToFirst()) {
                    rAdapter = new ReviewsAdapter(null, getActivity(), 0);
                    FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(getActivity(), rAdapter);
                    fetchReviewsTask.execute(mDBmovieID, dBMovieID);
                } else {
                    reviewsCursor.moveToFirst();
                    rAdapter = new ReviewsAdapter(reviewsCursor, getActivity(), 0);
                }
                lvReviews.setAdapter(rAdapter);
                setListViewHeightBasedOnChildren(lvReviews);

                lvReviews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_VIEW);
                        sendIntent.setData(Uri.parse(reviewsCursor.getString(5)));
                        startActivity(sendIntent);
                    }
                });

                break;
            } default:
        }
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
        scrollView.smoothScrollTo(0, 0);

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent intent = getActivity().getIntent();
        if (intent.getData() == null && getArguments() == null){

            Cursor cursor = getActivity()
                    .getContentResolver()
                    .query(
                            MoviesContract.MoviesEntry.buildMoviesUri(),
                            MOVIE_COLUMNS,
                            null,
                            null,
                            Utility.sortMethod(getActivity())

                    );
            if (cursor == null || !cursor.moveToFirst()){
                //TODO: Put placeholder layout here because no data in DB
                return;
            } else {
                mDBmovieID = cursor.getString(COL_MOVIE_MDB_ID);
                dBMovieID = "1";
                mUri = Uri.parse("content://com.mycompany.popmovies/movies/"+ dBMovieID);
                cursor.close();
            }
        } else {
            if (intent == null || intent.getData() == null) {
                Bundle arguments = getArguments();
                if (arguments != null) {
                    mUri = arguments.getParcelable(DETAIL_URI);
                    mDBmovieID = arguments.getString("mdbID");
                    dBMovieID = String.valueOf(MoviesContract.VideosEntry.getIDFromURI(mUri));
                }
            } else {
                mUri = intent.getData();
                mDBmovieID = intent.getStringExtra("mdbID");
                dBMovieID = String.valueOf(MoviesContract.VideosEntry.getIDFromURI(mUri));
            }
        }
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        getLoaderManager().initLoader(VIDEO_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        if (intent.getData() == null && getArguments() == null) {
            //No intent and no arguments
            Cursor cursor = getActivity()
                    .getContentResolver()
                    .query(
                            MoviesContract.MoviesEntry.buildMoviesUri(),
                            MOVIE_COLUMNS,
                            null,
                            null,
                            Utility.sortMethod(getActivity())

                    );
            if (cursor == null || !cursor.moveToFirst()) {
                //No cursor OR no moveToFirst
                return inflater.inflate(R.layout.fragment_detail_placeholder, container, false);
            } else {
                cursor.close();
            }
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        setHasOptionsMenu(true);

        tvTitle = (TextView) rootView.findViewById(R.id.movie_title);
        tvDate = (TextView) rootView.findViewById(R.id.movie_date);
        tvPlot = (TextView) rootView.findViewById(R.id.movie_plot);
        tvRating = (TextView) rootView.findViewById(R.id.movie_rating);
        ivPoster = (ImageView) rootView.findViewById(R.id.movie_poster);
        ibFav = (ImageButton) rootView.findViewById(R.id.fav_button);
        tvRuntime = (TextView) rootView.findViewById(R.id.movie_run_time);

        scrollView = (ScrollView) rootView.findViewById(R.id.scroll_view);
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