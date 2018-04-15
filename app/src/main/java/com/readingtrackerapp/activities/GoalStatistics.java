package com.readingtrackerapp.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.readingtrackerapp.R;
import com.readingtrackerapp.adapters.GoalStatisticAdapter;
import com.readingtrackerapp.database.DBHandler;

/**
 * Created by Anes on 4/15/2018.
 */

public class GoalStatistics extends AppCompatActivity {

    ListView listView;
    DBHandler dbHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.goals_statistics);

        dbHandler=new DBHandler(getApplicationContext());

        GoalStatisticAdapter adapter=new GoalStatisticAdapter(getApplicationContext(),dbHandler.getStatistics(),0);

        listView=(ListView)findViewById(R.id.listView2);

        listView.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        dbHandler.closeDB();
        super.onDestroy();
    }
}
