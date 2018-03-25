package com.readingtrackerapp.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Anes on 3/24/2018.
 */

public class CalendarHelper {

    private static Calendar calendar = Calendar.getInstance();
    private static SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a");

    public static String getCurrentlyDateInString(){

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

}
