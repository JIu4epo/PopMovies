package com.mycompany.popmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private String[] mMovieDetailArray;
    private String[] mVideosResult = {"11","22","33"};
    private VideosAdapter mVideosAdapter;
    TextView textView1;

    public DetailActivityFragment() {
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();



        if (intent !=null && intent.hasExtra("movieDetailsArray")) {
            mMovieDetailArray = intent.getStringArrayExtra("movieDetailsArray");
            Log.v(LOG_TAG, "--"+mMovieDetailArray[0]);
        }
        Log.v(LOG_TAG, "Starting Async");
        FetchMovieVideos movieVideosTask = new FetchMovieVideos();
        movieVideosTask.execute();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "Starting oncreateView");

        Intent intent = getActivity().getIntent();

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        //mVideosAdapter = new VideosAdapter(getActivity());

        mVideosAdapter = new VideosAdapter(getActivity(), R.layout.list_item_trailer, mVideosResult);

        if (intent !=null && intent.hasExtra("movieDetailsArray")){






            //mMovieDetailArray = intent.getStringArrayExtra("movieDetailsArray");

            TextView titleTextView = (TextView) rootView.findViewById(R.id.movie_title);
            titleTextView.setText(mMovieDetailArray[2]);
            TextView plotTextView = (TextView) rootView.findViewById(R.id.movie_plot);
            plotTextView.setText(mMovieDetailArray[3]);
            TextView ratingTextView = (TextView) rootView.findViewById(R.id.movie_rating);
            ratingTextView.setText(mMovieDetailArray[5]);
            ImageView posterImageView = (ImageView) rootView.findViewById(R.id.movie_poster);
            Picasso.with(getActivity()).load(mMovieDetailArray[0]).into(posterImageView);
            TextView dateTextView = (TextView) rootView.findViewById(R.id.movie_date);
            dateTextView.setText(mMovieDetailArray[4]);

//
//            TextView textView1 = (TextView) rootView.findViewById(R.id.movie_trailer);
//            //textView1.
//            trailerTextView.setClickable(true);
//            trailerTextView.setMovementMethod(LinkMovementMethod.getInstance());

            ListView trailersList = (ListView)rootView.findViewById(R.id.listview_trailers);
            Log.v(LOG_TAG, "Before adapter");
            trailersList.setAdapter(mVideosAdapter);



//            if (mVideosResult != null) {
//                Log.v(LOG_TAG, "Succes!!!!");
//                //trailerTextView.setText(Html.fromHtml(getActivity().getString(R.string.trailer_link, mMovieDetailArray[1])));
//                trailerTextView.setText(Html.fromHtml(getActivity().getString(R.string.trailer_link, mVideosResult[0])));
//            } else {
//                Toast.makeText(getActivity(),"ss",Toast.LENGTH_LONG).show();
//            }



        }
        return rootView;
    }


    public class FetchMovieVideos extends AsyncTask<Void, Void, String[]> {
        private final String LOG_TAG = FetchMovieVideos.class.getSimpleName();
        @Override
        protected String[] doInBackground(Void... params){
            Log.v(LOG_TAG, "Start");
//            if (params.length == 0){
//                return null;
//            }
            HttpURLConnection urlCOnnection = null;
            BufferedReader reader = null;

            String movieInfoJsonStr = null;

            String apiKey = "fa2461a57ac80bd28b2dc05dcb78f1e6";


            Intent intent = getActivity().getIntent();
            mMovieDetailArray = intent.getStringArrayExtra("movieDetailsArray");
            try {

                final String MOVIEINFO_BASE_URL= "http://api.themoviedb.org/3/movie/"+mMovieDetailArray[1]+"/videos";

                final String MOVIEINFO_APIKEY = "api_key";
                Uri builtUri = Uri.parse(MOVIEINFO_BASE_URL).buildUpon().
                        appendQueryParameter(MOVIEINFO_APIKEY, apiKey).build();
                Log.v(LOG_TAG, "+++"+builtUri);
                URL url = new URL(builtUri.toString());
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

            } catch (IOException e){
                return null;
            } finally {
                if (urlCOnnection !=null){
                    urlCOnnection.disconnect();
                }
                if (reader !=null){
                    try {
                        reader.close();
                    } catch (final IOException e){
                    }
                }
            }

            try {
                Log.v(LOG_TAG, "Getting json");
                return getMovieVideosFromJason(movieInfoJsonStr);
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;


        }

        private String[] getMovieVideosFromJason (String movieVideosJsonStr) throws JSONException {
            final String TMDB_RESULTS = "results";
            final String TMDB_KEY = "key";
            JSONObject movieVideosJason = new JSONObject(movieVideosJsonStr);
            JSONArray movieVideosArray = movieVideosJason.getJSONArray(TMDB_RESULTS);
            String[] finalArray = new String[movieVideosArray.length()];
            for (int i =0; i<movieVideosArray.length(); i++){
                JSONObject movieVideos = movieVideosArray.getJSONObject(i);
                finalArray[i] = movieVideos.getString(TMDB_KEY);
                Log.v(LOG_TAG, "+++" + finalArray[i]);
            }
            return finalArray;
        }

        @Override
        protected void onPostExecute(String[] result){
            if (result !=null){
                //mVideosResult = result;
                mVideosAdapter.setResult(result);
//                Log.v(LOG_TAG, "Setting results");
//                Log.v(LOG_TAG, "One string "+mVideosResult[0]);
            }
        }
    }
}
