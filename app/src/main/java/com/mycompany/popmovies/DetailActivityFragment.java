package com.mycompany.popmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mycompany.popmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    TextView tvTitle, tvDate, tvPlot, tvRaiting, tvTrailerLink, tvReview;
    ImageView ivPoster;
    ListView lvTrailers, lvReviews;
    String[] items, itemLabel;
    Cursor reviewsCursor, trailersCursor;

    String dBmovieID;
    String mDBmovieID;

    ShareActionProvider mShareActionProvider;

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

    public DetailActivityFragment() {
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }
        //Log.v(LOG_TAG, String.valueOf(intent.getData()));
        return new CursorLoader(getActivity(),intent.getData(), MOVIE_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()){
            return;
        }

        tvTitle = (TextView) getView().findViewById(R.id.movie_title);
        tvDate = (TextView) getView().findViewById(R.id.movie_date);
        tvPlot = (TextView) getView().findViewById(R.id.movie_plot);
        tvRaiting = (TextView) getView().findViewById(R.id.movie_rating);
        ivPoster = (ImageView) getView().findViewById(R.id.movie_poster);

        tvTitle.setText(data.getString(COL_MOVIE_NAME));
        tvRaiting.setText(data.getString(COL_MOVIE_VOTE_AVERAGE));
        tvPlot.setText(data.getString(COL_MOVIE_OVERVIEW));
        tvDate.setText(data.getString(COL_MOVIE_RELEASE_DATE));
        Picasso.with(getActivity()).load(data.getString(COL_MOVIE_POSTER_PATH)).into(ivPoster);

        dBmovieID = data.getString(COL_MOVIE_ID);
        mDBmovieID = data.getString(COL_MOVIE_MDB_ID);
        //Log.v(LOG_TAG, "dBmovieID - " + dBmovieID);
        //Log.v(LOG_TAG, "mDBmovieID - " + mDBmovieID);

        lvTrailers = (ListView) getView().findViewById(R.id.listview_trailers);
        lvReviews = (ListView) getView().findViewById(R.id.listview_reviews);

        //tvTrailerLink = (TextView) getView().findViewById(R.id.movie_trailer);

//        Cursor cursor = getActivity().getContentResolver().query(MoviesContract.VideosEntry.CONTENT_URI,
//                new String[]{MoviesContract.VideosEntry.COLUMN_TRAILER_PATH},
//                MoviesContract.VideosEntry.COLUMN_MOVIE_KEY,
//                new String[]{dBmovieID},
//                null);
        //Log.v("CorrectUri - ", String.valueOf(MoviesContract.VideosEntry.buildVideosUriWithID(Long.parseLong(dBmovieID))));

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
            //thereIsSomethingInTheTable(cursor);

           // Log.v(LOG_TAG, "Videos Table is not empty anymore");
/**            items = new String[cursor.getCount()];
            int i = 0;
            do {
                Log.v(LOG_TAG, cursor.getString(0) + " - " + cursor.getString(1) + " - " + cursor.getString(2) + " - " + cursor.getString(3));
                items[i] = cursor.getString(3);
                i++;
            } while (cursor.moveToNext());
            List<String> stringList = new ArrayList<String>(Arrays.asList(items)); //new ArrayList is only needed if you absolutely need an ArrayList

            ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_trailer,R.id.textview_trailer_link, stringList);
            listView.setAdapter(adapter);*/

/**        lvReviews.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        lvTrailers.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });*/


            VideosAdapter vAdapter = new VideosAdapter(trailersCursor, getActivity(), 0);
            lvTrailers.setAdapter(vAdapter);

            ReviewsAdapter rAdapter = new ReviewsAdapter(reviewsCursor, getActivity(), 0);
            lvReviews.setAdapter(rAdapter);
            //vAdapter.notifyDataSetChanged();
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
        //cursor.close();
    }

/**    public void thereIsSomethingInTheTable(){

        final Cursor cursor = getActivity().getContentResolver().query(
                //MoviesContract.VideosEntry.CONTENT_URI,
                MoviesContract.VideosEntry.buildVideosUriWithID(Long.parseLong(dBmovieID)),
                null,
                //new String[]{MoviesContract.VideosEntry.COLUMN_TRAILER_PATH},
                null,
                //new String[]{dBmovieID},
                null,
                null);
        //final String[] items;


        if (!cursor.moveToFirst()){

            FetchVideosTask fetchVideosTask = new FetchVideosTask(getActivity());
            fetchVideosTask.execute(mDBmovieID, dBmovieID);

            Log.v(LOG_TAG, "Starting fetchVideos");

            //thereIsSomethingInTheTable(cursor);

        } else {
            //thereIsSomethingInTheTable(cursor);

            Log.v(LOG_TAG, "Videos Table is not empty anymore");
            items = new String[cursor.getCount()];
            itemLabel = new String[cursor.getCount()];
            int i = 0;
            do {
                //Log.v(LOG_TAG, cursor.getString(0) + " - " + cursor.getString(1) + " - " + cursor.getString(2) + " - " + cursor.getString(3));
                items[i] = cursor.getString(3);
                itemLabel[i] = "Trailer "+(i+1);
                i++;
            } while (cursor.moveToNext());
            List<String> stringList = new ArrayList<String>(Arrays.asList(itemLabel)); //new ArrayList is only needed if you absolutely need an ArrayList
            ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_trailer,R.id.textview_trailer_link, stringList);
            //VideosAdapter adapter = new VideosAdapter(getActivity(),R.layout.list_item_trailer,items);
            listView.setAdapter(adapter);



            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_VIEW);
                    sendIntent.setData(Uri.parse(items[position]));
                    startActivity(sendIntent);
                }
            });
        }




        cursor.close();
*//**        final String[] items;

        Log.v(LOG_TAG, "Videos Table is not empty anymore");
        items = new String[cursor.getCount()];
        int i = 0;
        do {
            Log.v(LOG_TAG, cursor.getString(0) + " - " + cursor.getString(1) + " - " + cursor.getString(2) + " - " + cursor.getString(3));
            items[i] = cursor.getString(3);
            i++;
        } while (cursor.moveToNext());
            List<String> stringList = new ArrayList<String>(Arrays.asList(items)); //new ArrayList is only needed if you absolutely need an ArrayList

            ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_trailer,R.id.textview_trailer_link, stringList);
            listView.setAdapter(adapter);*//*
    }*/

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "Starting oncreateView");

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        return rootView;
    }

    private Intent createShareForecastIntent (String uri){
        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        intent.setType("text/html");
        intent.setData(Uri.parse(uri));
        return intent;
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



}
