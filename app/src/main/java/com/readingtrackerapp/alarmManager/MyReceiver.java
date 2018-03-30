package com.readingtrackerapp.alarmManager;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.readingtrackerapp.R;
import com.readingtrackerapp.activities.RecordReading;
import com.readingtrackerapp.database.DBHandler;
import com.readingtrackerapp.model.Book;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Anes on 3/29/2018.
 */

public class MyReceiver extends BroadcastReceiver {

    private static int notId=0;
    DBHandler dbHandler;
    MyAlarmManager myAlarmManager;


    @Override
    public void onReceive(Context context, Intent intent) {

        myAlarmManager=new MyAlarmManager(context);

        if(intent.getExtras().containsKey("bookId")) {

            dbHandler= new DBHandler(context);

            String bookId =intent.getStringExtra("bookId");

            Book book=dbHandler.getBookById(bookId);

            if(book.isAlreadyRead())
                myAlarmManager.stopAlarmForBook(bookId);
            else
                showNotificationForBook(context, book);


            dbHandler.closeDB();

            return;
        }

        if(intent.getExtras().containsKey("monthlyGoal")){

            showNotificationForMonthlyGoal(context);

            return;
        }
    }

    private void showNotificationForMonthlyGoal(Context context) {

        Intent intent1 = new Intent(context, RecordReading.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 100,
                intent1, PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("NEW MONTHLY GOAL!!!")
                        .setContentText("click to set new monthly goal")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notId++, mBuilder.build());

        myAlarmManager.setAlarmForNextMonthlyGoal();
    }

    private void showNotificationForBook(Context context,Book book){

        Intent intent1 = new Intent(context, RecordReading.class);
        // needs book id to be added to intent ***
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 100,
                intent1, PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(book.getTitle())
                        .setContentText("click to record reading for today")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notId++, mBuilder.build());

    }



}
