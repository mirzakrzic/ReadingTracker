package com.readingtrackerapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.readingtrackerapp.R;
import com.readingtrackerapp.database.DBContractClass;

import java.util.zip.Inflater;

/**
 * Created by Adnan on 6.4.2018.
 */

public class CommentsListAdapter extends CursorAdapter{
    public CommentsListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.comments_list_item,null,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(R.id.comment_text)).setText(cursor.getString(cursor.getColumnIndex(DBContractClass.COMMENT.COLUMN_COMMENT)));
        ((TextView) view.findViewById(R.id.comment_date)).setText(cursor.getString(cursor.getColumnIndex(DBContractClass.COMMENT.COLUMN_DATE)));
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
