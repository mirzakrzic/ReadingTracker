package com.readingtrackerapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.Toast;

import com.readingtrackerapp.database.DBContractClass.BOOK;
import com.readingtrackerapp.database.DBContractClass.GENRE;
import com.readingtrackerapp.database.DBContractClass.USER;
import com.readingtrackerapp.model.Book;
import com.readingtrackerapp.model.Genre;
import com.readingtrackerapp.model.User;
import com.readingtrackerapp.helper.CalendarHelper;

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

        Cursor c=db.query(GENRE.TABLE_NAME,new String[]{GENRE.COLUMN_ID, GENRE.COLUMN_NAME}, GENRE.COLUMN_ID+"=?",new String[]{id},null,null,null);

        return !c.moveToNext()?null:new Genre(c.getInt(0),c.getString(1));


    }
    public Cursor getAllGenres(){

        Cursor c=db.query(GENRE.TABLE_NAME,new String[]{GENRE.COLUMN_ID, GENRE.COLUMN_NAME},null,null,null,null, GENRE.COLUMN_NAME);

        return c;

    }


    // BOOK METHODS
    public int insertBook(String title, int pagesNumber, String author, int genreId, String timeForNotification, boolean currentlyOnReading){

        ContentValues values=new ContentValues();
        values.put(BOOK.COLUMN_TITLE,title);
        values.put(BOOK.COLUMN_NUMBER_OF_PAGES,pagesNumber);
        values.put(BOOK.COLUMN_AUTHOR_NAME,author);
        values.put(BOOK.COLUMN_RATING,-1);
        values.put(BOOK.COLUMN_GENRE_ID,genreId);
        values.put(BOOK.COLUMN_ALREADY_READ,0);
        values.put(BOOK.COLUMN_CURRENTLY_READING,currentlyOnReading);
        values.put(BOOK.COLUMN_FOR_READING,!currentlyOnReading);
        values.put(BOOK.COLUMN_NUMBER_OF_READ_PAGES,0);
        if(timeForNotification!=null)
            values.put(BOOK.COLUMN_NOTIFICATION_TIME,timeForNotification);
        else
            values.putNull(BOOK.COLUMN_NOTIFICATION_TIME);

        return (int)db.insert(BOOK.TABLE_NAME,null,values);


    }


    public int recordReading(String bookID, int numPages)
    {
        //Just insert function when recording reading but this one records values for READING_EVIDENTION TABLE

        ContentValues contentValues=new ContentValues();
        contentValues.put(DBContractClass.READING_EVIDENTION.COLUMN_BOOK_ID,bookID);
        contentValues.put(DBContractClass.READING_EVIDENTION.COLUMN_NUMBER_OF_READ_PAGES,numPages);
        contentValues.put(DBContractClass.READING_EVIDENTION.COLUMN_DAY,CalendarHelper.getDay());
        contentValues.put(DBContractClass.READING_EVIDENTION.COLUMN_MONTH,CalendarHelper.getMonth());
        contentValues.put(DBContractClass.READING_EVIDENTION.COLUMN_YEAR,CalendarHelper.getYear());
        int inserted=0;
        try {
            inserted = (int) db.insert(DBContractClass.READING_EVIDENTION.TABLE_NAME, null, contentValues);
            Log.e("Record_r","READING_EVIDENTION Instanced! "+String.valueOf(numPages)+" pages read");

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return inserted;
    }

    public int getNumberOfReadPages_EvidentionTable(String Book_id)
    {
        Cursor c=db.query(DBContractClass.READING_EVIDENTION.TABLE_NAME,new String[]{DBContractClass.READING_EVIDENTION.COLUMN_ID, DBContractClass.READING_EVIDENTION.COLUMN_NUMBER_OF_READ_PAGES},
                DBContractClass.READING_EVIDENTION.COLUMN_BOOK_ID+"=?",new String[]{Book_id},null,null,null);
        int num_read_pages_Totally=0;

        while(c.moveToNext()){
            num_read_pages_Totally+=c.getInt(1);
        }
        Log.e("Num_read_ET",String.valueOf(num_read_pages_Totally));

        return num_read_pages_Totally;
    }



    public Book getBookById(String id){

        Cursor c=db.query(BOOK.TABLE_NAME,new String[]{BOOK.COLUMN_ID, BOOK.COLUMN_TITLE, BOOK.COLUMN_NUMBER_OF_PAGES, BOOK.COLUMN_AUTHOR_NAME, BOOK.COLUMN_GENRE_ID, BOOK.COLUMN_RATING, BOOK.COLUMN_CURRENTLY_READING, BOOK.COLUMN_ALREADY_READ, BOOK.COLUMN_FOR_READING, BOOK.COLUMN_NOTIFICATION_TIME, BOOK.COLUMN_NUMBER_OF_READ_PAGES}, BOOK.COLUMN_ID+"=?",new String[]{id},null,null,null);

        return !c.moveToNext()? null: new Book(c.getInt(0),c.getString(1),c.getInt(2),c.getString(3),c.getInt(4),c.getInt(5),c.getInt(6)>0,c.getInt(7)>0,c.getInt(8)>0,c.getString(9),c.getInt(10));

    }

    public Cursor getBooksByGenreId(String genreId){

        Cursor c=db.query(BOOK.TABLE_NAME,new String[]{BOOK.COLUMN_ID, BOOK.COLUMN_TITLE, BOOK.COLUMN_NUMBER_OF_PAGES, BOOK.COLUMN_AUTHOR_NAME, BOOK.COLUMN_GENRE_ID, BOOK.COLUMN_RATING, BOOK.COLUMN_CURRENTLY_READING, BOOK.COLUMN_ALREADY_READ, BOOK.COLUMN_FOR_READING, BOOK.COLUMN_NOTIFICATION_TIME, BOOK.COLUMN_NUMBER_OF_READ_PAGES}, BOOK.COLUMN_GENRE_ID+"=?",new String[]{genreId},null,null, BOOK.COLUMN_TITLE);

        return c;

    }

    public Cursor getCurrentlyReadingBooks(boolean ascending,String orderColumn, String bookTitle) {

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        // join genres and books tables
        builder.setTables(BOOK.TABLE_NAME + " join " + GENRE.TABLE_NAME + " on " + BOOK.TABLE_NAME + "." + BOOK.COLUMN_GENRE_ID + "=" + GENRE.TABLE_NAME + "." + GENRE.COLUMN_ID);

        // selection statement and arguments for query WHERE
        String selection;
        String [] arguments;

        // if search text is not empty
        if(!bookTitle.isEmpty()){

            // curentlyOnReading = 1 AND title like searchBookTitleText
           selection=BOOK.COLUMN_CURRENTLY_READING+"=? AND "+ BOOK.COLUMN_TITLE+" like ?";
           arguments=new String[]{"1",bookTitle+"%"};
        }
        // if search text is not empty
        else{
            // currentlyOnReading=1
            selection=BOOK.COLUMN_CURRENTLY_READING+"=?";
            arguments=new String[]{"1"};
        }

        // querying db
        Cursor c = builder.query(db, new String[]{BOOK.TABLE_NAME + "." + BOOK.COLUMN_ID, "((" + BOOK.COLUMN_NUMBER_OF_READ_PAGES + "*100)/" + BOOK.COLUMN_NUMBER_OF_PAGES + ") as PERCENTAGE", BOOK.COLUMN_TITLE, BOOK.COLUMN_NUMBER_OF_PAGES, BOOK.COLUMN_AUTHOR_NAME, BOOK.COLUMN_GENRE_ID, GENRE.COLUMN_NAME, BOOK.COLUMN_RATING, BOOK.COLUMN_CURRENTLY_READING, BOOK.COLUMN_ALREADY_READ, BOOK.COLUMN_FOR_READING, BOOK.COLUMN_NOTIFICATION_TIME, BOOK.COLUMN_NUMBER_OF_READ_PAGES}, selection, arguments, null, null, orderColumn + (ascending ? " ASC" : " DESC"));

        String msg="";
        int cc=c.getColumnCount();

        while(c.moveToNext()) {

            for (int i=0;i<cc;i++)
            {
                msg+=c.getString(i)+" - ";
            }

            Log.e("Data retrieved: ", msg);
            msg="";
        }
        return c;

    }

    public Cursor getReadBooks(boolean ascending,String orderColumn, String bookTitle){

        // same comments for getCurrentlyReading books

        SQLiteQueryBuilder builder=new SQLiteQueryBuilder();

        builder.setTables(BOOK.TABLE_NAME+" join "+ GENRE.TABLE_NAME+" on "+ BOOK.TABLE_NAME+"."+ BOOK.COLUMN_GENRE_ID+"="+ GENRE.TABLE_NAME+"."+ GENRE.COLUMN_ID);

        String selection;
        String [] arguments;


        if(!bookTitle.isEmpty()){
            selection=BOOK.COLUMN_ALREADY_READ+"=? AND "+ BOOK.COLUMN_TITLE+" like ?";
            arguments=new String[]{"1",bookTitle+"%"};
        }
        else{
            selection=BOOK.COLUMN_ALREADY_READ+"=?";
            arguments=new String[]{"1"};
        }

        Cursor c=builder.query(db,new String[]{BOOK.TABLE_NAME+"."+ BOOK.COLUMN_ID, BOOK.COLUMN_TITLE, BOOK.COLUMN_NUMBER_OF_PAGES, BOOK.COLUMN_AUTHOR_NAME, BOOK.COLUMN_GENRE_ID, GENRE.COLUMN_NAME, BOOK.COLUMN_RATING, BOOK.COLUMN_CURRENTLY_READING, BOOK.COLUMN_ALREADY_READ, BOOK.COLUMN_FOR_READING, BOOK.COLUMN_NOTIFICATION_TIME, BOOK.COLUMN_NUMBER_OF_READ_PAGES}, selection,arguments,null,null, orderColumn+(ascending?" ASC":" DESC"));

        String msg="";
        int cc=c.getColumnCount();

        while(c.moveToNext()) {

            for (int i=0;i<cc;i++)
            {
                msg+=c.getString(i)+" - ";
            }

            Log.e("Data retrieved rb: ", msg);
            msg="";
        }

        return c;
    }

    public Cursor getBookForReading(boolean ascending,String orderColumn, String bookTitle){

        // same comments for getCurrentlyReading books

        SQLiteQueryBuilder builder=new SQLiteQueryBuilder();

        builder.setTables(BOOK.TABLE_NAME+" join "+ GENRE.TABLE_NAME+" on "+ BOOK.TABLE_NAME+"."+ BOOK.COLUMN_GENRE_ID+"="+ GENRE.TABLE_NAME+"."+ GENRE.COLUMN_ID);

        String selection;
        String [] arguments;


        if(!bookTitle.isEmpty()){
            selection=BOOK.COLUMN_FOR_READING+"=? AND "+ BOOK.COLUMN_TITLE+" like ?";
            arguments=new String[]{"1",bookTitle+"%"};
        }
        else{
            selection=BOOK.COLUMN_FOR_READING+"=?";
            arguments=new String[]{"1"};
        }

        Cursor c=builder.query(db,new String[]{BOOK.TABLE_NAME+"."+ BOOK.COLUMN_ID, BOOK.COLUMN_TITLE, BOOK.COLUMN_NUMBER_OF_PAGES, BOOK.COLUMN_AUTHOR_NAME, BOOK.COLUMN_GENRE_ID, GENRE.COLUMN_NAME, BOOK.COLUMN_RATING, BOOK.COLUMN_CURRENTLY_READING, BOOK.COLUMN_ALREADY_READ, BOOK.COLUMN_FOR_READING, BOOK.COLUMN_NOTIFICATION_TIME, BOOK.COLUMN_NUMBER_OF_READ_PAGES}, selection,arguments,null,null, orderColumn+(ascending?" ASC":" DESC"));
        //Cursor c = builder.query(db, new String[]{BOOK.TABLE_NAME + "." + BOOK.COLUMN_ID, "((" + BOOK.COLUMN_NUMBER_OF_READ_PAGES + "*100)/" + BOOK.COLUMN_NUMBER_OF_PAGES + ") as PERCENTAGE", BOOK.COLUMN_TITLE, BOOK.COLUMN_NUMBER_OF_PAGES, BOOK.COLUMN_AUTHOR_NAME, BOOK.COLUMN_GENRE_ID, GENRE.COLUMN_NAME, BOOK.COLUMN_RATING, BOOK.COLUMN_CURRENTLY_READING, BOOK.COLUMN_ALREADY_READ, BOOK.COLUMN_FOR_READING, BOOK.COLUMN_NOTIFICATION_TIME, BOOK.COLUMN_NUMBER_OF_READ_PAGES}, selection, arguments, null, null, orderColumn + (ascending ? " ASC" : " DESC"));

        String msg="";
        int cc=c.getColumnCount();

        while(c.moveToNext()) {

            for (int i=0;i<cc;i++)
            {
                msg+=c.getString(i)+" - ";
            }

            Log.e("Data retrieved bfr: ", msg);
            msg="";
        }

        return c;
    }
    public Cursor getBooksForSettingAlarmAfterReboot(){

        return db.rawQuery("select "+BOOK.COLUMN_ID+", "+BOOK.COLUMN_NOTIFICATION_TIME+" FROM "+BOOK.TABLE_NAME+" WHERE "+BOOK.COLUMN_NOTIFICATION_TIME+" IS NOT NULL",null);

    }

    public void getCountOfRecords()
    {
        Cursor c=db.query(BOOK.TABLE_NAME,new String[]{BOOK.COLUMN_ID},null,null,null,null,null);

         Log.e("Row_count",String.valueOf(c.getCount()));
    }


    public boolean updateBook(ContentValues values,String whereClause,String[] args)
    {
        boolean update=false;
        int updated_rows=db.update(BOOK.TABLE_NAME,values,whereClause,args);
        Toast.makeText(this.context,"Updated rows "+String.valueOf(updated_rows),Toast.LENGTH_SHORT).show();
        if(updated_rows!=0) return true;
        return update;

    }

    public boolean deleteBook(String whereClause,String[]args)
    {
        boolean update=false;
        int deleted_rows=db.delete(BOOK.TABLE_NAME,whereClause,args);
        Toast.makeText(this.context,"Deleted rows "+String.valueOf(deleted_rows),Toast.LENGTH_SHORT).show();
        if(deleted_rows!=0) return true;
        return update;
    }

    public boolean insertComment(String bookID,String comment)
    {
        ContentValues values=new ContentValues();
        values.put(DBContractClass.COMMENT.COLUMN_BOOK_ID,bookID);
        values.put(DBContractClass.COMMENT.COLUMN_COMMENT,comment);
        values.put(DBContractClass.COMMENT.COLUMN_DATE,CalendarHelper.getCurrentlyDateInString());
        long inserted=db.insert(DBContractClass.COMMENT.TABLE_NAME,null,values);

        return inserted!=0?true:false;
    }


    public Cursor getCommentsForBook(String bookID)
    {
        Cursor c;
        SQLiteQueryBuilder queryBuilder=new SQLiteQueryBuilder();
        queryBuilder.setTables(DBContractClass.COMMENT.TABLE_NAME+" join "+BOOK.TABLE_NAME+" on "+ DBContractClass.COMMENT.TABLE_NAME+"."+ DBContractClass.COMMENT.COLUMN_BOOK_ID+"="+BOOK.TABLE_NAME+"."+BOOK.COLUMN_ID);

        String selection= DBContractClass.COMMENT.COLUMN_BOOK_ID+"=?";
        String[] arguments={bookID};

        try{
            c=queryBuilder.query(db,new String[]{DBContractClass.COMMENT.TABLE_NAME+"."+ DBContractClass.COMMENT.COLUMN_ID, DBContractClass.COMMENT.COLUMN_COMMENT, DBContractClass.COMMENT.COLUMN_DATE},selection,arguments,null,null, DBContractClass.COMMENT.COLUMN_DATE);
        }
        catch (Exception e){
            Log.e("Comment retrieve","Something went wrong!");
            c=null;

        }
        return c;

    }

    public int getNumberOfReadPages(String bookID)
    {
        Cursor c=db.query(DBContractClass.BOOK.TABLE_NAME,new String[]{DBContractClass.BOOK.COLUMN_ID, DBContractClass.BOOK.COLUMN_NUMBER_OF_READ_PAGES}, DBContractClass.BOOK.COLUMN_ID+"=?",new String[]{bookID},null,null,null);
        c.moveToNext();
        return c.getInt(1);

    }

    public int getNumberOPages(String bookID)
    {
        Cursor c=db.query(DBContractClass.BOOK.TABLE_NAME,new String[]{DBContractClass.BOOK.COLUMN_ID, BOOK.COLUMN_NUMBER_OF_PAGES}, DBContractClass.BOOK.COLUMN_ID+"=?",new String[]{bookID},null,null,null);
        c.moveToNext();
        return c.getInt(1);


    }


}
