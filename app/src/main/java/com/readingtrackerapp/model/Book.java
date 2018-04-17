package com.readingtrackerapp.model;


import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.readingtrackerapp.R;
import com.squareup.picasso.Picasso;

public class Book implements Parcelable{

    private int id;
    public String title;
    public String subTitle;
    public int numberOfPages;
    public String authorName;
    private int genreId;
    private int rating;
    private boolean readingCurrently;
    private boolean alreadyRead;
    private boolean forReading;
    private String timeForNotification;
    private int numberOfReadPages;
    public String publisher;
    public String publishedDate;
    public String description;
    public String thumbnail;

    public Book(String title, String subTitle, int numberOfPages, String authorName, int rating, String publisher, String publishedDate, String description,String thumbnail) {
        this.title = title;
        this.subTitle = subTitle;
        this.numberOfPages = numberOfPages;
        this.authorName = authorName;
        this.rating = rating;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.thumbnail=thumbnail;
    }




    public Book(){}
    public Book(int id, String title,int numberOfPages, String authorName, int genreId, int rating, boolean readingCurrently, boolean alreadyRead, boolean forReading, String timeForNotification, int numberOfReadPages){
        this.id=id;
        this.title=title;
        this.numberOfPages=numberOfPages;
        this.authorName=authorName;
        this.genreId=genreId;
        this.rating=rating;
        this.readingCurrently=readingCurrently;
        this.alreadyRead=alreadyRead;
        this.forReading=forReading;
        this.timeForNotification=timeForNotification;
        this.numberOfReadPages=numberOfReadPages;
    }


    protected Book(Parcel in) {
        id = in.readInt();
        title = in.readString();
        subTitle = in.readString();
        numberOfPages = in.readInt();
        authorName = in.readString();
        genreId = in.readInt();
        rating = in.readInt();
        readingCurrently = in.readByte() != 0;
        alreadyRead = in.readByte() != 0;
        forReading = in.readByte() != 0;
        timeForNotification = in.readString();
        numberOfReadPages = in.readInt();
        publisher = in.readString();
        publishedDate = in.readString();
        description = in.readString();
        thumbnail = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    // id
    public int getId() {
        return id;
    }

    // title
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    // number of pages
    public int getNumberOfPages() {
        return numberOfPages;
    }
    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    // author name
    public String getAuthorName() {
        return authorName;
    }
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    // genre
    public int getGenreId() {
        return genreId;
    }
    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    // rating
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }

    //  is currently reading
    public boolean isReadingCurrently() {
        return readingCurrently;
    }
    public void setReadingCurrently(boolean readingCurrently) {
        this.readingCurrently = readingCurrently;
    }

    // is already read
    public boolean isAlreadyRead() {
        return alreadyRead;
    }
    public void setAlreadyRead(boolean alreadyRead) {
        this.alreadyRead = alreadyRead;
    }

    // is for reading
    public boolean isForReading() {
        return forReading;
    }
    public void setForReading(boolean forReading) {
        this.forReading = forReading;
    }

    // time for notification
    public String getTimeForNotification() {
        return timeForNotification;
    }
    public void setTimeForNotification(String timeForNotification) {
        this.timeForNotification = timeForNotification;
    }

    // number of read pages
    public int getNumberOfReadPages() {
        return numberOfReadPages;
    }
    public void setNumberOfReadPages(int numberOfReadPages) {
        this.numberOfReadPages = numberOfReadPages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(subTitle);
        parcel.writeInt(numberOfPages);
        parcel.writeString(authorName);
        parcel.writeInt(genreId);
        parcel.writeInt(rating);
        parcel.writeByte((byte) (readingCurrently ? 1 : 0));
        parcel.writeByte((byte) (alreadyRead ? 1 : 0));
        parcel.writeByte((byte) (forReading ? 1 : 0));
        parcel.writeString(timeForNotification);
        parcel.writeInt(numberOfReadPages);
        parcel.writeString(publisher);
        parcel.writeString(publishedDate);
        parcel.writeString(description);
        parcel.writeString(thumbnail);
    }

    @BindingAdapter({"android:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl)
    {
        if(!imageUrl.isEmpty()) {
            Picasso.with(view.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_book)
                    .into(view);
        }else
        {
            view.setBackgroundResource(R.drawable.ic_book);
        }
    }
}
