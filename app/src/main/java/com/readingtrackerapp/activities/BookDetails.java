package com.readingtrackerapp.activities;

import android.content.Intent;
import android.media.Rating;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.readingtrackerapp.R;
import com.readingtrackerapp.database.DBHandler;
import com.readingtrackerapp.model.Book;
import com.readingtrackerapp.model.Genre;

public class BookDetails extends AppCompatActivity {

    TextView bookTitle,bookAuthor,bookGenre,bookNumPages,progressBarText;
    RatingBar ratingBar;
    ProgressBar progressBar;
    String bookID;
    ListView listView;
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        this.setTitle("Book details");

        Intent intent=getIntent();
        if (!intent.getExtras().isEmpty()){
            bookID=intent.getStringExtra("BookID");
        }
        dbHandler=new DBHandler(this);

        setViews();
        bindData();

    }


    public void setViews()
    {
        bookTitle=(TextView) findViewById(R.id.book_title);
        bookAuthor=(TextView) findViewById(R.id.book_author);
        bookGenre=(TextView) findViewById(R.id.book_genre);
        bookNumPages=(TextView) findViewById(R.id.book_numPages);
        ratingBar=(RatingBar) findViewById(R.id.ratingBar);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBarText=(TextView) findViewById(R.id.progresTxt);

    }

    public void bindData() {
        Book book = dbHandler.getBookById(bookID);
        Genre genre = dbHandler.getGenreById(String.valueOf(book.getGenreId()));

        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthorName());
        bookGenre.setText(genre.getName());
        bookNumPages.setText(String.valueOf(book.getNumberOfPages()) + " pages");

        if (book.isForReading())
        {
            LinearLayout bookCooments=(LinearLayout) findViewById(R.id.book_comments_layout);
            bookCooments.setVisibility(View.INVISIBLE);
            progressBarText.setText("0% read");
            progressBar.setProgress(0);
        }

        if(book.isReadingCurrently())
        {

        }

    }

}
