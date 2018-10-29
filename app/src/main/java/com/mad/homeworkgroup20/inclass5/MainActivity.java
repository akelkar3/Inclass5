/*
a. Assignment: In Class 5.
b. File Name: InClass05_Group20
c. Full name of all students in your group: Shubhra Mishra , Ankit Kelkar*/
package com.mad.homeworkgroup20.inclass5;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements  getAsyncImage.IData {
 ImageView gal=null;
 String TAG = "inclass";
 int current;
    String keywords[];
    ArrayList<String> imageUrls = new ArrayList<String>();
    ImageButton next;
    ImageButton prev;
    TextView tvKeyword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Main Activity");
     next = (ImageButton)findViewById(R.id.btnnext);
     prev = (ImageButton)findViewById(R.id.btnprev);
        next.setEnabled(false);
        prev.setEnabled(false);
        next.setAlpha(0.4f);
        prev.setAlpha(0.4f);
        gal= (ImageView) findViewById(R.id.gallary);;
        tvKeyword=findViewById(R.id.keyword);
        tvKeyword.setFocusable(false);
        tvKeyword.setEnabled(false);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()) {
                    current= (current+1) % imageUrls.size();
                    new getAsyncImage(MainActivity.this, MainActivity.this).execute(imageUrls.get(current));
                } else {
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()) {
                    current= (current-1 +imageUrls.size()) % imageUrls.size();
                    new getAsyncImage(MainActivity.this, MainActivity.this).execute(imageUrls.get(current));
                } else {
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.btnGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick");
                new GetSimpleAsync().execute("http://dev.theappsdr.com/apis/photos/keywords.php");
            }
        });

    }
    @Override
    public void handleImage(Bitmap data) {
        Log.d(TAG, "imagedisplay");
        gal.setImageBitmap(data);
    }
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }


    private class GetSimpleAsync extends AsyncTask<String, Void, String> {
        String line="";
        @Override
            protected String doInBackground(String... params) {
            StringBuilder stringBuilder = new StringBuilder();
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String result = null;
            Log.d(TAG, "back");
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    while ((line = reader.readLine()) != null) {
                        if(params.length>1){
                            imageUrls.add(line);
                        }else {
                            stringBuilder.append(line);
                        }
                    }
                    result = stringBuilder.toString();

                }
                //Splitting the string
if (!result.isEmpty() && params.length==1){
                keywords= result.split(";");
                for (String temp: keywords) {
                    System.out.println(temp);
                    Log.d(TAG, temp);
                }
            }else {
            result="imageURL";
            }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            return result;
        }

       AlertDialog dialog;
        @Override
        protected void onPostExecute(String result) {

            if (result.equalsIgnoreCase("imageURL")){
                if ( imageUrls.size()>0){
                    new getAsyncImage(MainActivity.this, MainActivity.this).execute(imageUrls.get(current));
                    next.setEnabled(true);
                    prev.setEnabled(true);
                    next.setAlpha(1.0f);
                    prev.setAlpha(1.0f);
                }else{
                    Toast.makeText(MainActivity.this, "No Images Found", Toast.LENGTH_SHORT).show();
                    next.setEnabled(false);
                    next.setAlpha(0.4f);
                    prev.setAlpha(0.4f);
                    prev.setEnabled(false);

                    gal.setImageBitmap(null);

                }

            }else {
                final String[] generatedKeywords = keywords;
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setItems(generatedKeywords, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                        dialog.cancel();
                        Log.d(TAG, generatedKeywords[i]);
                        tvKeyword.setFocusable(true);
                        tvKeyword.setEnabled(true);
                        tvKeyword.setText( generatedKeywords[i]);
                        current = 0;
                        imageUrls.clear();
                        new GetSimpleAsync().execute("http://dev.theappsdr.com/apis/photos/index.php?keyword=" + generatedKeywords[i], "imageUrls");
                    }
                });
                dialog = builder.create();
                dialog.show();

            }

              if (result != null) {
                Log.d(TAG, result);
            } else {
                Log.d(TAG, "null result");
            }
        }
    }
}


