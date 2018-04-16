package com.readingtrackerapp.alarmManager;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.readingtrackerapp.R;
import com.readingtrackerapp.activities.MainActivity;
import com.readingtrackerapp.activities.RecordReading;
import com.readingtrackerapp.database.DBHandler;
import com.readingtrackerapp.helper.CalendarHelper;
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
        intent1.putExtra("goal","");
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final PendingIntent pendingIntent = PendingIntent.getActivity(context, notId,
                intent1, PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("ReadingTracker*** NEW MONTHLY GOAL!!!")
                        .setContentText("click to set new monthly goal")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notId++, mBuilder.build());

        myAlarmManager.setAlarmForNextMonthlyGoal();
    }

    private void showNotificationForBook(Context context,Book book){

        Calendar calendar=Calendar.getInstance();
        if(dbHandler.isBookReadForToday(Integer.toString(book.getId()),Integer.toString(calendar.get(Calendar.YEAR)),Integer.toString(calendar.get(Calendar.MONTH)),Integer.toString(calendar.get(Calendar.DAY_OF_MONTH))))
            return;


        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(context, RecordReading.class);
        resultIntent.putExtra("bookID",book.getId());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
// Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(notId,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "100");
        builder.setContentIntent(resultPendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ReadingTracker*** "+book.getTitle())
                .setContentText("click to record reading for today")
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notId++, builder.build());

        Log.d("alarm","alarm set=> notID="+notId+"   bookID="+book.getId());

    }



}
