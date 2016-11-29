package com.mycompany.popmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = FetchMovieInfoTask.class.getSimpleName();

    GridView gridView;
    private GridViewAdapter mGridViewAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridViewAdapter = new GridViewAdapter(getActivity());

        gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridView.setAdapter(mGridViewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movieDetails = mGridViewAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra("movie",movieDetails);

                startActivity(intent);
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

    private void updateImgs(){
        FetchMovieInfoTask movieInfoTask = new FetchMovieInfoTask(getContext(), mGridViewAdapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortMethod = prefs.getString(getString(R.string.sort_key),getString(R.string.sort_default));
        movieInfoTask.execute(sortMethod);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateImgs();
    }

    /*public class FetchMovieInfoTask extends AsyncTask<String, Void, Movie[]>{
        private final String LOG_TAG = FetchMovieInfoTask.class.getSimpleName();


        @Override
        protected Movie[] doInBackground(String... params){
            if (params.length == 0){
                return null;
            }
            HttpURLConnection urlCOnnection = null;
            BufferedReader reader = null;

            String movieInfoJsonStr = null;
            //Log.v(LOG_TAG, "+++"+params[0]);
            String apiKey = "fa2461a57ac80bd28b2dc05dcb78f1e6"; //delete API key when sharing

            try {
                final String MOVIEINFO_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String MOVIEINFO_SORT = "sort_by";
                final String MOVIEINFO_APIKEY = "api_key";
                Uri builtUri = Uri.parse(MOVIEINFO_BASE_URL).buildUpon().
                        appendQueryParameter(MOVIEINFO_SORT, params[0]).
                        appendQueryParameter(MOVIEINFO_APIKEY, apiKey).build();
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
                Log.e(LOG_TAG, "Error ", e);
                return null;
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

            try {
                return getMovieInfoDataFromJason(movieInfoJsonStr);
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(),e);
                e.printStackTrace();
            }
            return null;


        }

        private Movie[] getMovieInfoDataFromJason (String movieInfoJsonStr) throws JSONException {
            final String TMDB_RESULTS = "results";
            final String TMDB_IMG_URL = "poster_path"; //maybe need to change to "postar_path"
            final String TMDB_ID = "id";
            final String TMDB_TITLE = "original_title";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_RAITING = "vote_average";
            final String imageUrlBase = "http://image.tmdb.org/t/p/w185";


            JSONObject movieDataJason = new JSONObject(movieInfoJsonStr);
            JSONArray movieDataArray = movieDataJason.getJSONArray(TMDB_RESULTS);

            Movie[] resultArray = new Movie[movieDataArray.length()];

            for (int i =0; i<movieDataArray.length(); i++){

                JSONObject movieInfo = movieDataArray.getJSONObject(i);
                resultArray[i] = new Movie();

                resultArray[i].setId(movieInfo.getString(TMDB_ID)); //TODO: change ID to int
                resultArray[i].setTitle(movieInfo.getString(TMDB_TITLE));
                resultArray[i].setPosterUri(imageUrlBase + movieInfo.getString(TMDB_IMG_URL));
                resultArray[i].setOverview(movieInfo.getString(TMDB_OVERVIEW));
                resultArray[i].setRating(movieInfo.getString(TMDB_RAITING));
                resultArray[i].setDate(movieInfo.getString(TMDB_RELEASE_DATE));
            }
            return resultArray;
        }



        @Override
        protected void onPostExecute(Movie[] result){
            if (result !=null){
                mGridViewAdapter.setResult(result);
            }
        }
    }*/
}

