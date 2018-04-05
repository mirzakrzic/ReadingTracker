package com.readingtrackerapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.readingtrackerapp.R;
import com.readingtrackerapp.database.DBContractClass;

/**
 * Created by Anes on 3/24/2018.
 */

public class CurrentlyReadingBooksListAdapter extends CursorAdapter {


    public CurrentlyReadingBooksListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        // inflating view for curently reading books list view
        return LayoutInflater.from(context).inflate(R.layout.currently_reading_book_list_item,null,false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // binding data from cursor to view
        ((TextView)view.findViewById(R.id.title)).setText(cursor.getString(cursor.getColumnIndex(DBContractClass.BOOK.COLUMN_TITLE)));
        ((TextView)view.findViewById(R.id.author)).setText(cursor.getString(cursor.getColumnIndex(DBContractClass.BOOK.COLUMN_AUTHOR_NAME)));
        ((TextView)view.findViewById(R.id.genre)).setText(cursor.getString(cursor.getColumnIndex(DBContractClass.GENRE.COLUMN_NAME)));
        ((ImageView)view.findViewById(R.id.imageView)).setImageResource(R.drawable.ic_book);


        // setting progres bar
        ProgressBar progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        progressBar.getProgressDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setMax(100);

            // calculating percent of read pages
        int percentOfReadPages=(int)(cursor.getInt(cursor.getColumnIndex("PERCENTAGE")));

            // set percent to progres bar and progress text view
        progressBar.setProgress(50);
        ((TextView)view.findViewById(R.id.progresTxt)).setText(50+"% READ");

    }

    @Override
    public int getCount() {
        if (getCursor() == null) {
            return 0;
        }
        return getCursor().getCount();
    }

    // changing cursor while sorting
    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
    }
}
