package com.readingtrackerapp.model;

import android.os.Parcel;

public class Book {

    private int id;
    private String title;
    private int numberOfPages;
    private String authorName;
    private int genreId;
    private int rating;
    private boolean readingCurrently;
    private boolean alreadyRead;
    private boolean forReading;
    private String timeForNotification;
    private int numberOfReadPages;

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
}
