package com.readingtrackerapp.alarmManager;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.readingtrackerapp.database.DBContractClass;
import com.readingtrackerapp.database.DBHandler;

/**
 * Created by Anes on 3/30/2018.
 */

public class AfterRebootService extends IntentService {

    public AfterRebootService() {
        super("myIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        // working in background working thread, not main thread

        // getting instances of dbHandler and alarm manager
        DBHandler dbHandler=new DBHandler(getApplicationContext());
        MyAlarmManager alarmManager=new MyAlarmManager(getApplicationContext());

        int bookId;
        String notifTime="";

        // retrieving cursor with all books for setting notification
        Cursor c=dbHandler.getBooksForSettingAlarmAfterReboot();

        while(c.moveToNext()){

            bookId=c.getInt(c.getColumnIndex(DBContractClass.BOOK._ID));
            notifTime=c.getString(c.getColumnIndex(DBContractClass.BOOK.COLUMN_NOTIFICATION_TIME));

            // setting alarm for bookId on notifTime
            alarmManager.setAlarmForBook(Integer.toString(bookId),notifTime);

        }

        // setting alarm for monthly goal notification
        alarmManager.setAlarmForNextMonthlyGoal();

    }


}