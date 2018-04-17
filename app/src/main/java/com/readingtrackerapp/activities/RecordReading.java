package com.readingtrackerapp.activities;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.readingtrackerapp.R;
import com.readingtrackerapp.alarmManager.MyAlarmManager;
import com.readingtrackerapp.database.DBContractClass;
import com.readingtrackerapp.database.DBHandler;

/**
 * Created by Anes on 3/29/2018.
 */

public class RecordReading extends AppCompatActivity {

    DBHandler dbHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHandler=new DBHandler(getApplicationContext());

        if(getIntent().hasExtra("goal"))
            setMonthlyGoal();
        else
            evidentiateReadPages();
    }

    private void setMonthlyGoal() {


        //setting up alert dialog which is gonna serve to get number of read pages
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(RecordReading.this);
        View view = getLayoutInflater().inflate(R.layout.add_monthly_goal_layout, null);
        final EditText readPages = (EditText) view.findViewById(R.id.input_read_pages);
        Button btnSave = (Button) view.findViewById(R.id.save_numReadPages_btn);

        alert_builder.setView(view);
        final AlertDialog dialog = alert_builder.create();
        dialog.show();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(readPages.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"CANNOT BE EMPTY!",Toast.LENGTH_SHORT).show();
                    setMonthlyGoal();
                }

                String s=readPages.getText().toString();
                if(s==null || s.isEmpty())finish1();
                dbHandler.insertMonthlyGoal(s);

                Toast.makeText(getApplicationContext(),"New monthly goal is set!",Toast.LENGTH_SHORT).show();

                MyAlarmManager alarmManager=new MyAlarmManager(getApplicationContext());
                alarmManager.setAlarmForNextMonthlyGoal();

                finish1();

            }
        });


    }

    public void evidentiateReadPages(){

        final int selected_bookId=getIntent().getIntExtra("bookID",0);

        //setting up alert dialog which is gonna serve to get number of read pages
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(RecordReading.this);
        View view = getLayoutInflater().inflate(R.layout.add_read_pages_layout, null);
        final EditText readPages = (EditText) view.findViewById(R.id.input_read_pages);
        Button btnSave = (Button) view.findViewById(R.id.save_numReadPages_btn);

        final int total_num_of_pages = dbHandler.getNumberOPages(String.valueOf(selected_bookId));
        final int currently_read_pages = dbHandler.getNumberOfReadPages(String.valueOf(selected_bookId));
        readPages.setText(Integer.toString(currently_read_pages));

        alert_builder.setView(view);
        final AlertDialog dialog = alert_builder.create();
        dialog.show();

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish1();
                    dialog.dismiss();
                }
                return false;
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(readPages.getText().toString().isEmpty()){
                    readPages.setError("This field can't be empty!");
                    return;
                }

                int input_readPages = Integer.parseInt(readPages.getText().toString());

                if(input_readPages>total_num_of_pages) { readPages.setError("You can't read more pages than books has!");return; }

                if(input_readPages<currently_read_pages){ readPages.setError("Input value lower than last time recorded");return;}

                if (input_readPages >= total_num_of_pages) {


                    final boolean has_alarm_setup=dbHandler.hasAlatmSet(Integer.toString(selected_bookId));

                    input_readPages=total_num_of_pages;

                    final ContentValues contentValues = new ContentValues();
                    contentValues.put(DBContractClass.BOOK.COLUMN_FOR_READING, "0");
                    contentValues.put(DBContractClass.BOOK.COLUMN_CURRENTLY_READING, "0");
                    contentValues.put(DBContractClass.BOOK.COLUMN_ALREADY_READ, "1");
                    contentValues.put(DBContractClass.BOOK.COLUMN_NUMBER_OF_READ_PAGES, total_num_of_pages);

                    //in case that user entered bigger number of pages, we are setting up new alert to ask does he rlly want to save it

                    final int finalInput_readPages = input_readPages;
                    final int finalInput_readPages1 = input_readPages;
                    final int finalInput_readPages2 = input_readPages;
                    new AlertDialog.Builder(RecordReading.this)
                            .setTitle(getString(R.string.book_read))
                            .setMessage(getString(R.string.congrats))
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, int whichButton) {
                                    String whereClause = DBContractClass.BOOK.COLUMN_ID + "=?";
                                    String[] args = {String.valueOf(selected_bookId)};
                                    boolean updated = dbHandler.updateBook(contentValues, whereClause, args);
                                    dbHandler.evidentateReading(Integer.toString(selected_bookId), finalInput_readPages2 -currently_read_pages);
                                    dbHandler.addreadPages(finalInput_readPages1-currently_read_pages,true);


                                    if (updated) {
                                        //remove alarm
                                        if (has_alarm_setup) {
                                            MyAlarmManager myAlarmManager = new MyAlarmManager(getApplicationContext());
                                            myAlarmManager.stopAlarmForBook(String.valueOf(selected_bookId));
                                        }

                                        //if everything is alright, rate the book
                                        AlertDialog.Builder alert_builder_rating = new AlertDialog.Builder(RecordReading.this);
                                        View view = getLayoutInflater().inflate(R.layout.rate_the_book, null);
                                        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.input_rate);

                                        alert_builder_rating.setView(view);
                                        final AlertDialog dialog1 = alert_builder_rating.create();
                                        dialog1.show();

                                        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                            @Override
                                            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                                                final int rating = (int) ratingBar.getRating();

                                                final ContentValues contentValues_rating = new ContentValues();
                                                contentValues_rating.put(DBContractClass.BOOK.COLUMN_RATING, rating);


                                                if (dbHandler.updateBook(contentValues_rating, DBContractClass.BOOK.COLUMN_ID + "=?", new String[]{String.valueOf(selected_bookId)})) {
                                                    dialog1.hide();

                                                    Intent intent = new Intent(getApplicationContext(), BookDetails.class);
                                                    intent.putExtra("BookID", String.valueOf(selected_bookId));
                                                    startActivity(intent);
                                                    finish();
                                                }

                                            }

                                        });

                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                    dialog.hide();
                }
                else {

                    final ContentValues contentValues = new ContentValues();
                    contentValues.put(DBContractClass.BOOK.COLUMN_NUMBER_OF_READ_PAGES, input_readPages);
                    String whereClause = DBContractClass.BOOK.COLUMN_ID + "=?";
                    String[] args = {String.valueOf(selected_bookId)};
                    //if book is not read totally just update number of read pages.
                    dbHandler.updateBook(contentValues, whereClause, args);
                    dbHandler.evidentateReading(Integer.toString(selected_bookId),input_readPages-currently_read_pages);
                    dbHandler.addreadPages(input_readPages-currently_read_pages,false);

                    finish1();
                }


            }
        });
    }

    @Override
    public void onBackPressed() {
        finish1();
    }

    public void finish1() {
        dbHandler.closeDB();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

}

