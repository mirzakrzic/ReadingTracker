package com.readingtrackerapp.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.readingtrackerapp.Apis.ApiUtil;
import com.readingtrackerapp.R;

import java.io.IOException;
import java.net.URL;


public class Search_online extends Fragment {


    public Search_online() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_search_online, container, false);

        try{
            URL bookURL= ApiUtil.buildUrl("cooking");
            new BooksAsyncTask().execute(bookURL);
        }
        catch (Exception e)
        {
            Log.e("Connection","Connection required");
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
            TextView response=(TextView) getActivity().findViewById(R.id.response);

            if(result==null)
                response.setText("Kralju nista to...");
            response.setText(result);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
    }

}
