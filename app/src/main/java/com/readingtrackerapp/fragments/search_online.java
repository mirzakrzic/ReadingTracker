package com.readingtrackerapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.readingtrackerapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class search_online extends Fragment {


    public search_online() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_online, container, false);
    }

}
