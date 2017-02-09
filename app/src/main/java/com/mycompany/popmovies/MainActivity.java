package com.mycompany.popmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mycompany.popmovies.data.MoviesContract;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {

    final String LOG_TAG = MainActivity.class.getSimpleName();
    boolean mTwoPane;
    private final String MAIN_FRAGMENT_TAG = "MFT";
    private final String DETAIL_FRAGMENT_TAG = "DFT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null){
            mTwoPane = true;
            Log.v(LOG_TAG, "TWO PANE MODE");
            if (savedInstanceState == null){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailActivityFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            Log.v(LOG_TAG, "ONE PANE MODE");

        }
/*        MainActivityFragment mainActivityFragment = ((MainActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_activity_fragment));*/
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.clear_table) {
            Utility.clearTable(this, MoviesContract.MoviesEntry.buildMoviesUri());
        } else if (id == R.id.drop_db){
            Utility.dropDB(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //outState.putString("test", "working");
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onItemSelected(Uri uri, String mdbID) {
        if (mTwoPane){
            Log.v(LOG_TAG, "mTwoPane - "+mTwoPane);
            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_URI, uri);
            args.putString("mdbID", mdbID);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAIL_FRAGMENT_TAG)
                    .commit();

        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(uri).putExtra("mdbID",mdbID);
            startActivity(intent);
        }
    }
}
