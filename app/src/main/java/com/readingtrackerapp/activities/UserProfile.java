package com.readingtrackerapp.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.readingtrackerapp.R;
import com.readingtrackerapp.database.DBContractClass;
import com.readingtrackerapp.database.DBHandler;
import com.readingtrackerapp.model.User;

import org.w3c.dom.Text;

/**
 * Created by Anes on 4/15/2018.
 */

public class UserProfile extends AppCompatActivity {

    DBHandler dbHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile_layout);

        dbHandler=new DBHandler(getApplicationContext());

        Cursor user=dbHandler.getUser();
        user.moveToNext();

        ((TextView)findViewById(R.id.name)).setText(user.getString(user.getColumnIndex(DBContractClass.USER.COLUMN_NAME)));
        ((TextView)findViewById(R.id.surname)).setText(user.getString(user.getColumnIndex(DBContractClass.USER.COLUMN_SURNAME)));
        ((TextView)findViewById(R.id.regDate)).setText(user.getString(user.getColumnIndex(DBContractClass.USER.COLUMN_REGISTRATION_DATE)));
        ((TextView)findViewById(R.id.readPagesNum)).setText(user.getString(user.getColumnIndex(DBContractClass.USER.COLUMN_READ_PAGES_NUMBER)));
        ((TextView)findViewById(R.id.readTitlesNum)).setText(user.getString(user.getColumnIndex(DBContractClass.USER.COLUMN_READ_TITLES_NUMBER)));
        ((TextView)findViewById(R.id.name)).setText(user.getString(user.getColumnIndex(DBContractClass.USER.COLUMN_NAME)));

    }

    @Override
    protected void onDestroy() {
        dbHandler.closeDB();
        super.onDestroy();
    }
}
