package com.readingtrackerapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.readingtrackerapp.R;
import com.readingtrackerapp.database.DBContractClass;

/**
 * Created by Anes on 4/15/2018.
 */

public class GoalStatisticAdapter extends CursorAdapter {

    public GoalStatisticAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.goals_stat_list_view_item,null,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ((TextView)view.findViewById(R.id.month)).setText(cursor.getString(cursor.getColumnIndex(DBContractClass.MONTHLY_GOAL.COLUMN_MONTH))+"/"+cursor.getString(cursor.getColumnIndex(DBContractClass.MONTHLY_GOAL.COLUMN_YEAR)));
        ((TextView)view.findViewById(R.id.goal)).setText("GOAL: "+cursor.getInt(cursor.getColumnIndex(DBContractClass.MONTHLY_GOAL.COLUMN_NUMBER_OF_PAGES))+" pages");
        ((TextView)view.findViewById(R.id.achieved)).setText("ACHIEVED: "+cursor.getInt(cursor.getColumnIndex("sum"))+" pages");

        ProgressBar progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        progressBar.getProgressDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setMax(100);



        // calculating percent of read pages
        int percentOfReadPages=(cursor.getInt(cursor.getColumnIndex("sum"))*100)/cursor.getInt(cursor.getColumnIndex(DBContractClass.MONTHLY_GOAL.COLUMN_NUMBER_OF_PAGES));

        // set percent to progres bar and progress text view
        progressBar.setProgress(percentOfReadPages);
        ((TextView)view.findViewById(R.id.progresTxt)).setText(percentOfReadPages+"% READ");


    }
}
