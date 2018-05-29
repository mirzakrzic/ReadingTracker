package com.readingtrackerapp.Apis;

import android.net.Uri;
import android.util.Log;

import com.readingtrackerapp.model.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class ApiUtil {

    private ApiUtil(){}

    public static final String BASE_API_URL = "https://www.googleapis.com/books/v1/volumes";
    public static final String QUERY_PARAMETER_KEY="q";
    public static final String APIKEY="AIzaSyAESfI8DnQ8jx_IrzT8dYoJzoFdIBDyN7A";
    public static final String KEY="key";
    public static final String TITLE="intitle:";
    public static final String AUTHOR="inauthor:";
    public static final String PUBLISHER="inpublisher:";
    public static final String ISBN="isbn:";


    public static URL buildUrl(String title)
    {

        URL url = null;
        Uri uri=Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY,title)
                .appendQueryParameter(KEY,APIKEY)
                .build();
        try {
            url = new URL(uri.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return url;

    }

    public static URL buildURL(String title,String author,String isbn,String publisher)
    {
        URL url = null;
        StringBuilder sb = new StringBuilder();

        if (!title.isEmpty()) sb.append(TITLE + title + "+");
        if (!author.isEmpty())  sb.append(AUTHOR+ author + "+");
        if (!publisher.isEmpty())  sb.append(PUBLISHER + publisher + "+");
        if (!isbn.isEmpty())  sb.append(ISBN + isbn + "+");
        //removes the last character
        sb.setLength(sb.length() - 1);
        String query = sb.toString();
        Uri uri = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY, query)
                .appendQueryParameter(KEY, APIKEY)
                .build();
        try {
            url = new URL(uri.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return url;
    }

    public static ArrayList<Book> getBooksFromJSON(String json){
        final String TITLE = "title";
        final String SUBTITLE = "subtitle";
        final String AUTHORS = "authors";
        final String PUBLISHER = "publisher";
        final String PUBLISHED_DATE="publishedDate";
        final String ITEMS = "items";
        final String VOLUMEINFO = "volumeInfo";
        final String DESCRIPTION = "description";
        final String IMAGEINFO = "imageLinks";
        final String THUMBNAIL = "thumbnail";
        final String PAGECOUNT="pageCount";
        final String RATINGSCOUNT="ratingsCount";


        ArrayList<Book> books = new ArrayList<Book>();
        try {
            JSONObject jsonBooks = new JSONObject(json);
            JSONArray arrayBooks = jsonBooks.getJSONArray(ITEMS);
            int numberOfBooks = arrayBooks.length();
            for (int i =0; i<numberOfBooks;i++){
                JSONObject bookJSON = arrayBooks.getJSONObject(i);
                JSONObject volumeInfoJSON =
                        bookJSON.getJSONObject(VOLUMEINFO);
                JSONObject imageLinksJSON=null;
                //qui problema
                if (volumeInfoJSON.has(IMAGEINFO)) {
                    imageLinksJSON = volumeInfoJSON.getJSONObject(IMAGEINFO);
                }
                int authorNum;
                try {
                    authorNum = volumeInfoJSON.getJSONArray(AUTHORS).length();
                }
                catch (Exception e) {
                    authorNum = 0;
                }

                String authors ="";
                if(authorNum==1){
                    authors+=volumeInfoJSON.getJSONArray(AUTHORS).get(0);
                }
                else {
                    int before_last=0;
                    for (int j = 0; j < authorNum; j++) {
                        if (before_last==authorNum-2)
                        {
                            authors = authors + volumeInfoJSON.getJSONArray(AUTHORS).get(j).toString();
                        }

                        authors = authors + volumeInfoJSON.getJSONArray(AUTHORS).get(j).toString() + ", ";
                        before_last++;
                    }
                }
                Book book = new Book(
                        volumeInfoJSON.getString(TITLE),
                        (volumeInfoJSON.isNull(SUBTITLE)?"":volumeInfoJSON.getString(SUBTITLE)),
                        (volumeInfoJSON.isNull(PAGECOUNT))?0:volumeInfoJSON.getInt(PAGECOUNT),
                        authors,
                        (volumeInfoJSON.isNull(RATINGSCOUNT))?0:volumeInfoJSON.getInt(RATINGSCOUNT),
                        (volumeInfoJSON.isNull(PUBLISHER)?"":volumeInfoJSON.getString(PUBLISHER)),
                        (volumeInfoJSON.isNull(PUBLISHED_DATE)?"":volumeInfoJSON.getString(PUBLISHED_DATE)),
                        (volumeInfoJSON.isNull(DESCRIPTION)?"":volumeInfoJSON.getString(DESCRIPTION)),
                        (imageLinksJSON==null)?"":imageLinksJSON.getString(THUMBNAIL));
                books.add(book);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return books;
    }

    public static String getJSON(URL url) throws IOException
    {
        HttpURLConnection connection=(HttpURLConnection) url.openConnection();
        try {
            InputStream stream=connection.getInputStream();
            Scanner scanner=new Scanner(stream);
            scanner.useDelimiter("\\A");
            boolean hasData = scanner.hasNext();

            if (hasData) {
                return scanner.next();
            } else {
                return null;
            }
        }catch (Exception e)
        {
            Log.e("Error",e.toString());
            return null;
        }
        finally {
            connection.disconnect();
        }

    }


}

