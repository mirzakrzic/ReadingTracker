package com.readingtrackerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.readingtrackerapp.R;
import com.readingtrackerapp.database.DBHandler;
import com.readingtrackerapp.helper.CalendarHelper;

/**
 * Created by Anes on 3/24/2018.
 */

public class RegisterUser extends AppCompatActivity {

    TextInputLayout nameLayout,surnameLayout;
    EditText nameEditText, surnameEditText;
    DBHandler dbHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user_layout);

        dbHandler=new DBHandler(getApplicationContext());

        setViews();

    }

    private void setViews(){
        nameLayout=(TextInputLayout)findViewById(R.id.nameLayout);
        surnameLayout=(TextInputLayout)findViewById(R.id.surnameLayout);

        nameEditText=(EditText)findViewById(R.id.nameEditText);
        surnameEditText=(EditText)findViewById(R.id.surnameEditText);
    }

    public void register(View view) {

        // if input is invalid return
        if(!validateInput())
            return;


        // register user in database
        boolean register=dbHandler.registerUser(nameEditText.getText().toString(),surnameEditText.getText().toString(), CalendarHelper.getCurrentlyDateInString());


        // close activity
        Intent intent=new Intent(RegisterUser.this,RecordReading.class);
        intent.putExtra("goal","goal");
        startActivity(intent);
        finish();

    }

    private boolean validateInput() {

        boolean valid=true;

        // checking if name consists only of letters, setting error
        if(!(nameEditText.getText().toString().matches("[a-zA-Z]+"))){
            nameLayout.setError("Name should consists only of letters!");
            valid=false;
        }
        else
            nameLayout.setErrorEnabled(false);


        // checking if surname consists only of letters, setting error
        if(!(surnameEditText.getText().toString().matches("[a-zA-Z]+"))){
            surnameLayout.setError("Surname should consists only of letters!");
            valid=false;
        }
        else
            surnameLayout.setErrorEnabled(false);

        return valid;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHandler.closeDB();
    }
}
