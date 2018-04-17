package com.readingtrackerapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.readingtrackerapp.fragments.BooksForReadingFragment;
import com.readingtrackerapp.fragments.CurrentlyReadingBooksFragment;
import com.readingtrackerapp.fragments.ReadBooksFragment;
import com.readingtrackerapp.helper.IRefreshable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anes on 3/24/2018.
 */

public class TabAdapter extends FragmentStatePagerAdapter {

    List<Fragment> fragments;
    List<String> titles;

    public TabAdapter(FragmentManager fm) {
        super(fm);

        // setting fragments
        fragments=new ArrayList<>();
        fragments.add(new CurrentlyReadingBooksFragment());
        fragments.add(new ReadBooksFragment());
        fragments.add(new BooksForReadingFragment());

        // setting titles
        titles=new ArrayList<>();
        titles.add("reading books");
        titles.add("read books");
        titles.add("books for reading");

    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    // needed for refreshing fragments in view pager
    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
