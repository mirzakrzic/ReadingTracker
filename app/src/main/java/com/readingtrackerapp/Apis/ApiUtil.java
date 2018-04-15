package com.readingtrackerapp.Apis;

import android.net.Uri;
import android.util.Log;

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

