package com.mad.homeworkgroup20.inclass5;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Ankit Kelkar on 2/12/2018.
 */

public class getAsyncImage extends AsyncTask<String, Void,Bitmap>
{ IData iData;
    Bitmap result=null;
    HttpURLConnection connection=null;
    private ProgressDialog dialog;

    public getAsyncImage(IData idata, MainActivity activity) {
        this.iData = idata;
this.dialog= new ProgressDialog(activity);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            Log.d("inclass", "connection open");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.d("inclass", "result");
                result = BitmapFactory.decodeStream(connection.getInputStream());
                Log.d("inclass", "result 2");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("inclass", e.getMessage());
        } finally {
            //Close open connections and reader
            if (connection != null) {
                connection.disconnect();
            }

        }
        return result;
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Loading");
        dialog.show();
    }

    @Override
    protected void onPostExecute(Bitmap result) {
      //  ImageView iv=(ImageView)findViewById(R.id.imageView);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if(result!=null)
        {
          //  iv.setImageBitmap(bitmap);
            Log.d("inclass", "inpost");
            if (iData!=null) {
                iData.handleImage(result);
            }
        }
    }
    public static  interface IData{
        public void handleImage(Bitmap data);
    }
}