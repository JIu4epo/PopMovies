package com.mycompany.popmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;


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
        ImageView imageView = (ImageView) view;
        Picasso.with(context).setIndicatorsEnabled(true);

        String imageBaseUrl = "http://image.tmdb.org/t/p/w342/";
        String imageName = cursor.getString(MainActivityFragment.COL_MOVIE_POSTER_PATH).replace(imageBaseUrl, "");
        String imageUrl = cursor.getString(MainActivityFragment.COL_MOVIE_POSTER_PATH);

        File img = new File(context.getFilesDir() + "/" +imageName);

//        Log.v("GridAdapter", "img - "+img);
//        Log.v("GridAdapter", "imgUrl - "+imageUrl);
//        Log.v("GridAdapter", "imgName - "+imageName);

        if (!img.exists()){
            Utility.imageDownload(context, imageUrl , imageName);
            Picasso.with(context).load(cursor.getString(MainActivityFragment.COL_MOVIE_POSTER_PATH)).into(imageView);
        } else {
            Picasso.with(context).load(img).into(imageView);
        }

        //Picasso.with(context).load(cursor.getString(MainActivityFragment.COL_MOVIE_POSTER_PATH)).into(imageView);
    }
}
