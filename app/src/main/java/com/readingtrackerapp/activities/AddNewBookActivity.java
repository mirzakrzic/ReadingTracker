package com.readingtrackerapp.activities;

import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.readingtrackerapp.R;
import com.readingtrackerapp.alarmManager.MyAlarmManager;
import com.readingtrackerapp.database.DBContractClass;
import com.readingtrackerapp.database.DBHandler;
import com.readingtrackerapp.database.DBContractClass.*;
import com.readingtrackerapp.helper.CalendarHelper;

import java.util.Calendar;

/**
 * Created by Anes on 3/29/2018.
 */

public class AddNewBookActivity extends AppCompatActivity {

    EditText title,author,numberOfPages,notifTime;
    Spinner genreSpinner;
    CheckBox getNotificationsCheckBox, setCurrentlyOnReadingCheckBox;
    LinearLayout notificationLayout;
    DBHandler dbHandler;
    Calendar calendar;
    int genreId;
    MyAlarmManager alarmManager;

    // thread fot waiting until snackbar is active
    Thread thread = new Thread(){
        @Override
        public void run() {
            try {
                Thread.sleep(1000); // As I am using LENGTH_LONG in SnackBar
                AddNewBookActivity.this.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_book_layout);

        // dbHandler instance, IMPORTANT: close db in onDestroy method
        dbHandler=new DBHandler(getApplicationContext());

        // alarm manager instance
        alarmManager=new MyAlarmManager(getApplicationContext());

        // setting all views to starting point
        setViews();
        setCheckbox();
        setSpinner();

    }

    private void setViews() {

        title=(EditText)findViewById(R.id.title);
        author=(EditText)findViewById(R.id.author);
        numberOfPages=(EditText)findViewById(R.id.numberOfPages);
        notifTime=(EditText)findViewById(R.id.notificationTime);
        notificationLayout=(LinearLayout) findViewById(R.id.notifLayout);

        // setting notification time to invisible
        notificationLayout.setVisibility(View.INVISIBLE);

    }

    private void setCheckbox() {

        setCurrentlyOnReadingCheckBox=(CheckBox)findViewById(R.id.currentlyOnReadingCB);
        getNotificationsCheckBox=(CheckBox)findViewById(R.id.getNotificationsCB);

        // if checkbox for setting boog on currently reading list is checked, enable checkbox for getting notifications
        setCurrentlyOnReadingCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                getNotificationsCheckBox.setEnabled(b);
                if(!b)
                    getNotificationsCheckBox.setChecked(false);
            }
        });

        // if checkbox for getting notifications is checked, show time picker for setting time for notification
        getNotificationsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                notificationLayout.setVisibility(b?View.VISIBLE:View.INVISIBLE);

                if(b){

                    calendar=Calendar.getInstance();

                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(AddNewBookActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            notifTime.setText( selectedHour + ":" + selectedMinute);

                            calendar.set(Calendar.HOUR_OF_DAY,selectedHour);
                            calendar.set(Calendar.MINUTE,selectedMinute);

                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select time for getting notifications");
                    mTimePicker.show();

                }
            }
        });


        // initially settings
        setCurrentlyOnReadingCheckBox.setChecked(false);
        getNotificationsCheckBox.setEnabled(false);

    }

    private void setSpinner() {
        genreSpinner=(Spinner)findViewById(R.id.spinner);

        // setting cursor spinner adapter with all genres
        final Cursor cursor=dbHandler.getAllGenres();
        String[] adapterCols=new String[]{GENRE.COLUMN_NAME};
        int[] adapterRowViews=new int[]{android.R.id.text1};
        final SimpleCursorAdapter adapter=new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, cursor, adapterCols, adapterRowViews,0);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(adapter);

        // save genreId into genreId variable after selecting spinner item
        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cursor.moveToPosition(i);
                genreId=cursor.getInt(cursor.getColumnIndex(GENRE.COLUMN_ID));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        // setting Unknown genre initially
        for(int i = 0; i < adapter.getCount(); i++){
            cursor.moveToPosition(i);
            if ( cursor.getString(cursor.getColumnIndex(GENRE.COLUMN_NAME)).contentEquals("Unknown")){
                genreSpinner.setSelection(i);
                break;
            }
        }

    }

    public void saveBook(View view) {


        if(title.getText().toString().isEmpty() ||author.getText().toString().isEmpty() || numberOfPages.getText().toString().isEmpty())
        {
            Toast.makeText(this,"Error invalid data or missing",Toast.LENGTH_LONG).show();
            return;

        }

        // saving book
        int id=dbHandler.insertBook(title.getText().toString(),Integer.parseInt(numberOfPages.getText().toString()),author.getText().toString(),genreId,getNotificationsCheckBox.isChecked()?CalendarHelper.getDateInString(calendar):null,setCurrentlyOnReadingCheckBox.isChecked());


        // setting notification if getNotificationCheckBox is checked
        if(getNotificationsCheckBox.isChecked()) {

            // if time of notification is past for today set alarm for next day
            if(CalendarHelper.isBeforeNow(calendar))
                calendar.add(Calendar.DAY_OF_WEEK, 1);

            alarmManager.setAlarmForBook(Integer.toString(id), CalendarHelper.getDateInString(calendar));
        }

        // showing snackbar
        Snackbar.make(findViewById(R.id.content),"Book sucessfully saved!",Snackbar.LENGTH_SHORT).show();
        // activating thread for closing activity after 1 sec, making snackbar visible for 1 sec
        thread.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHandler.closeDB();
    }


}
