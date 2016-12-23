package com.mycompany.popmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


/**
 * Created by Borys on 2016-11-29.
 */

public class GridAdapter extends CursorAdapter {

    public GridAdapter(Cursor cursor, Context context, int flags){
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_movie, viewGroup, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView img = (ImageView) view;
        //ImageButton img = (ImageButton) view;
        Picasso.with(context).load(cursor.getString(MainActivityFragment.COL_MOVIE_POSTER_PATH)).into(img);
        //Log.d("GridAdapter","--"+cursor.getString(MainActivityFragment.COL_MOVIE_POSTER_PATH));
    }
}
