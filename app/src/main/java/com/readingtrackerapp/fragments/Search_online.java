package com.readingtrackerapp.fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.readingtrackerapp.Apis.ApiUtil;
import com.readingtrackerapp.R;
import com.readingtrackerapp.adapters.BooksAdapter;
import com.readingtrackerapp.helper.IRefreshable;
import com.readingtrackerapp.model.Book;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static android.view.View.VISIBLE;


public class Search_online extends Fragment implements SearchView.OnQueryTextListener,IRefreshable{


    public Search_online() {
        // Required empty public constructor
    }

    private ProgressBar mLoadingProgress;
    private RecyclerView rvBooks;
    Menu sortingMenu;
    URL bookURL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_search_online, container, false);
        setHasOptionsMenu(true);

        mLoadingProgress=(ProgressBar) view.findViewById(R.id.pb_loading);
        rvBooks=(RecyclerView) view.findViewById(R.id.rv_books);

        try{
            bookURL= ApiUtil.buildUrl("Programming");
            new BooksAsyncTask().execute(bookURL);
        }
        catch (Exception e)
        {
            Log.e("Connection","Connection required");
        }

        //create the layoutManager for the books (linear in this case, scrolling vertically
        LinearLayoutManager booksLayoutManager =
                new LinearLayoutManager(getActivity().getBaseContext(), LinearLayoutManager.VERTICAL, false);
        rvBooks.setLayoutManager(booksLayoutManager);

        return view;
    }

//    @Override
////    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
////        super.onCreateOptionsMenu(menu, inflater);
////
////        // get sorting menu
////        sortingMenu = menu;
////
////        // set menu sort by rating and by read pages unvisible for this fragment
////        sortingMenu.findItem(R.id.sortByRating).setVisible(false);
////        sortingMenu.findItem(R.id.sortByReadPages).setVisible(false);
////
////
////        // setting search action bar
////        SearchView searchView = (SearchView) MenuItemCompat.getActionView(sortingMenu.findItem(R.id.search));
////        if (searchView != null) {
////
////            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
////                @Override
////                public boolean onQueryTextSubmit(String query) {
////                    try{
////                        URL url=ApiUtil.buildUrl(query);
////                        new BooksAsyncTask().execute(url);
////
////                    }catch (Exception e)
////                    {
////                        Log.e("Error",e.toString());
////                    }
////
////                    return false;
////                }
////
////                @Override
////                public boolean onQueryTextChange(String newText) {
////                    return false;
////                }
////            });
////        }
////
////        searchView.setQueryHint("book title");
////
////    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        sortingMenu=menu;
        sortingMenu.findItem(R.id.sortByRating).setVisible(false);
        sortingMenu.findItem(R.id.sortByReadPages).setVisible(false);

        // setting search action bar
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(sortingMenu.findItem(R.id.search));
        if (searchView != null) {

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    try{

                        URL url=ApiUtil.buildUrl(query);
                        Toast.makeText(getContext(),"Searching for "+query,Toast.LENGTH_LONG).show();
                        new BooksAsyncTask().execute(url);

                    }catch (Exception e)
                    {
                        Log.e("Error",e.toString());
                    }


                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) { return false; }
            });
        }

        searchView.setQueryHint("book title");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void goToSettings(View view){
        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
    }

    public void retrySearch(View view){
        bookURL= ApiUtil.buildUrl("Programming");
        new BooksAsyncTask().execute(bookURL);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        URL url=ApiUtil.buildUrl(query);
        Toast.makeText(getContext(),"Searching for "+query,Toast.LENGTH_LONG).show();
        new BooksAsyncTask().execute(url);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void refresh() {

    }


    public class BooksAsyncTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String result=null;
            try{
                result=ApiUtil.getJSON(searchUrl);

            }catch (Exception e)
            {
                Log.e("Error",e.toString());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            TextView error=(TextView) getActivity().findViewById(R.id.tv_error);
            final TextView goToSettings=(TextView) getActivity().findViewById(R.id.tv_settings);
            Button retryBtn=(Button) getActivity().findViewById(R.id.btn_retry);

            mLoadingProgress.setVisibility(View.INVISIBLE);

            if(result==null) {
                error.setVisibility(VISIBLE);
                goToSettings.setVisibility(VISIBLE);
                goToSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToSettings(view);
                    }
                });

                retryBtn.setVisibility(VISIBLE);
                retryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        retrySearch(view);
                    }
                });
            }
            else{

                error.setVisibility(View.INVISIBLE);
                goToSettings.setVisibility(View.INVISIBLE);
                retryBtn.setVisibility(View.INVISIBLE);

            }

            ArrayList<Book> books=ApiUtil.getBooksFromJSON(result);
            String resultString="";

            BooksAdapter adapter = new BooksAdapter(books);
            rvBooks.setAdapter(adapter);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgress.setVisibility(VISIBLE);

        }
    }

}
