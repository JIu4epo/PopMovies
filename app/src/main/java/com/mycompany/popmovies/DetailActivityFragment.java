package com.mycompany.popmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private String[] mMovieDetailArray;
    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        if (intent !=null && intent.hasExtra("movieDetailsArray")){
            mMovieDetailArray = intent.getStringArrayExtra("movieDetailsArray");

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
        }
        return rootView;
    }
}
