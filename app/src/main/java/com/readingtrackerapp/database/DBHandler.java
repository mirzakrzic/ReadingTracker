package com.readingtrackerapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readingtrackerapp.database.DBContractClass.*;
import com.readingtrackerapp.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anes on 3/23/2018.
 */

public class DBHandler {

    private Context context;
    private SQLiteDatabase db;

    // constructor
    public DBHandler(Context c){

        context=c;
        openDB();


    }


    // DB open & close methods
    private void openDB(){
        db=(new DBCreator(context)).getWritableDatabase();
    }
    public void closeDB(){
        db.close();
    }


    // USER METHODS
    public boolean registerUser(String name, String surname, String date){

        ContentValues values=new ContentValues();
        values.put(USER.COLUMN_NAME,name);
        values.put(USER.COLUMN_SURNAME,surname);
        values.put(USER.COLUMN_REGISTRATION_DATE,date);
        values.put(USER.COLUMN_READ_PAGES_NUMBER,0);

        long num=db.insert(USER.TABLE_NAME,null,values);

        return num!=0;

    }
    public User getUser(){

        Cursor c=db.query(USER.TABLE_NAME,new String[]{USER.COLUMN_ID, USER.COLUMN_NAME, USER.COLUMN_SURNAME, USER.COLUMN_REGISTRATION_DATE, USER.COLUMN_READ_PAGES_NUMBER},null,null,null,null,null);

        return !c.moveToNext()?null:new User(c.getString(1),c.getString(2),c.getString(3),c.getInt(4));

    }
    public boolean isUserRegistered(){

        Cursor c=db.query(USER.TABLE_NAME,new String[]{USER.COLUMN_ID, USER.COLUMN_NAME, USER.COLUMN_SURNAME, USER.COLUMN_REGISTRATION_DATE, USER.COLUMN_READ_PAGES_NUMBER},null,null,null,null,null);


        boolean registered=c.moveToNext();

        c.close();

        return registered;

    }
    public void deleteUser() {

        db.delete(USER.TABLE_NAME,null,null);

    }


    // GENRE METHODS
    public boolean insertGenre(String name){

        ContentValues values=new ContentValues();
        values.put(GENRE.COLUMN_NAME,name);

        long num=db.insert(GENRE.TABLE_NAME,null,values);

        return num!=0;

    }
    public Genre getGenreById(String id){

        Cursor c=db.query(GENRE.TABLE_NAME,new String[]{GENRE.COLUMN_ID,GENRE.COLUMN_NAME},GENRE.COLUMN_ID+"=?",new String[]{id},null,null,null);

        return !c.moveToNext()?null:new Genre(c.getInt(0),c.getString(1));


    }
    public Cursor getAllGenres(){

        Cursor c=db.query(GENRE.TABLE_NAME,new String[]{GENRE.COLUMN_ID,GENRE.COLUMN_NAME},null,null,null,null,GENRE.COLUMN_NAME);

        return c;

       /* List<Genre> genres=new ArrayList<>();

        while (c.moveToNext())
            genres.add(new Genre(c.getInt(0),c.getString(1)));

        return genres;*/


    }


    // BOOK METHODS
    public boolean insertBook(String title, int pagesNumber, String author, int genreId, String timeForNotification){

        ContentValues values=new ContentValues();
        values.put(BOOK.COLUMN_TITLE,title);
        values.put(BOOK.COLUMN_NUMBER_OF_PAGES,pagesNumber);
        values.put(BOOK.COLUMN_AUTHOR_NAME,author);
        values.put(BOOK.COLUMN_RATING,-1);
        values.put(BOOK.COLUMN_GENRE_ID,genreId);
        values.put(BOOK.COLUMN_ALREADY_READ,0);
        values.put(BOOK.COLUMN_CURRENTLY_READING,0);
        values.put(BOOK.COLUMN_FOR_READING,0);
        values.put(BOOK.COLUMN_NUMBER_OF_READ_PAGES,0);
        values.put(BOOK.COLUMN_NOTIFICATION_TIME,timeForNotification);

        long num=db.insert(BOOK.TABLE_NAME,null,values);

        return num!=0;

    }
    public Book getBookById(String id){

        Cursor c=db.query(BOOK.TABLE_NAME,new String[]{BOOK.COLUMN_ID,BOOK.COLUMN_TITLE,BOOK.COLUMN_NUMBER_OF_PAGES,BOOK.COLUMN_AUTHOR_NAME,BOOK.COLUMN_GENRE_ID,BOOK.COLUMN_RATING,BOOK.COLUMN_CURRENTLY_READING,BOOK.COLUMN_ALREADY_READ, BOOK.COLUMN_FOR_READING, BOOK.COLUMN_NOTIFICATION_TIME, BOOK.COLUMN_NUMBER_OF_READ_PAGES},BOOK.COLUMN_ID+"=?",new String[]{id},null,null,null);

        return !c.moveToNext()? null: new Book(c.getInt(0),c.getString(1),c.getInt(2),c.getString(3),c.getInt(4),c.getInt(5),c.getInt(6)>0,c.getInt(7)>0,c.getInt(8)>0,c.getString(9),c.getInt(10));

    }
    public Cursor getBooksByGenreId(String genreId){

        Cursor c=db.query(BOOK.TABLE_NAME,new String[]{BOOK.COLUMN_ID,BOOK.COLUMN_TITLE,BOOK.COLUMN_NUMBER_OF_PAGES,BOOK.COLUMN_AUTHOR_NAME,BOOK.COLUMN_GENRE_ID,BOOK.COLUMN_RATING,BOOK.COLUMN_CURRENTLY_READING,BOOK.COLUMN_ALREADY_READ, BOOK.COLUMN_FOR_READING, BOOK.COLUMN_NOTIFICATION_TIME, BOOK.COLUMN_NUMBER_OF_READ_PAGES},BOOK.COLUMN_GENRE_ID+"=?",new String[]{genreId},null,null,BOOK.COLUMN_TITLE);

        return c;

    }
    public Cursor getCurrentlyReadingBooks(){

        Cursor c=db.query(BOOK.TABLE_NAME,new String[]{BOOK.COLUMN_ID,BOOK.COLUMN_TITLE,BOOK.COLUMN_NUMBER_OF_PAGES,BOOK.COLUMN_AUTHOR_NAME,BOOK.COLUMN_GENRE_ID,BOOK.COLUMN_RATING,BOOK.COLUMN_CURRENTLY_READING,BOOK.COLUMN_ALREADY_READ, BOOK.COLUMN_FOR_READING, BOOK.COLUMN_NOTIFICATION_TIME, BOOK.COLUMN_NUMBER_OF_READ_PAGES},BOOK.COLUMN_CURRENTLY_READING+"=?",new String[]{"1"},null,null,BOOK.COLUMN_TITLE);

        return c;

    }
    public Cursor getReadBooks(){

        Cursor c=db.query(BOOK.TABLE_NAME,new String[]{BOOK.COLUMN_ID,BOOK.COLUMN_TITLE,BOOK.COLUMN_NUMBER_OF_PAGES,BOOK.COLUMN_AUTHOR_NAME,BOOK.COLUMN_GENRE_ID,BOOK.COLUMN_RATING,BOOK.COLUMN_CURRENTLY_READING,BOOK.COLUMN_ALREADY_READ, BOOK.COLUMN_FOR_READING, BOOK.COLUMN_NOTIFICATION_TIME, BOOK.COLUMN_NUMBER_OF_READ_PAGES},BOOK.COLUMN_ALREADY_READ+"=?",new String[]{"1"},null,null,BOOK.COLUMN_TITLE);

        return c;
    }
    public Cursor getBookForReading(){

        Cursor c=db.query(BOOK.TABLE_NAME,new String[]{BOOK.COLUMN_ID,BOOK.COLUMN_TITLE,BOOK.COLUMN_NUMBER_OF_PAGES,BOOK.COLUMN_AUTHOR_NAME,BOOK.COLUMN_GENRE_ID,BOOK.COLUMN_RATING,BOOK.COLUMN_CURRENTLY_READING,BOOK.COLUMN_ALREADY_READ, BOOK.COLUMN_FOR_READING, BOOK.COLUMN_NOTIFICATION_TIME, BOOK.COLUMN_NUMBER_OF_READ_PAGES},BOOK.COLUMN_FOR_READING+"=?",new String[]{"1"},null,null,BOOK.COLUMN_TITLE);

        return c;
    }



}
