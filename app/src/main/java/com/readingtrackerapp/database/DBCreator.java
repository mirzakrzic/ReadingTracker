package com.readingtrackerapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.readingtrackerapp.database.DBContractClass.BOOK;
import com.readingtrackerapp.database.DBContractClass.COMMENT;
import com.readingtrackerapp.database.DBContractClass.GENRE;
import com.readingtrackerapp.database.DBContractClass.MONTHLY_GOAL;
import com.readingtrackerapp.database.DBContractClass.READING_EVIDENTION;
import com.readingtrackerapp.database.DBContractClass.USER;

import java.util.Random;


/**
 * Created by Anes on 3/23/2018.
 */

public class DBCreator extends SQLiteOpenHelper {

    private static final String DB_NAME = "readingTracker.db";
    private static final int VERSION = 1;

    public DBCreator(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    private static final String CREATE_TABLE_USER =
            "CREATE TABLE " + USER.TABLE_NAME + "(" +
                    USER.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    USER.COLUMN_NAME + " TEXT NOT NULL,"+
                    USER.COLUMN_SURNAME+" TEXT NOT NULL,"+
                    USER.COLUMN_REGISTRATION_DATE+" TEXT NOT NULL,"+
                    USER.COLUMN_READ_PAGES_NUMBER+" INTEGER NOT NULL)";


    private static final String CREATE_TABLE_GENRES =
            "CREATE TABLE " + GENRE.TABLE_NAME + "(" +
                   GENRE.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    GENRE.COLUMN_NAME + " TEXT NOT NULL)";

    private static final String CREATE_TABLE_BOOKS=
        "CREATE TABLE "+ BOOK.TABLE_NAME+" ("+
                BOOK.COLUMN_ID+" INTEGER PRIMARY KEY,"+
                BOOK.COLUMN_TITLE+" TEXT NOT NULL,"+
                BOOK.COLUMN_NUMBER_OF_PAGES+" INTEGER NOT NULL,"+
                BOOK.COLUMN_AUTHOR_NAME+" TEXT NOT NULL,"+
                BOOK.COLUMN_GENRE_ID+" INTEGER,"+
                BOOK.COLUMN_RATING+" INTEGER,"+
                BOOK.COLUMN_CURRENTLY_READING+" INTEGER DEFAULT 0,"+
                BOOK.COLUMN_ALREADY_READ+" INTEGER DEFAULT 0,"+
                BOOK.COLUMN_FOR_READING+" INTEGER DEFAULT 0,"+
                BOOK.COLUMN_NOTIFICATION_TIME+" TEXT,"+
                BOOK.COLUMN_NUMBER_OF_READ_PAGES+" INTEGER DEFAULT 0,"+
                "FOREIGN KEY ("+ BOOK.COLUMN_GENRE_ID+") REFERENCES " + GENRE.TABLE_NAME + " (" + GENRE.COLUMN_ID + ") )";

    private static final String CREATE_TABLE_MONTHLY_GOALS=
            "CREATE TABLE "+ MONTHLY_GOAL.TABLE_NAME+" ("+
                    MONTHLY_GOAL.COLUMN_ID+" INTEGER PRIMARY KEY,"+
                    MONTHLY_GOAL.COLUMN_YEAR+" INTEGER NOT NULL,"+
                    MONTHLY_GOAL.COLUMN_MONTH+" INTEGER NOT NULL,"+
                    MONTHLY_GOAL.COLUMN_NUMBER_OF_PAGES+" INTEGER NOT NULL)";

    private static final String CREATE_TABLE_READING_EVIDENTIONS=
            "CREATE TABLE "+ READING_EVIDENTION.TABLE_NAME+" ("+
                    READING_EVIDENTION.COLUMN_ID+" INTEGER PRIMARY KEY,"+
                    READING_EVIDENTION.COLUMN_BOOK_ID+" INTEGER NOT NULL,"+
                    READING_EVIDENTION.COLUMN_YEAR+" INTEGER NOT NULL,"+
                    READING_EVIDENTION.COLUMN_MONTH+" INTEGER NOT NULL,"+
                    READING_EVIDENTION.COLUMN_DAY+" INTEGER NOT NULL,"+
                    READING_EVIDENTION.COLUMN_NUMBER_OF_READ_PAGES+" INTEGER NOT NULL,"+
                    "FOREIGN KEY ("+ READING_EVIDENTION.COLUMN_BOOK_ID+") REFERENCES "+ BOOK.TABLE_NAME+" ("+ BOOK.COLUMN_ID+") )";

    private static final String CREATE_TABLE_COMMENTS=
            "CREATE TABLE "+ COMMENT.TABLE_NAME+" ("+
                    COMMENT.COLUMN_ID+" INTEGER PRIMARY KEY,"+
                    COMMENT.COLUMN_BOOK_ID+" INTEGER NOT NULL,"+
                    COMMENT.COLUMN_DATE+" TEXT NOT NULL,"+
                    COMMENT.COLUMN_COMMENT+" TEXT NOT NULL,"+
                    "FOREIGN KEY ("+ COMMENT.COLUMN_BOOK_ID+") REFERENCES "+ BOOK.TABLE_NAME+" ("+ BOOK.COLUMN_ID+") )";



    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_GENRES);
        db.execSQL(CREATE_TABLE_BOOKS);
        db.execSQL(CREATE_TABLE_MONTHLY_GOALS);
        db.execSQL(CREATE_TABLE_READING_EVIDENTIONS);
        db.execSQL(CREATE_TABLE_COMMENTS);


        String[] genres=new String[]{
                "Fiction","Novel","Science Fiction","Poetry","Romance Novel","Horror fiction"
        };

        for (int i=0;i<genres.length;i++) {
            ContentValues values = new ContentValues();
            values.put(GENRE.COLUMN_NAME,genres[i].toString());
            db.insert(GENRE.TABLE_NAME, null,values);
        }


        Random random_num=new Random();
        for (int i=0;i<20;i++)
        {
            ContentValues values=new ContentValues();
            values.put(BOOK.COLUMN_TITLE,"Title #"+String.valueOf(i));
            values.put(BOOK.COLUMN_NUMBER_OF_PAGES,50+random_num.nextInt(1000));
            values.put(BOOK.COLUMN_AUTHOR_NAME,"Author #"+String.valueOf(i));
            values.put(BOOK.COLUMN_RATING,-1);
            values.put(BOOK.COLUMN_GENRE_ID,"1");
            values.put(BOOK.COLUMN_ALREADY_READ,0);

            values.put(BOOK.COLUMN_CURRENTLY_READING,i%2!=0?0:1);
            values.put(BOOK.COLUMN_FOR_READING,i%2==0?0:1);
            values.put(BOOK.COLUMN_NUMBER_OF_READ_PAGES,"0");

            values.putNull(BOOK.COLUMN_NOTIFICATION_TIME);

            long num=db.insert(BOOK.TABLE_NAME,null,values);
            if(num!=-1){
                Log.e("Inserted data"," Inserted row(ID): "+String.valueOf(num));
            }

        }
        Cursor cursor = db.query(BOOK.TABLE_NAME,new String[]{String.valueOf(BOOK.COLUMN_ID),BOOK.COLUMN_TITLE,BOOK.COLUMN_AUTHOR_NAME,BOOK.COLUMN_NUMBER_OF_PAGES},null,null,null,null,null);
        int cc=cursor.getCount();
        Log.e("Columns_count", String.valueOf(cc));
        String rowContent = "";

        while(cursor.moveToNext()){
            for (int i=0;i<4;i++)
            {
                rowContent+=cursor.getString(i)+" - ";
            }
            Log.e("Row "+String.valueOf(cursor.getPosition()),rowContent);
            rowContent="";
        }
        cursor.close();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS "+ USER.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ MONTHLY_GOAL.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ READING_EVIDENTION.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ COMMENT.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ BOOK.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ GENRE.TABLE_NAME);

        onCreate(db);

    }
}
