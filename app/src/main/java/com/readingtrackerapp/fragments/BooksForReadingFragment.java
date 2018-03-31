package com.readingtrackerapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.readingtrackerapp.R;
import com.readingtrackerapp.adapters.BooksForReadingListAdapter;
import com.readingtrackerapp.database.DBHandler;
import com.readingtrackerapp.database.DBContractClass.*;

/**
 * Created by Anes on 3/24/2018.
 */

public class BooksForReadingFragment extends Fragment {

    ListView listView;
    DBHandler dbHandler;
    BooksForReadingListAdapter adapter;
    Menu sortingMenu;
    boolean ASCENDING_ORDER = true;
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
        Log.e("Count_of_rows",String.valueOf(adapter.getCount()));
        listView.setAdapter(adapter);
        //registerForContextMenu(listView);//Adding context menu, need to inflate with onCreateContextMenu

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater=getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu,menu);
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
}

