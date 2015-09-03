package com.mycompany.popmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by JIu4epo on 2015-08-28.
 */


public class ImageAdapter extends BaseAdapter {

    Context context;
    String[][] result;
    private static LayoutInflater inflater = null;

    public ImageAdapter(Context mainActivity){
        result = new String[0][0];
        context = mainActivity;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return result.length;
    }

    @Override
    public String[] getItem(int position){

        String[] res = {result[position][0],
                result[position][1],
                result[position][2],
                result[position][3],
                result[position][4],
                result[position][5]};
        return res;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    public void setResult(String[][] imgUrls) {
        result = imgUrls;
        notifyDataSetChanged();
    }

    public class Holder{
        ImageView imgView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.grid_item_movie, null);
        holder.imgView = (ImageView) rowView.findViewById(R.id.grid_item_movie_imageview);

        Picasso.with(context).load(result[position][0]).into(holder.imgView);
        return rowView;
    }



}
