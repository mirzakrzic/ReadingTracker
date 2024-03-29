package com.readingtrackerapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.readingtrackerapp.R;
import com.readingtrackerapp.database.DBContractClass;

/**
 * Created by Anes on 3/24/2018.
 */

public class BooksForReadingListAdapter extends CursorAdapter {


    public BooksForReadingListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.books_for_reading_list_item,null,false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // binding views and data from cursor
        ((TextView)view.findViewById(R.id.title)).setText(cursor.getString(cursor.getColumnIndex(DBContractClass.BOOK.COLUMN_TITLE)));
        ((TextView)view.findViewById(R.id.author)).setText(cursor.getString(cursor.getColumnIndex(DBContractClass.BOOK.COLUMN_AUTHOR_NAME)));
        ((TextView)view.findViewById(R.id.pagesNumber)).setText(cursor.getString(cursor.getColumnIndex(DBContractClass.BOOK.COLUMN_NUMBER_OF_PAGES)));
        ((TextView)view.findViewById(R.id.genre)).setText(cursor.getString(cursor.getColumnIndex(DBContractClass.GENRE.COLUMN_NAME)));
        ((ImageView)view.findViewById(R.id.imageView)).setImageResource(R.drawable.ic_book);


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
