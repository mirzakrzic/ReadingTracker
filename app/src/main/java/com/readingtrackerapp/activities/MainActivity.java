package com.readingtrackerapp.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.readingtrackerapp.R;
import com.readingtrackerapp.adapters.TabAdapter;
import com.readingtrackerapp.database.DBHandler;
import com.readingtrackerapp.model.User;

public class MainActivity extends AppCompatActivity {

    DBHandler dbHandler;
    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        dbHandler=new DBHandler(getApplicationContext());

        // checks if user is registered
        isUserRegistered();

        // set view pager
        setViewPager();

    }

    // user registration
    private void isUserRegistered() {

        if(dbHandler.isUserRegistered()){

            User user=dbHandler.getUser();

            Snackbar.make(findViewById(R.id.content),"Wellcome back "+user.getName().toUpperCase()+" "+user.getSurname().toUpperCase() , Snackbar.LENGTH_SHORT)
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                    .show();
        }
        else
            startActivity(new Intent(MainActivity.this,RegisterUser.class));

    }

    private void setViewPager() {
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        tabLayout=(TabLayout)findViewById(R.id.tabLayout);

        TabAdapter adapter=new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHandler.closeDB();
    }
}
