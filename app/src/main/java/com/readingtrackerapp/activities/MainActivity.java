package com.readingtrackerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.readingtrackerapp.R;
import com.readingtrackerapp.adapters.TabAdapter;
import com.readingtrackerapp.database.DBHandler;
import com.readingtrackerapp.model.User;

public class MainActivity extends AppCompatActivity {

    DBHandler dbHandler;
    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    Menu sortingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dbHandler = new DBHandler(getApplicationContext());

        // checks if user is registered
        isUserRegistered();

        // set toolbar and options menu for sorting books
        setToolbar();

        // set view pager
        setViewPager();



    }


    // user registration
    private void isUserRegistered() {

        if (dbHandler.isUserRegistered()) {

            User user = dbHandler.getUser();

            Snackbar.make(findViewById(R.id.content), "Wellcome back " + user.getName().toUpperCase() + " " + user.getSurname().toUpperCase(), Snackbar.LENGTH_SHORT)
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                    .show();
        } else
            startActivity(new Intent(MainActivity.this, RegisterUser.class));

    }

    // setting view pager
    private void setViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        // setting view pager with fragments and titles to show on tabs
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        // setting listener to call method for showing and hiding sorting menu items on every tab scroll
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                // important to check !=null because view pager listener invoke setMenuItems method before onCreateOptionsMenu is called and menu then is null
            if(sortingMenu!=null)

                // set menu items depending on currently watching tab
                setMenuItems(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });


    }

    // setting toolbar for sorting books menu
    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    // showing & hiding sorting menu items depending on currently view pager tab
    private void setMenuItems(int position) {

        // sortByRating and sortByReadPages are menu items which will not be shown at sorting menu for every scrolling tab
        MenuItem sortByRating = sortingMenu.findItem(R.id.sortByRating);
        MenuItem sortByReadPages = sortingMenu.findItem(R.id.sortByReadPages);

        // position retreived from viewPager change listener
        switch (position) {

            // currently reading books tab, cannot sort by rating here
            case 0: {
                sortByReadPages.setVisible(true);

                sortByRating.setVisible(false);
            }
            break;

            // already read books tab, cannot sort by number of read pages here
            case 1: {
                sortByRating.setVisible(true);

                sortByReadPages.setVisible(false);
            }
            break;

            // books for reading tab, cannot sort by read pages and rating here
            case 2: {
                sortByRating.setVisible(false);

                sortByReadPages.setVisible(false);
            }
            break;

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu1) {

        // creating sorting menu
        getMenuInflater().inflate(R.menu.sorting_menu, menu1);
        sortingMenu = menu1;

        // seting sorting menu for currently watching view pager tab
        setMenuItems(viewPager.getCurrentItem());


        return true;

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHandler.closeDB();
    }
}
