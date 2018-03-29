package com.readingtrackerapp.alarmManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.readingtrackerapp.helper.CalendarHelper;
import com.readingtrackerapp.model.Book;

import java.io.Serializable;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Anes on 3/29/2018.
 */

public class MyAlarmManager {

    private AlarmManager alarmManager;
    static Context context;

    public MyAlarmManager(Context c){
        alarmManager= (AlarmManager) c.getSystemService(ALARM_SERVICE);
        context=c;
    }

    public void setAlarmForBook(String bookId, String timeForNotification){

        Intent intent=new Intent(context,MyReceiver.class);
        intent.putExtra("bookId",bookId);

        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,Integer.parseInt(bookId),intent,0);

        AlarmManager alarmManager= (AlarmManager) context.getSystemService(ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, CalendarHelper.getDateFromString(timeForNotification).getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);

    }

    public void stopAlarmForBook(String bookId){

        Intent intent=new Intent(context,MyReceiver.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,Integer.parseInt(bookId),intent,0);
        AlarmManager alarmManager= (AlarmManager) context.getSystemService(ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);

    }

    public void setAlarmForNextMonthlyGoal(){

        Intent intent=new Intent(context,MyReceiver.class);
        intent.putExtra("monthlyGoal", "monthlyGoal");

        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,-1,intent,0);

        AlarmManager alarmManager= (AlarmManager) context.getSystemService(ALARM_SERVICE);


        alarmManager.set(AlarmManager.RTC_WAKEUP,getNextMonth() ,pendingIntent);

    }

    private long getNextMonth(){
        // get todays date
        Calendar cal = Calendar.getInstance();
        // get current month
        int currentMonth = cal.get(Calendar.MONTH);

        // move month ahead
        currentMonth++;
        // check if has not exceeded threshold of december

        if(currentMonth > Calendar.DECEMBER){
            // alright, reset month to jan and forward year by 1 e.g fro 2013 to 2014
            currentMonth = Calendar.JANUARY;
            // Move year ahead as well
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)+1);
        }

        // reset calendar to next month
        cal.set(Calendar.MONTH, currentMonth);

        // set the calendar to first day of the month
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY,12);
        long thenTime = cal.getTimeInMillis(); // this is time one month ahead



        return (thenTime); // this is what you set as trigger point time i.e one month after

    }

}
