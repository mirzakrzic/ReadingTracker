package com.readingtrackerapp.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.readingtrackerapp.R;
import com.readingtrackerapp.activities.BookDetails;
import com.readingtrackerapp.adapters.ReadBooksListAdapter;
import com.readingtrackerapp.database.DBContractClass;
import com.readingtrackerapp.database.DBHandler;
import com.readingtrackerapp.helper.IRefreshable;

/**
 * Created by Anes on 3/24/2018.
 */

public class ReadBooksFragment extends Fragment implements IRefreshable {

    ListView listView;
    DBHandler dbHandler;
    ReadBooksListAdapter adapter;
    Menu sortingMenu;
    boolean ASCENDING_ORDER = true;
    String SORTING_COLUMN = DBContractClass.BOOK.COLUMN_TITLE;
    String SEARCH_TEXT="";
    String sortByTextForSnackBar="title";
    int selected_bookId;

    // *** SAME COMMENTS AS BooksForReadingFragment :)


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.read_books_fragment_layout, null, false);

        // db object for retrieving data: IMPORTANT close it in onDestroy()
        dbHandler = new DBHandler(getContext());

        listView = view.findViewById(R.id.listView);

        adapter = new ReadBooksListAdapter(getActivity().getApplicationContext(), dbHandler.getReadBooks(ASCENDING_ORDER, SORTING_COLUMN,SEARCH_TEXT), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        Log.e("Count_of_rows",String.valueOf(adapter.getCount()));
        listView.setAdapter(adapter);

        //If book is read you can check info only.
       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
               Cursor c = (Cursor) adapterView.getItemAtPosition(position);
               selected_bookId = c.getInt(c.getColumnIndex(DBContractClass.BOOK.COLUMN_ID));
               Intent intent=new Intent(getActivity(), BookDetails.class);
               intent.putExtra("BookID",String.valueOf(selected_bookId));
               startActivity(intent);

           }
       });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        sortingMenu = menu;

        sortingMenu.findItem(R.id.sortByRating).setVisible(true);
        sortingMenu.findItem(R.id.sortByReadPages).setVisible(false);


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
                    adapter.changeCursor(dbHandler.getReadBooks(ASCENDING_ORDER,SORTING_COLUMN,SEARCH_TEXT));

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
            case R.id.sortByRating: {
                SORTING_COLUMN = DBContractClass.BOOK.COLUMN_RATING;
                sortByTextForSnackBar = "rating";
            }
            break;

            default: return true;

        }

        // retrieving new db cursor to sort data
        adapter.changeCursor(dbHandler.getReadBooks(ASCENDING_ORDER, SORTING_COLUMN,SEARCH_TEXT));

        Snackbar.make(getActivity().findViewById(R.id.content),"Sorted by "+sortByTextForSnackBar+ (ASCENDING_ORDER?" ascending":" descending"),Snackbar.LENGTH_SHORT).show();

        return true;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHandler.closeDB();
    }

    @Override
    public void refresh() {
        adapter.changeCursor(dbHandler.getReadBooks(ASCENDING_ORDER, SORTING_COLUMN,SEARCH_TEXT));
        Log.d("refresh"," read books");

    }
}

