package com.readingtrackerapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.readingtrackerapp.R;
import com.readingtrackerapp.adapters.BookListAdapter;
import com.readingtrackerapp.database.DBHandler;

/**
 * Created by Anes on 3/24/2018.
 */

public class CurrentlyReadingBooksFragment extends Fragment {

    ListView listView;
    DBHandler dbHandler;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.currently_reading_books_fragment_layout,null,false);

        dbHandler=new DBHandler(getContext());

        listView=view.findViewById(R.id.listView);

        BookListAdapter adapter=new BookListAdapter(getActivity().getApplicationContext(),dbHandler.getCurrentlyReadingBooks(), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(adapter);

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHandler.closeDB();
    }
}
