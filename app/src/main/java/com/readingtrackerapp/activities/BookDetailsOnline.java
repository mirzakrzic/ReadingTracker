package com.readingtrackerapp.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.readingtrackerapp.R;
import com.readingtrackerapp.databinding.ActivityBookDetailsOnlineBinding;
import com.readingtrackerapp.model.Book;



public class BookDetailsOnline extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details_online);


        final Book book=getIntent().getParcelableExtra("book");
        ActivityBookDetailsOnlineBinding binding=DataBindingUtil.setContentView(this,R.layout.activity_book_details_online);
        binding.setBook(book);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), AddNewBookActivity.class);
                intent.putExtra("book",book);
                startActivity(intent);
            }
        });


    }
}
