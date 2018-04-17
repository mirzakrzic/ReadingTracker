package com.readingtrackerapp.fragments;

import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.readingtrackerapp.R;
import com.readingtrackerapp.activities.BookDetails;
import com.readingtrackerapp.activities.RecordReading;
import com.readingtrackerapp.adapters.CurrentlyReadingBooksListAdapter;
import com.readingtrackerapp.alarmManager.MyAlarmManager;
import com.readingtrackerapp.database.DBContractClass;
import com.readingtrackerapp.database.DBHandler;
import com.readingtrackerapp.helper.CalendarHelper;
import com.readingtrackerapp.helper.IRefreshable;

import java.util.Calendar;

/**
 * Created by Anes on 3/24/2018.
 */

public class CurrentlyReadingBooksFragment extends Fragment implements IRefreshable {

    ListView listView;
    DBHandler dbHandler;
    CurrentlyReadingBooksListAdapter adapter;
    Menu sortingMenu;
    int selected_bookId;
    boolean has_alarm_setup = false;
    boolean ASCENDING_ORDER = true;
    String SORTING_COLUMN = DBContractClass.BOOK.COLUMN_TITLE;
    String SEARCH_TEXT = "";
    String sortByTextForSnackBar = "title";

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

        adapter = new CurrentlyReadingBooksListAdapter(getActivity().getApplicationContext(), dbHandler.getCurrentlyReadingBooks(ASCENDING_ORDER, SORTING_COLUMN, SEARCH_TEXT), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        Log.e("Count_of_rows", String.valueOf(adapter.getCount()));
        listView.setAdapter(adapter);
        registerForContextMenu(listView);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor c = (Cursor) adapterView.getItemAtPosition(position);
                selected_bookId = c.getInt(c.getColumnIndex(DBContractClass.BOOK.COLUMN_ID));
                if (c.getString(c.getColumnIndex(DBContractClass.BOOK.COLUMN_NOTIFICATION_TIME)) != null) {
                    has_alarm_setup = true;//* check if the notification time is set-up?
                }
                listView.showContextMenu();
                Toast.makeText(getActivity().getBaseContext(), "SELECTED BOOK_ID: " + String.valueOf(selected_bookId), Toast.LENGTH_SHORT).show();
                return true;
            }
        });


        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu_book_on_reading, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.books_details:
                Intent intent = new Intent(getActivity(), BookDetails.class);
                intent.putExtra("BookID", String.valueOf(selected_bookId));
                startActivity(intent);
                getActivity().finish();
                return true;
            case R.id.books_readingTrack:
                addReadPages();
                return true;
            case R.id.books_setNotifTime:
                setNotificationTime();
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

    private void setNotificationTime() {

        new AlertDialog.Builder(getActivity())
                .setTitle("Notifications?")
                .setMessage("Do you really want to set new time for ntoifications for this book?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(
                        android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                final Calendar calendar = Calendar.getInstance();

                                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                int minute = calendar.get(Calendar.MINUTE);

                                TimePickerDialog mTimePicker;
                                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                                        calendar.set(Calendar.MINUTE, selectedMinute);
                                        MyAlarmManager alarmManager = new MyAlarmManager(getContext());
                                        alarmManager.setAlarmForBook(Integer.toString(selected_bookId), CalendarHelper.getDateInString(calendar));

                                        dbHandler.setTimeForNotif(Integer.toString(selected_bookId),CalendarHelper.getDateInString(calendar));

                                    }

                                }, hour, minute, true);//Yes 24 hour time
                                mTimePicker.setTitle("Select time for getting notifications");
                                mTimePicker.show();}})
                .setNegativeButton(android.R.string.no,null).show();

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

                    SEARCH_TEXT = newText;
                    adapter.changeCursor(dbHandler.getCurrentlyReadingBooks(ASCENDING_ORDER, SORTING_COLUMN, SEARCH_TEXT));

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

            default:
                return true;
        }

        // retrieving new db cursor to sort data
        adapter.changeCursor(dbHandler.getCurrentlyReadingBooks(ASCENDING_ORDER, SORTING_COLUMN, SEARCH_TEXT));

        Snackbar.make(getActivity().findViewById(R.id.content), "Sorted by " + sortByTextForSnackBar + (ASCENDING_ORDER ? " ascending" : " descending"), Snackbar.LENGTH_SHORT).show();

        return true;

    }

    public void deleteFromList() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.delete_book))
                .setMessage(getString(R.string.delete_book_text))
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String whereClause = DBContractClass.BOOK.COLUMN_ID + "=?";
                        String[] args = {String.valueOf(selected_bookId)};

                        if (has_alarm_setup) {
                            MyAlarmManager myAlarmManager = new MyAlarmManager(getContext());
                            myAlarmManager.stopAlarmForBook(String.valueOf(selected_bookId));
                            Log.e("Alarm", "Alarm turned of for book: " + String.valueOf(selected_bookId));
                        }

                        boolean deleted = dbHandler.deleteBook(whereClause, args);

                        if (deleted) {
                            adapter.changeCursor(dbHandler.getCurrentlyReadingBooks(ASCENDING_ORDER, SORTING_COLUMN, SEARCH_TEXT));
                        } else {
                            Log.e("Delete books ", "Book delete failed");
                        }

                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }


    public void addComment() {
        //creating alert dialog so that you can add comment on a go
        Boolean saved = false;
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.add_comment_layout, null);
        final EditText comment = (EditText) view.findViewById(R.id.input_comment);
        Button btnSave = (Button) view.findViewById(R.id.save_comment_btn);

        alert_builder.setView(view);
        final AlertDialog dialog = alert_builder.create();
        dialog.show();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment_text = comment.getText().toString();

                if (comment_text.isEmpty()) {
                    Toast.makeText(getActivity().getBaseContext(), "Comment can't be empty.", Toast.LENGTH_SHORT).show();
                } else {
                    if (dbHandler.insertComment(String.valueOf(selected_bookId), comment_text)) {
                        Toast.makeText(getActivity().getBaseContext(), "Comment sucesfully added", Toast.LENGTH_SHORT).show();
                        dialog.hide();

                    }
                }

            }
        });
    }


    public void addReadPages() {

        Intent intent=new Intent(getContext(), RecordReading.class);
        intent.putExtra("bookID",selected_bookId);
        startActivity(intent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHandler.closeDB();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void refresh() {
        adapter.changeCursor(dbHandler.getCurrentlyReadingBooks(ASCENDING_ORDER, SORTING_COLUMN, SEARCH_TEXT));
        Log.d("refresh","curr reading");

    }
}
