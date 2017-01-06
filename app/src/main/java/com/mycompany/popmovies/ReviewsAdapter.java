package com.mycompany.popmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Borys on 2016-12-22.
 */

public class ReviewsAdapter extends CursorAdapter {

    public ReviewsAdapter(Cursor cursor, Context context, int flags){
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_review, viewGroup, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView) view.findViewById(R.id.textview_review_content);
        int position = cursor.getPosition() + 1;

        tv.setText(context.getString(R.string.list_item_review_text) + position);
    }
}
