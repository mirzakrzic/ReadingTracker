package com.readingtrackerapp.Apis;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.util.Scanner;

public class ApiUtil {

    public static final String BASE_API_URL="https://wwww.googleapis.com/books/v1/volumes";
    public static final String QUERY_PARAMETER_KEY="q";
    public static final String APIKEY="AIzaSyAESfI8DnQ8jx_IrzT8dYoJzoFdIBDyN7A";
    public static final String KEY="key";

    private ApiUtil(){}

    public static URL buildUrl(String title)
    {
        URL url=null;
        Uri uri=Uri.parse(BASE_API_URL  )
                .buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY,title)
                .appendQueryParameter(KEY,APIKEY)
                .build();

        try{
            url=new URL(uri.toString());
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return url;
    }

    public static String getJSON(URL url) throws IOException{
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try{
            InputStream stream=connection.getInputStream();
            Scanner scanner=new Scanner(stream);
            scanner.useDelimiter("\\A");
            boolean hasData=scanner.hasNext();

            if (hasData){
                return scanner.next();
            }
            else{
                return null;
            }
        }catch (Exception e)
        {
            Log.e("Error",e.toString());
            return null;
        }finally
        {
            connection.disconnect();
        }
    }

}
