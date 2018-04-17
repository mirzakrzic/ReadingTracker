package com.readingtrackerapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.readingtrackerapp.R;
import com.readingtrackerapp.activities.BookDetailsOnline;
import com.readingtrackerapp.model.Book;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Adnan on 11.4.2018.
 */

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    ArrayList<Book> books;
    public BooksAdapter(ArrayList<Book> books){
        this.books=books;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context c=parent.getContext();
        View itemView= LayoutInflater.from(c).inflate(R.layout.book_list_item,parent,false);
        return new BookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book=books.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvTitle;
        TextView tvAuthors;
        TextView tvDate;
        TextView tvPublisher;

        public BookViewHolder(View itemView) {
            super(itemView);

            tvTitle=(TextView) itemView.findViewById(R.id.tvTitle);
            tvAuthors=(TextView) itemView.findViewById(R.id.tvAuthors);
            tvDate=(TextView) itemView.findViewById(R.id.tvPublishedDate);
            tvPublisher=(TextView) itemView.findViewById(R.id.tvPublisher);
            itemView.setOnClickListener(this);
        }

        public void bind(Book book)
        {
            tvTitle.setText(book.title);
            tvAuthors.setText(book.authorName);
            tvDate.setText(book.publishedDate);
            tvPublisher.setText(book.publisher);

        }

        @Override
        public void onClick(View view) {
            int position=getAdapterPosition(); // get position of book clicked by user
            Book selectedBook=books.get(position);
            Intent intent=new Intent(view.getContext(),BookDetailsOnline.class);
            intent.putExtra("book",selectedBook);
            view.getContext().startActivity(intent);
        }

    }

}