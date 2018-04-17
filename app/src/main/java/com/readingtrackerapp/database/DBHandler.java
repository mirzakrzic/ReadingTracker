package com.readingtrackerapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import com.readingtrackerapp.database.DBContractClass.*;
import com.readingtrackerapp.model.*;
import com.readingtrackerapp.helper.CalendarHelper;

import java.util.Calendar;

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
        values.put(USER.COLUMN_READ_TITLES_NUMBER,0);

        long num=db.insert(USER.TABLE_NAME,null,values);

        return num!=0;

    }
    public Cursor getUser(){

        Cursor c=db.query(USER.TABLE_NAME,new String[]{USER.COLUMN_ID, USER.COLUMN_NAME, USER.COLUMN_SURNAME, USER.COLUMN_REGISTRATION_DATE, USER.COLUMN_READ_PAGES_NUMBER,USER.COLUMN_READ_TITLES_NUMBER},null,null,null,null,null);

        return c;
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
    public void addreadPages(int readPagesNumber, boolean bookFinished){

        Cursor c=db.query(USER.TABLE_NAME,new String[]{USER.COLUMN_READ_TITLES_NUMBER, USER.COLUMN_READ_PAGES_NUMBER},null,null,null,null,null);

        c.moveToNext();

        int readPages=c.getInt(1);
        readPages+=readPagesNumber;

        ContentValues values=new ContentValues();
        if(bookFinished){
            int numberOfReadTitles=c.getInt(0);
            numberOfReadTitles++;
            values.put(USER.COLUMN_READ_TITLES_NUMBER,numberOfReadTitles);
        }
        values.put(USER.COLUMN_READ_PAGES_NUMBER,readPages);


        db.update(USER.TABLE_NAME,values,null,null);
    }
    public Cursor getStatistics(){

    String query="select monthlyGoals._id,monthlyGoals.year,monthlyGoals.month, monthlyGoals.numberOfPages,ifnull(sum(readingEvidentions.readPagesNumber),0) as 'sum'" +
            "from monthlyGoals left join readingEvidentions on monthlyGoals.year=readingEvidentions.year AND monthlyGoals.month=readingEvidentions.month " +
            "group by monthlyGoals._id,monthlyGoals.year,monthlyGoals.month, monthlyGoals.numberOfPages " +
            "order by monthlyGoals.year DESC, monthlyGoals.month DESC";

    Cursor cursor=db.rawQuery(query,null);


    return cursor;


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

       /* List<Genre> genres=new ArrayList<>();

        while (c.moveToNext())
            genres.add(new Genre(c.getInt(0),c.getString(1)));

        return genres;*/


    }


    public void insertMonthlyGoal(String pagesNum){

        ContentValues values=new ContentValues();
        values.put(MONTHLY_GOAL.COLUMN_NUMBER_OF_PAGES,pagesNum);
        values.put(MONTHLY_GOAL.COLUMN_YEAR,Calendar.getInstance().get(Calendar.YEAR));
        values.put(MONTHLY_GOAL.COLUMN_MONTH,Calendar.getInstance().get(Calendar.MONTH));

        db.insert(MONTHLY_GOAL.TABLE_NAME,null,values);

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

    public void evidentateReading(String bookID, int pagesNum){

        ContentValues values=new ContentValues();
        values.put(READING_EVIDENTION.COLUMN_BOOK_ID,bookID);
        values.put(READING_EVIDENTION.COLUMN_NUMBER_OF_READ_PAGES,pagesNum);
        values.put(READING_EVIDENTION.COLUMN_YEAR, Calendar.getInstance().get(Calendar.YEAR));
        values.put(READING_EVIDENTION.COLUMN_MONTH, Calendar.getInstance().get(Calendar.MONTH));
        values.put(READING_EVIDENTION.COLUMN_DAY, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        db.insert(READING_EVIDENTION.TABLE_NAME,null,values);

    }

    public boolean hasAlatmSet(String bookID){
        Cursor cursor=db.query(BOOK.TABLE_NAME,new String[]{BOOK.COLUMN_NOTIFICATION_TIME},BOOK.COLUMN_ID+"=?",new String[]{bookID},null,null,null);
        cursor.moveToNext();

        return !cursor.isNull(0);

    }

    public void setTimeForNotif(String bookID,String newTime){

        ContentValues values=new ContentValues();
        values.put(BOOK.COLUMN_NOTIFICATION_TIME,newTime);
        db.update(BOOK.TABLE_NAME,values,BOOK.COLUMN_ID+"=?",new String[]{bookID});

    }

    public boolean isBookReadForToday(String bookID, String year, String month, String day) {

        Cursor cursor=db.query(READING_EVIDENTION.TABLE_NAME,new String []{READING_EVIDENTION._ID},READING_EVIDENTION.COLUMN_BOOK_ID+"=? AND "+READING_EVIDENTION.COLUMN_YEAR+"=? AND "+READING_EVIDENTION.COLUMN_MONTH+"=? AND "+READING_EVIDENTION.COLUMN_DAY+"=?",new String[]{bookID,year,month,day},null,null,null);

        if (cursor.getCount() ==0){
            //cursor is empty
            Log.d("citanje","nema citanja na datum: "+year+"."+month+"."+day+" za knjigu: "+bookID);
            return false;
        }
        else{
            Log.d("citanje","citanje se desilo za knjigu: "+bookID);
            return true;
        }
    }

    public boolean isMonthlyGoalSetForThisMonth() {

    Cursor c=db.query(MONTHLY_GOAL.TABLE_NAME,new String[]{MONTHLY_GOAL.COLUMN_ID},MONTHLY_GOAL.COLUMN_YEAR+"=? AND "+MONTHLY_GOAL.COLUMN_MONTH+"=?",new String[]{Integer.toString(Calendar.getInstance().get(Calendar.YEAR)),Integer.toString(Calendar.getInstance().get(Calendar.MONTH))},null,null,null);

    return c.getCount()>0;

    }
}




