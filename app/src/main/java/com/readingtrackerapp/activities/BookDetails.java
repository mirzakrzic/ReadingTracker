package com.readingtrackerapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.media.Rating;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.readingtrackerapp.R;
import com.readingtrackerapp.adapters.CommentsListAdapter;
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
    CommentsListAdapter adapter;

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
        listView=(ListView) findViewById(R.id.book_comments);
    }

    public void bindData() {
        Book book = dbHandler.getBookById(bookID);
        Genre genre = dbHandler.getGenreById(String.valueOf(book.getGenreId()));

        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthorName());
        bookGenre.setText(genre.getName());
        bookNumPages.setText(String.valueOf(book.getNumberOfPages()) + " pages");

        if (book.isForReading()) // checking out if book is in ForReading list, so if it is we don't need comments
        {
            LinearLayout bookCooments=(LinearLayout) findViewById(R.id.book_comments_layout);
            bookCooments.setVisibility(View.INVISIBLE);
            progressBarText.setText("0% read");
            progressBar.setProgress(0);
        }
        if(book.isAlreadyRead())
        {
            progressBarText.setText("100% read");
            progressBar.setProgress(100);

            //setting rating
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
            ratingBar.setRating(book.getRating());
        }


        progressBar.getProgressDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setMax(100);

        // calculating percent of read pages
        int percentOfReadPages= (int)(book.getNumberOfReadPages()*100)/book.getNumberOfPages();

        // set percent to progres bar and progress text view
        progressBar.setProgress(percentOfReadPages);
        progressBarText.setText(percentOfReadPages+"% READ");

        adapter=new CommentsListAdapter(this,dbHandler.getCommentsForBook(bookID), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(adapter);
    }

}
