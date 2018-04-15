package com.readingtrackerapp.fragments;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.readingtrackerapp.R;
import com.readingtrackerapp.activities.BookDetails;
import com.readingtrackerapp.adapters.CurrentlyReadingBooksListAdapter;
import com.readingtrackerapp.alarmManager.MyAlarmManager;
import com.readingtrackerapp.database.DBContractClass;
import com.readingtrackerapp.database.DBHandler;

/**
 * Created by Anes on 3/24/2018.
 */

public class CurrentlyReadingBooksFragment extends Fragment {

    ListView listView;
    DBHandler dbHandler;
    CurrentlyReadingBooksListAdapter adapter;
    Menu sortingMenu;
    int selected_bookId;
    boolean has_alarm_setup=false;
    boolean ASCENDING_ORDER = true;
    String SORTING_COLUMN = DBContractClass.BOOK.COLUMN_TITLE;
    String SEARCH_TEXT="";
    String sortByTextForSnackBar="title";

    // *** SAME COMMENTS AS BooksForReadingFragment :)

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.currently_reading_books_fragment_layout, null, false);

        // db class for retreiving data, IMPORTANT:onDestroy() closing
        dbHandler = new DBHandler(getContext());

        listView = view.findViewById(R.id.listView);

        adapter = new CurrentlyReadingBooksListAdapter(getActivity().getApplicationContext(), dbHandler.getCurrentlyReadingBooks(ASCENDING_ORDER, SORTING_COLUMN,SEARCH_TEXT), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        Log.e("Count_of_rows",String.valueOf(adapter.getCount()));
        listView.setAdapter(adapter);
        registerForContextMenu(listView);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor c = (Cursor) adapterView.getItemAtPosition(position);
                selected_bookId=c.getInt(c.getColumnIndex(DBContractClass.BOOK.COLUMN_ID));
                if(c.getString(c.getColumnIndex(DBContractClass.BOOK.COLUMN_NOTIFICATION_TIME))!=null)
                {
                    has_alarm_setup=true;//* check if the notification time is set-up?
                }
                listView.showContextMenu();
                Toast.makeText(getActivity().getBaseContext(),"SELECTED BOOK_ID: "+String.valueOf(selected_bookId),Toast.LENGTH_SHORT).show();
                return true;
            }
        });


        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater=getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu_book_on_reading,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.books_details:
                Intent intent=new Intent(getActivity(), BookDetails.class);
                intent.putExtra("BookID",String.valueOf(selected_bookId));
                startActivity(intent);
                return true;
            case R.id.books_readingTrack:
                addReadPages();
                return true;

            case R.id.books_comment:
                addComment();
                return true;
            case R.id.books_delete:
                deleteFromList();
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        sortingMenu = menu;

        sortingMenu.findItem(R.id.sortByRating).setVisible(false);
        sortingMenu.findItem(R.id.sortByReadPages).setVisible(true);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(sortingMenu.findItem(R.id.search));
        if (searchView != null) {

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    SEARCH_TEXT=newText;
                    adapter.changeCursor(dbHandler.getCurrentlyReadingBooks(ASCENDING_ORDER,SORTING_COLUMN,SEARCH_TEXT));

                    return false;
                }
            });

            searchView.setQueryHint("book title");

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.sortByAuthor: {
                SORTING_COLUMN = DBContractClass.BOOK.COLUMN_AUTHOR_NAME;
                sortByTextForSnackBar = "author name";
            }
            break;
            case R.id.sortByGenre: {
                SORTING_COLUMN = DBContractClass.GENRE.COLUMN_NAME;
                sortByTextForSnackBar = "genre";
            }
            break;
            case R.id.sortByPagesNumber: {
                SORTING_COLUMN = DBContractClass.BOOK.COLUMN_NUMBER_OF_PAGES;
                sortByTextForSnackBar = "number of pages";
            }
            break;
            case R.id.sortByTitle: {
                SORTING_COLUMN = DBContractClass.BOOK.COLUMN_TITLE;
                sortByTextForSnackBar = "title";
            }
            break;
            case R.id.order:
                ASCENDING_ORDER = !ASCENDING_ORDER;
                break;
            case R.id.sortByReadPages: {
                SORTING_COLUMN = "PERCENTAGE";
                sortByTextForSnackBar = "percent of read pages";
            }
            break;

            default: return true;
        }

        // retrieving new db cursor to sort data
        adapter.changeCursor(dbHandler.getCurrentlyReadingBooks(ASCENDING_ORDER, SORTING_COLUMN,SEARCH_TEXT));

        Snackbar.make(getActivity().findViewById(R.id.content), "Sorted by " + sortByTextForSnackBar + (ASCENDING_ORDER ? " ascending" : " descending"), Snackbar.LENGTH_SHORT).show();

        return true;

    }

    public void deleteFromList(){
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.delete_book))
                .setMessage(getString(R.string.delete_book_text))
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String whereClause= DBContractClass.BOOK.COLUMN_ID+"=?";
                        String[] args={String.valueOf(selected_bookId)};

                        if(has_alarm_setup){
                            MyAlarmManager myAlarmManager=new MyAlarmManager(getContext());
                            myAlarmManager.stopAlarmForBook(String.valueOf(selected_bookId));
                            Log.e("Alarm","Alarm turned of for book: "+String.valueOf(selected_bookId));
                        }

                        boolean deleted=dbHandler.deleteBook(whereClause,args);

                        if (deleted)
                        {
                            adapter.changeCursor(dbHandler.getCurrentlyReadingBooks(ASCENDING_ORDER, SORTING_COLUMN, SEARCH_TEXT));
                        }
                        else
                        {
                            Log.e("Delete books ","Book delete failed");
                        }

                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }


    public void addComment(){
        //creating alert dialog so that you can add comment on a go
        Boolean saved=false;
        AlertDialog.Builder alert_builder=new AlertDialog.Builder(getActivity());
        View view= getLayoutInflater().inflate(R.layout.add_comment_layout,null);
        final EditText comment=(EditText) view.findViewById(R.id.input_comment);
        Button btnSave=(Button) view.findViewById(R.id.save_comment_btn);

        alert_builder.setView(view);
        final AlertDialog dialog=alert_builder.create();
        dialog.show();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment_text = comment.getText().toString();

                if (comment_text.isEmpty())
                {
                    Toast.makeText(getActivity().getBaseContext(),"Comment can't be empty.",Toast.LENGTH_SHORT).show();
                }
                else{
                    if (dbHandler.insertComment(String.valueOf(selected_bookId),comment_text)){
                        Toast.makeText(getActivity().getBaseContext(),"Comment sucesfully added",Toast.LENGTH_SHORT).show();
                        dialog.hide();

                    }
                }

            }
        });
    }


    public void addReadPages(){
        //setting up alert dialog which is gonna serve to get number of read pages
        AlertDialog.Builder alert_builder=new AlertDialog.Builder(getActivity());
        View view= getLayoutInflater().inflate(R.layout.add_read_pages_layout,null);
        final EditText readPages=(EditText) view.findViewById(R.id.input_read_pages);
        Button btnSave=(Button) view.findViewById(R.id.save_numReadPages_btn);

        alert_builder.setView(view);
        final AlertDialog dialog=alert_builder.create();
        dialog.show();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int currently_read_pages = dbHandler.getNumberOfReadPages(String.valueOf(selected_bookId));
                final int total_num_of_pages = dbHandler.getNumberOPages(String.valueOf(selected_bookId));
                String input_readPages_check = readPages.getText().toString();//need to check if user entered anything
                int input_readPages=0;

                final int reading_evidention_read =dbHandler.getNumberOfReadPages_EvidentionTable(String.valueOf(selected_bookId));

                if(input_readPages_check.isEmpty()){ readPages.setError("This field can't be empty!");return; }
                else{ input_readPages=Integer.parseInt(input_readPages_check); }

                if(input_readPages>total_num_of_pages) { readPages.setError("You can't read more pages than books has!");return; }

                if(input_readPages<currently_read_pages){ readPages.setError("Input value lower than last time recorded");return;}

                if (input_readPages==total_num_of_pages) {

                    final ContentValues contentValues = new ContentValues();
                    contentValues.put(DBContractClass.BOOK.COLUMN_FOR_READING, "0");
                    contentValues.put(DBContractClass.BOOK.COLUMN_CURRENTLY_READING, "0");
                    contentValues.put(DBContractClass.BOOK.COLUMN_ALREADY_READ, "1");
                    contentValues.put(DBContractClass.BOOK.COLUMN_NUMBER_OF_READ_PAGES,total_num_of_pages);

                    //in case that user entered bigger number of pages, we are setting up new alert to ask does he rlly want to save it

                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.book_read))
                            .setMessage(getString(R.string.congrats))
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, int whichButton) {
                                    String whereClause = DBContractClass.BOOK.COLUMN_ID + "=?";
                                    String[] args = {String.valueOf(selected_bookId)};
                                    boolean updated = dbHandler.updateBook(contentValues, whereClause, args);
                                    int inserted = dbHandler.recordReading(String.valueOf(selected_bookId),total_num_of_pages-reading_evidention_read); // if

                                    if (updated) {
                                        //remove alarm
                                        if(has_alarm_setup){
                                            MyAlarmManager myAlarmManager=new MyAlarmManager(getContext());
                                            myAlarmManager.stopAlarmForBook(String.valueOf(selected_bookId));
                                            Log.e("Alarm","Alarm turned of for book: "+String.valueOf(selected_bookId));
                                        }

                                    //if everything is alright, rate the book
                                        AlertDialog.Builder alert_builder_rating=new AlertDialog.Builder(getActivity());
                                        View view= getLayoutInflater().inflate(R.layout.rate_the_book,null);
                                        RatingBar ratingBar=(RatingBar) view.findViewById(R.id.input_rate);

                                        alert_builder_rating.setView(view);
                                        final AlertDialog dialog1=alert_builder_rating.create();
                                        dialog1.show();

                                        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                            @Override
                                            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                                                final int rating= (int)ratingBar.getRating() ;

                                                final ContentValues contentValues_rating = new ContentValues();
                                                contentValues_rating.put(DBContractClass.BOOK.COLUMN_RATING,rating);

                                                if(dbHandler.updateBook(contentValues_rating, DBContractClass.BOOK.COLUMN_ID+"=?",new String[]{String.valueOf(selected_bookId)}))
                                                {
                                                    dialog1.hide();

                                                    Intent intent=new Intent(getActivity(), BookDetails.class);
                                                    intent.putExtra("BookID",String.valueOf(selected_bookId));
                                                    startActivity(intent);
                                                }

                                            }
                                        });

                                        adapter.changeCursor(dbHandler.getCurrentlyReadingBooks(ASCENDING_ORDER, SORTING_COLUMN, SEARCH_TEXT));
                                    } else {
                                        Log.e("Book_read", "Book moved to read - FALSE");
                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                    dialog.hide();
                } else {

                    final ContentValues contentValues = new ContentValues();
                    contentValues.put(DBContractClass.BOOK.COLUMN_NUMBER_OF_READ_PAGES,input_readPages);
                    String whereClause = DBContractClass.BOOK.COLUMN_ID + "=?";
                    String[] args = {String.valueOf(selected_bookId)};
                    //if book is not read totally just update number of read pages.
                    boolean updated = dbHandler.updateBook(contentValues,whereClause,args);


                    int reading_evidention_insert=input_readPages-reading_evidention_read; //get difference between current page and read so far

                    int inserted = dbHandler.recordReading(String.valueOf(selected_bookId),reading_evidention_insert);

                    if(updated && inserted!=0)
                    {
                        adapter.changeCursor(dbHandler.getCurrentlyReadingBooks(ASCENDING_ORDER, SORTING_COLUMN, SEARCH_TEXT));
                        Toast.makeText(getActivity().getBaseContext(),"Added read pages",Toast.LENGTH_SHORT).show();
                        dialog.hide();
                    }
                    else{
                        Log.e("Book_read", "Book moved to read - FALSE");
                    }
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHandler.closeDB();
    }
}
