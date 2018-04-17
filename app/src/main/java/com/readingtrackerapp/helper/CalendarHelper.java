package com.readingtrackerapp.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Anes on 3/24/2018.
 */

public class CalendarHelper {

    private static SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a");

    public static String getCurrentlyDateInString(){

        Calendar calendar=Calendar.getInstance();

        return format.format(calendar.getTime());

    }

    public static String getDateInString(Calendar calendar){

        return format.format(calendar.getTime());
    }

    public static Calendar getDateFromString(String date){

        Calendar calendar=Calendar.getInstance();

        try {
            calendar.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return calendar;

    }

    public static boolean isBeforeNow(Calendar then){

        Calendar now=Calendar.getInstance();

        return now.getTimeInMillis()>=then.getTimeInMillis();

    }


    public static String getDay()
    {
        Calendar now=Calendar.getInstance();
        return String.valueOf(now.get(Calendar.DAY_OF_MONTH));
    }

    public static String getMonth()
    {
        Calendar now=Calendar.getInstance();
        return String.valueOf(now.get(Calendar.MONTH));
    }

    public static String getYear()
    {
        Calendar now=Calendar.getInstance();
        return String.valueOf(now.get(Calendar.YEAR));
    }
}
