package com.mycompany.popmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Borys on 2017-01-21.
 */

public class GridViewAdapter extends BaseAdapter {

    Context context;
    /**String[][] result;*/
    Movie[] result;
    private static LayoutInflater inflater = null;

    public GridViewAdapter(Context mainActivity){
        /**result = new String[0][0];*/
        result = new Movie[0];
        context = mainActivity;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return result.length;
    }

    @Override
    public Movie getItem(int position){
        return result[position];
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    public void setResult(Movie[] imgUrls) {
        result = imgUrls;
        notifyDataSetChanged();
    }

    public class Holder{
        ImageView imgView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        Holder holder;
        View vi = convertView;

        if (convertView == null){
            vi = inflater.inflate(R.layout.grid_item_movie, null);
            holder = new Holder();
            holder.imgView = (ImageView) vi.findViewById(R.id.grid_item_movie_imageview);
            vi.setTag(holder);
        } else
            holder = (Holder) vi.getTag();
        Picasso.with(context).load(result[position].getPosterUri()).into(holder.imgView);
        return vi;
    }



}
