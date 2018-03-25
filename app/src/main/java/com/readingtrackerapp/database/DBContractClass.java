package com.readingtrackerapp.database;

import android.provider.BaseColumns;

/**
 * Created by Anes on 3/23/2018.
 */

public class DBContractClass {


    // USER TABLE
    public static final class USER implements BaseColumns{

        public static final String TABLE_NAME="user";

        public static final String COLUMN_ID=BaseColumns._ID;
        public static final String COLUMN_NAME="name";
        public static final String COLUMN_SURNAME="surname";
        public static final String COLUMN_REGISTRATION_DATE="registrationDate";
        public static final String COLUMN_READ_PAGES_NUMBER="readPagesNumber";
    }

    // MONTHLY GOAL
    public static final class MONTHLY_GOAL implements BaseColumns{

        public static final String TABLE_NAME="monthlyGoals";

        public static final String COLUMN_ID=BaseColumns._ID;
        public static final String COLUMN_NUMBER_OF_PAGES="numberOfPages";
        public static final String COLUMN_YEAR="year";
        public static final String COLUMN_MONTH="month";
    }

    // BOOKS TABLE
    public static final class BOOK implements BaseColumns{

        public static final String TABLE_NAME="books";

        public static final String COLUMN_ID=BaseColumns._ID;
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_NUMBER_OF_PAGES="numberOfPages";
        public static final String COLUMN_NUMBER_OF_READ_PAGES="numberOfReadPages";
        public static final String COLUMN_AUTHOR_NAME="author";
        public static final String COLUMN_GENRE_ID="genreId";
        public static final String COLUMN_RATING="rating";
        public static final String COLUMN_CURRENTLY_READING="currentlyOnReading";
        public static final String COLUMN_ALREADY_READ="alreadyRead";
        public static final String COLUMN_FOR_READING="forReading";
        public static final String COLUMN_NOTIFICATION_TIME="notificationTime";

    }

    // READING EVIDENTION TABLE
    public static final class READING_EVIDENTION implements BaseColumns{

        public static final String TABLE_NAME="readingEvidentions";

        public static final String COLUMN_ID=BaseColumns._ID;
        public static final String COLUMN_BOOK_ID="bookId";
        public static final String COLUMN_NUMBER_OF_READ_PAGES="readPagesNumber";
        public static final String COLUMN_YEAR="year";
        public static final String COLUMN_MONTH="month";
        public static final String COLUMN_DAY="day";
    }

    // GENRES TABLE
    public static final class GENRE implements BaseColumns{

        public static final String TABLE_NAME="genres";

        public static final String COLUMN_ID=BaseColumns._ID;
        public static final String COLUMN_NAME="name";

    }

    // COMMENTS TABLE
    public static final class COMMENT implements BaseColumns{

        public static final String TABLE_NAME="comments";

        public static final String COLUMN_ID=BaseColumns._ID;
        public static final String COLUMN_BOOK_ID="bookId";
        public static final String COLUMN_COMMENT="comment";
        public static final String COLUMN_DATE="date";


    }

}
