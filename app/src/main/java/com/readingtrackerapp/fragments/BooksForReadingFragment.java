package com.readingtrackerapp.fragments;

import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.readingtrackerapp.R;
import com.readingtrackerapp.activities.AddNewBookActivity;
import com.readingtrackerapp.activities.BookDetails;
import com.readingtrackerapp.adapters.BooksForReadingListAdapter;
import com.readingtrackerapp.alarmManager.MyAlarmManager;
import com.readingtrackerapp.database.DBHandler;
import com.readingtrackerapp.database.DBContractClass.*;
import com.readingtrackerapp.helper.CalendarHelper;
import com.readingtrackerapp.helper.IRefreshable;
import com.readingtrackerapp.model.Book;

import org.w3c.dom.Text;

import java.util.Calendar;


public class BooksForReadingFragment extends Fragment implements IRefreshable {

    ListView listView;
    DBHandler dbHandler;
    BooksForReadingListAdapter adapter;
    Menu sortingMenu;
    boolean ASCENDING_ORDER = true;
    int selected_bookId;
    String SORTING_COLUMN = BOOK.COLUMN_TITLE;
    String SEARCH_TEXT = "";
    String sortByTextForSnackBar = "title";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set options menu available to fragment
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.books_for_reading_fragment_layout, null, false);

        // db object for retrieving data: IMPORTANT close it in onDestroy()
        dbHandler = new DBHandler(getContext());

        // setting list view
        listView = view.findViewById(R.id.listView);
        adapter = new BooksForReadingListAdapter(getActivity().getApplicationContext(), dbHandler.getBookForReading(ASCENDING_ORDER, SORTING_COLUMN, SEARCH_TEXT), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        Log.e("Count_of_rows", String.valueOf(adapter.getCount()));
        listView.setAdapter(adapter);
        registerForContextMenu(listView);//Adding context menu, need to inflate with onCreateContextMenu

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor c = (Cursor) adapterView.getItemAtPosition(position);
                selected_bookId = c.getInt(c.getColumnIndex(BOOK.COLUMN_ID));
                listView.showContextMenu();
                return true;
            }
        });

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu_books_for_reading, menu);
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
            case R.id.books_addToReading:
                addToReading();
                return true;
            case R.id.books_delete:
                deleteFromList();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void addToReading() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.move_book_to_reading))
                .setMessage(getString(R.string.add_to_reading))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(
                        android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                final String whereClause = BOOK.COLUMN_ID + "=?";
                                final String[] args = {String.valueOf(selected_bookId)};


                                final ContentValues values = new ContentValues();
                                values.put(BOOK.COLUMN_CURRENTLY_READING, 1);
                                values.put(BOOK.COLUMN_FOR_READING, 0);

                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Notifications?")
                                        .setMessage("Do you want to get reminders for reading for this book?")
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
                                                                values.put(BOOK.COLUMN_NOTIFICATION_TIME, CalendarHelper.getDateInString(calendar));
                                                                MyAlarmManager alarmManager = new MyAlarmManager(getContext());
                                                                alarmManager.setAlarmForBook(Integer.toString(selected_bookId), CalendarHelper.getDateInString(calendar));
                                                                boolean updated = dbHandler.updateBook(values, whereClause, args);

                                                                if (updated) {
                                                                    Log.e("Updated books ", String.valueOf(updated));
                                                                    adapter.changeCursor(dbHandler.getBookForReading(ASCENDING_ORDER, SORTING_COLUMN, SEARCH_TEXT));

                                                                } else {
                                                                    Log.e("Updated books ", "Book update failed");
                                                                }

                                                            }

                                                        }, hour, minute, true);//Yes 24 hour time
                                                        mTimePicker.setTitle("Select time for getting notifications");
                                                        mTimePicker.show();

                                                    }
                                                })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                boolean updated = dbHandler.updateBook(values, whereClause, args);

                                                if (updated) {
                                                    Log.e("Updated books ", String.valueOf(updated));
                                                    adapter.changeCursor(dbHandler.getBookForReading(ASCENDING_ORDER, SORTING_COLUMN, SEARCH_TEXT));

                                                } else {
                                                    Log.e("Updated books ", "Book update failed");
                                                }
                                            }
                                        }).show();
                            }
                        }).setNegativeButton(android.R.string.no, null).show();
    }


    public void deleteFromList() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.delete_book))
                .setMessage(getString(R.string.delete_book_text))
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String whereClause = BOOK.COLUMN_ID + "=?";
                        String[] args = {String.valueOf(selected_bookId)};


                        boolean deleted = dbHandler.deleteBook(whereClause, args);

                        if (deleted) {
                            adapter.changeCursor(dbHandler.getBookForReading(ASCENDING_ORDER, SORTING_COLUMN, SEARCH_TEXT));
                        } else {
                            Log.e("Delete books ", "Book delete failed");
                        }

                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // get sorting menu
        sortingMenu = menu;

        // set menu sort by rating and by read pages unvisible for this fragment
        sortingMenu.findItem(R.id.sortByRating).setVisible(false);
        sortingMenu.findItem(R.id.sortByReadPages).setVisible(false);

        // setting search action bar
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(sortingMenu.findItem(R.id.search));
        if (searchView != null) {

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    // on text change in search bar, requery db with that text and change cursor
                    SEARCH_TEXT = newText;
                    adapter.changeCursor(dbHandler.getBookForReading(ASCENDING_ORDER, SORTING_COLUMN, SEARCH_TEXT));

                    return false;
                }
            });
        }

        searchView.setQueryHint("book title");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.sortByAuthor: {
                SORTING_COLUMN = BOOK.COLUMN_AUTHOR_NAME;
                sortByTextForSnackBar = "author name";
            }
            break;
            case R.id.sortByGenre: {
                SORTING_COLUMN = GENRE.COLUMN_NAME;
                sortByTextForSnackBar = "genre";
            }
            break;
            case R.id.sortByPagesNumber: {
                SORTING_COLUMN = BOOK.COLUMN_NUMBER_OF_PAGES;
                sortByTextForSnackBar = "number of pages";
            }
            break;
            case R.id.sortByTitle: {
                SORTING_COLUMN = BOOK.COLUMN_TITLE;
                sortByTextForSnackBar = "title";
            }
            break;
            case R.id.order:
                ASCENDING_ORDER = !ASCENDING_ORDER;
                break;

            default:
                return true;

        }

        // retrieving new db cursor to sort data
        adapter.changeCursor(dbHandler.getBookForReading(ASCENDING_ORDER, SORTING_COLUMN, SEARCH_TEXT));

        Snackbar.make(getActivity().findViewById(R.id.content), "Sorted by " + sortByTextForSnackBar + (ASCENDING_ORDER ? " ascending" : " descending"), Snackbar.LENGTH_SHORT).show();

        return true;

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
        adapter.changeCursor(dbHandler.getBookForReading(ASCENDING_ORDER, SORTING_COLUMN, SEARCH_TEXT));
        Log.d("refresh", "for reading");
    }
}

