package com.example.bojie.blogreader;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainListActivity extends ListActivity {

    protected String[] mBlogPostTitles;
    public static final int NUMBER_OF_POSTS = 20;
    public static final String TAG = MainListActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        if (isNetworkAvailable()) {
            GetBlogPostsTask getBlogPostsTask = new GetBlogPostsTask();
            getBlogPostsTask.execute();
            //Toast.makeText(this, getString(R.string.no_items),Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Network is unavailable!", Toast.LENGTH_LONG).show();
        }

    }
    private boolean isNetworkAvailable () {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable=false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable=true;
        }

        return isAvailable;
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_list, menu);
        return true;
    }

    private class GetBlogPostsTask extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object[] objects) {
            int responseCode = -1;

            try {
                URL blogFeedUrl = new URL(
                        "http://blog.teamtreehouse.com/api/get_recent_summary/?count"
                                + NUMBER_OF_POSTS);
                HttpURLConnection connection = (HttpURLConnection) blogFeedUrl.openConnection();
                connection.connect();

                //Creating are response code so that we can get data from the internet
                responseCode = connection.getResponseCode();
                if(responseCode==HttpURLConnection.HTTP_OK){
                    InputStream inputStream = connection.getInputStream(); //store the data into the input stream
                    Reader reader = new InputStreamReader(inputStream);//read the input stream
                    int contentLength = connection.getContentLength();//get the number of characters to read in
                    char [] charArray = new char[contentLength];//create the char array to store the the data
                    reader.read(charArray); //read and store the data array into the char array
                    String responseData = new String(charArray);//create a new string and convert and store from char to string

                    //This creates are Json object
                    JSONObject jSonResponse = new JSONObject(responseData);
                    String status = jSonResponse.getString("status");
                    Log.v(TAG, status);

                    //Creat's an array so we can store are blog posts
                    JSONArray jSonPost = jSonResponse.getJSONArray("posts");
                    //A for loop so are post can run simitanously untill conditions are met.
                    for (int i =0; i < jSonPost.length(); i++){
                        JSONObject jsonPost = jSonPost.getJSONObject(i);
                        String title = jsonPost.getString("title");
                        Log.v(TAG, "posts " + i + ": " + title);

                    }
                }
                else{
                    Log.i(TAG, "Unsuccessful HTTP Response Code: " + responseCode);
                }
                Log.i(TAG, "Code: " + responseCode);
            } catch (MalformedURLException e) {
                //e.printStackTrace();
                Log.e(TAG, "Exception caught: ", e);
            } catch (IOException e) {
                Log.e(TAG, "Exception caught: ", e);

            } catch (Exception e) {
                Log.e(TAG, "Exception caught: ", e);
            }
            return "code: " + responseCode;
        }
    }
}