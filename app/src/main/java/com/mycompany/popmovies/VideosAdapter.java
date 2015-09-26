package com.mycompany.popmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by JIu4epo on 2015-09-23.
 */
public class VideosAdapter extends ArrayAdapter<String[]> {

    Context context;
    int layoutResourceId;
    String[] links;
    private static LayoutInflater inflater = null;


    public VideosAdapter(Context context, int layoutResourceId, String[] links){
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.links = links;
    }

    public void setResult(String[] videosUrls) {
        links = videosUrls;
        notifyDataSetChanged();
    }

    public class Holder{
        TextView linkTextView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        Holder holder = null;
        View row = convertView;
        if (row == null){
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new Holder();
            holder.linkTextView = (TextView)row.findViewById(R.id.textview_trailer_link);
            row.setTag(holder);

        } else {
            holder = (Holder)row.getTag();
        }

        String link = links[position];
        holder.linkTextView.setText(link);

        return row;
    }

}
