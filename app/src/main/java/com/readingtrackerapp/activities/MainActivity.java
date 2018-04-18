package com.readingtrackerapp.activities;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.readingtrackerapp.R;
import com.readingtrackerapp.adapters.TabAdapter;
import com.readingtrackerapp.alarmManager.MyAlarmManager;
import com.readingtrackerapp.database.DBContractClass;
import com.readingtrackerapp.database.DBHandler;
import com.readingtrackerapp.helper.CalendarHelper;
import com.readingtrackerapp.helper.IRefreshable;
import com.readingtrackerapp.model.User;

import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;

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
        if(isUserRegistered())
            isMonhlyGoalSet();

        // set toolbar and options menu for sorting books
        setToolbar();

        // set view pager
        setViewPager();

        //initialize nav drawer and put username in header
        InitializeNavDrawer();

    }

    private void isMonhlyGoalSet() {

        if(dbHandler.isMonthlyGoalSetForThisMonth()) return;

        Intent intent=new Intent(MainActivity.this,RecordReading.class);
        intent.putExtra("goal","goal");
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setViewPager();
    }


    private void InitializeNavDrawer()
    {

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

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){

                    case R.id.nav_user:
                        startActivity(new Intent(getApplicationContext(),UserProfile.class));
                        return true;
                    case R.id.nav_user_stats:
                        startActivity(new Intent(getApplicationContext(),GoalStatistics.class));
                        return true;
                }
                return false;
            }
        });

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
    private boolean isUserRegistered() {

        if (dbHandler.isUserRegistered()) {

            Cursor user = dbHandler.getUser();

            user.moveToNext();
            Snackbar.make(findViewById(R.id.content), "Wellcome back " + user.getString(user.getColumnIndex(DBContractClass.USER.COLUMN_NAME)).toUpperCase() + " " + user.getString(user.getColumnIndex(DBContractClass.USER.COLUMN_SURNAME)).toUpperCase(), Snackbar.LENGTH_SHORT)
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                    .show();
            username_text=user.getString(user.getColumnIndex(DBContractClass.USER.COLUMN_NAME)).toUpperCase() + " " + user.getString(user.getColumnIndex(DBContractClass.USER.COLUMN_SURNAME)).toUpperCase();

            return true;

        } else {
            startActivity(new Intent(MainActivity.this, RegisterUser.class));
            return false;
        }
    }

    // setting view pager
    private void setViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        // setting view pager with fragments and titles to show on tabs
        final TabAdapter adapter = new TabAdapter(getSupportFragmentManager());


        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                              @Override
                                              public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                              }

                                              @Override
                                          public void onPageSelected(int position) {
                                                  IRefreshable f = (IRefreshable ) adapter.getItem(position);;
                                                  if (f != null) {
                                                      f.refresh();
                                                  }
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewBook(view);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu1) {

        // creating sorting menu
        getMenuInflater().inflate(R.menu.sorting_menu, menu1);

        return true;

    }


    // add new book
    public void AddNewBook(View view) {

        Intent intent=new Intent(this,AddNewBookActivity.class);
        startActivityForResult(intent,0);

    }

    // after adding new bok, refresh list views
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {
            viewPager.getAdapter().notifyDataSetChanged();
        }
        }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHandler.closeDB();
    }


}
