package com.readingtrackerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.readingtrackerapp.R;
import com.readingtrackerapp.adapters.TabAdapter;
import com.readingtrackerapp.database.DBHandler;
import com.readingtrackerapp.model.User;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    DBHandler dbHandler;
    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    TextView username;
    NavigationView mNavigationView;
    View mHeaderView;
    String username_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandler = new DBHandler(getApplicationContext());

        // checks if user is registered
        isUserRegistered();

//        // set toolbar and options menu for sorting books
//        setToolbar();
//
//        // set view pager
//        setViewPager();

        //initialize nav drawer and put username in header
        InitializeNavDrawer();


    }

    private void InitializeNavDrawer()
    {
        setToolbar();
        setViewPager();
        // NavigationView
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        // NavigationView Header
        mHeaderView =  mNavigationView.getHeaderView(0);
        username=(TextView) mHeaderView.findViewById(R.id.user_name);
        username.setText(username_text);

        drawerLayout=(DrawerLayout) findViewById(R.id.content);
        toggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // user registration
    private void isUserRegistered() {

        if (dbHandler.isUserRegistered()) {

            User user = dbHandler.getUser();

            Snackbar.make(findViewById(R.id.content), "Wellcome back " + user.getName().toUpperCase() + " " + user.getSurname().toUpperCase(), Snackbar.LENGTH_SHORT)
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                    .show();
            username_text=user.getName().toUpperCase() + " " + user.getSurname().toUpperCase();

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

    }

    // setting toolbar for sorting books menu
    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu1) {

        // creating sorting menu
        getMenuInflater().inflate(R.menu.sorting_menu, menu1);

        return true;

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHandler.closeDB();
    }
}
