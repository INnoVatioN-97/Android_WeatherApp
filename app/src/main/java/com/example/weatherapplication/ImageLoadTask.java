package com.example.weatherapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class ImageLoadTask extends AsyncTask<String, Void, Bitmap> {

    private String urlStr;
    private ImageView imageView;
    private static HashMap<String, Bitmap> bitmapHash = new HashMap<String, Bitmap>();

    public ImageLoadTask(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap bmp = null;
        try {
            String img_url = strings[0]; //url of the image
            URL url = new URL(img_url);
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmp;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected void onPostExecute(Bitmap result) {
        // doInBackground 에서 받아온 total 값 사용 장소
        imageView.setImageBitmap(result);
    }
}